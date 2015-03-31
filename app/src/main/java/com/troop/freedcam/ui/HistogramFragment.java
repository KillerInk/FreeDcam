package com.troop.freedcam.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by George on 3/26/2015.
 */
public class HistogramFragment extends Fragment implements I_Callbacks.PreviewCallback, I_ModuleEvent, I_CameraChangedListner, AbstractModuleHandler.I_worker {

    private AppSettingsManager appSettingsManager;
    AbstractCameraUiWrapper cameraUiWrapper;
    boolean fragmentloaded = false;
    View view;
    MyHistogram histogram;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    LinearLayout ll;
    I_Activity i_activity;

    boolean doWork = false;
    boolean stoppedOnModuleChange = false;

    int width;
    int height;




    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.histogram_fragment, container, false);
        ll = (LinearLayout)view.findViewById(R.id.histoOverlay);
        histogram = new MyHistogram(container.getContext());
        ll.addView(histogram);
        fragmentloaded = true;
        return view;
    }


    public void SetAppSettings(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = i_activity;
    }


    private void extactMutable(byte[] PreviewFrame)
    {

        final YuvImage yuvImage = new YuvImage(PreviewFrame, ImageFormat.NV21,width,height,null);
        if (!doWork)
            return;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        yuvImage.compressToJpeg(new Rect(0,0,width,height),50,byteArrayOutputStream);

        if (!doWork)
            return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        options.inMutable = true;
        //options.inJustDecodeBounds = true;
        options.inDither = false ; // Disable Dithering mode
        options.inPurgeable = true ; // Tell to gc that whether it needs free memory, // the Bitmap can be cleared
        options.inInputShareable = true;
        histogram.setBitmap(BitmapFactory.decodeByteArray(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size(), options));

    }



    @Override
    public void onPreviewFrame(final byte[] data)
    {
        if (mYuvFrameQueue.size() == 2)
        {
            mYuvFrameQueue.remove();
        }
        mYuvFrameQueue.add(data);
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        if (cameraUiWrapper == null)
            return;
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        cameraUiWrapper.SetCameraChangedListner(this);


    }
    public void strtLsn()
    {
        String[] split = cameraUiWrapper.camParametersHandler.PreviewSize.GetValue().split("x");
        width = Integer.parseInt(split[0]);
        height = Integer.parseInt(split[1]);
        if (cameraUiWrapper != null && cameraUiWrapper.cameraHolder != null && cameraUiWrapper.cameraHolder.isPreviewRunning)
        {
            try {
                cameraUiWrapper.cameraHolder.SetPreviewCallback(this);
            }
            catch (java.lang.RuntimeException ex)
            {
                ex.printStackTrace();
                return;
            }

        }
        else return;
        doWork = true;
        new Thread() {
            @Override
            public void run()
            {
                byte[] data = null;
                try {
                    while (doWork)
                    {
                        data = mYuvFrameQueue.take();
                        if (data != null)
                            extactMutable(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally
                {
                    mYuvFrameQueue.clear();
                    doWork = false;
                }
            }
        }.start();


    }

    void stopLsn()
    {
        doWork = false;
        if (cameraUiWrapper != null && cameraUiWrapper.cameraHolder != null)
            cameraUiWrapper.cameraHolder.SetPreviewCallback(null);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public String ModuleChanged(String module)
    {


        return null;
    }

    @Override
    public void onCameraOpen(String message) {

    }

    @Override
    public void onCameraOpenFinish(String message) {

    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message)
    {
        if (!doWork)
            strtLsn();
    }

    @Override
    public void onPreviewClose(String message)
    {
        if (doWork)
            stopLsn();
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module) {

    }

    @Override
    public void onWorkStarted()
    {
        if (doWork) {
            stopLsn();
        }
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        if (stoppedOnModuleChange)
        {
            stoppedOnModuleChange = false;
            strtLsn();

        }
    }


    public class MyHistogram extends View {

        public MyHistogram(Context context) {
            super(context);

        }
        Paint mPaint = new Paint ();
        int [] redHistogram = new int [ 256 ];
        int [] greenHistogram = new int [ 256 ];
        int [] blueHistogram = new int [ 256 ];
        Path mHistoPath = new Path ();


       class ComputeHistogramTask extends AsyncTask < Bitmap , Void , int []> {
           @Override
           protected int [] doInBackground ( Bitmap ... params ) {

               int [] histo = new int [ 256 * 3 ];
               Bitmap bitmap = params [ 0 ];
               //System.out.println("Histogram Async "+bitmap.getByteCount());
               int w = bitmap . getWidth ();
               int h = bitmap . getHeight ();
               int [] pixels = new int [ w * h ];
               bitmap . getPixels( pixels , 0 , w , 0 , 0 , w , h );
               for ( int i = 0 ; i < w ; i ++) {
                   for ( int j = 0 ; j < h ; j ++) {
                       int index = j * w + i ;
                       int r = Color . red ( pixels [ index ]);
                       int g = Color . green ( pixels [ index ]);
                       int b = Color . blue ( pixels [ index ]);
                       histo [ r ]++;
                       histo [ 256 + g ]++;
                       histo [ 512 + b ]++;
                   }
               }
               return histo ;
           }

           @Override
           protected void onPostExecute ( int [] result ) {
               //System.out.println("Histogram Async Post " +result.length);
               System . arraycopy( result , 0 , redHistogram , 0 , 256 );
               System . arraycopy( result , 256 , greenHistogram , 0 , 256 );
               System . arraycopy( result , 512 , blueHistogram , 0 , 256 );
               invalidate ();
               //System.out.println("Histogram Draw");
           }
       }

        private void createHistogramm(Bitmap bitmap)
        {
            if(bitmap == null)
                return;
            int [] histo = new int [ 256 * 3 ];
            int w = bitmap . getWidth ();
            int h = bitmap . getHeight ();
            int [] pixels = new int [ w * h ];
            bitmap . getPixels( pixels , 0 , w , 0 , 0 , w , h );
            for ( int i = 0 ; i < w ; i ++) {
                for ( int j = 0 ; j < h ; j ++) {
                    int index = j * w + i ;
                    int r = Color . red ( pixels [ index ]);
                    int g = Color . green ( pixels [ index ]);
                    int b = Color . blue ( pixels [ index ]);
                    histo [ r ]++;
                    histo [ 256 + g ]++;
                    histo [ 512 + b ]++;
                }
            }
            System . arraycopy( histo , 0 , redHistogram , 0 , 256 );
            System . arraycopy( histo , 256 , greenHistogram , 0 , 256 );
            System . arraycopy( histo , 512 , blueHistogram , 0 , 256 );
            this.post(new Runnable() {
                @Override
                public void run() {
                    invalidate();
                }
            });
            bitmap.recycle();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

       public void setBitmap ( Bitmap bitmap )
       {

           //System.out.println("Histogram SetBitmap " + mBitmap.getByteCount());
           createHistogramm(bitmap);

       }

       private void drawHistogram ( Canvas canvas , int [] histogram , int color , PorterDuff . Mode mode ) {
           int max = 0 ;
           //System.out.println("Histogram drawin");
           for ( int i = 0 ; i < histogram . length ; i ++) {
               if ( histogram [ i ] > max ) {
                   max = histogram [ i ];
               }
           }
           float w = getWidth (); // - Spline.curveHandleSize();
           float h = getHeight (); // - Spline.curveHandleSize() / 2.0f;
           float dx = 0 ; // Spline.curveHandleSize() / 2.0f;
           float wl = w / histogram . length ;
           float wh = h / max ;

           mPaint . reset ();
           mPaint . setAntiAlias ( true );
           mPaint . setARGB ( 100 , 255 , 255 , 255 );
           mPaint . setStrokeWidth (( int ) Math . ceil ( wl ));

// Draw grid
           mPaint . setStyle ( Paint . Style . STROKE );
           canvas . drawRect ( dx, 0 , dx + w , h , mPaint );
           canvas . drawLine ( dx + w / 3 , 0 , dx + w / 3 , h , mPaint );
           canvas . drawLine ( dx + 2 * w / 3 , 0 , dx + 2 * w / 3 , h , mPaint );

           mPaint . setStyle ( Paint . Style . FILL );
           mPaint . setColor ( color );
           mPaint . setStrokeWidth ( 6 );
           mPaint . setXfermode ( new PorterDuffXfermode( mode ));
           mHistoPath . reset ();
           mHistoPath . moveTo ( dx , h );
           boolean firstPointEncountered = false ;
           float prev = 0 ;
           float last = 0 ;
           for ( int i = 0 ; i < histogram . length ; i ++) {
               float x = i * wl + dx ;
               float l = histogram [ i ] * wh ;
               if ( l != 0 ) {
                   float v = h - ( l + prev ) / 2.0f ;
                   if (! firstPointEncountered ) {
                       mHistoPath . lineTo ( x , h );
                       firstPointEncountered = true ;
                   }
                   mHistoPath . lineTo ( x , v );
                   prev = l ;
                   last = x ;
               }
           }
           mHistoPath . lineTo ( last , h );
           mHistoPath . lineTo ( w , h );
           mHistoPath . close ();
           canvas . drawPath ( mHistoPath , mPaint );
           mPaint . setStrokeWidth ( 2 );
           mPaint . setStyle ( Paint . Style . STROKE );
           mPaint . setARGB( 255 , 200 , 200 , 200 );
           canvas . drawPath ( mHistoPath , mPaint );
       }


       public void onDraw ( Canvas canvas ) {
           canvas . drawARGB ( 0 , 0 , 0 , 0 );
           drawHistogram ( canvas , redHistogram , Color . RED , PorterDuff . Mode . SCREEN );
           drawHistogram ( canvas , greenHistogram , Color . GREEN , PorterDuff . Mode . SCREEN );
           drawHistogram ( canvas , blueHistogram , Color . BLUE , PorterDuff . Mode . SCREEN );
          // this.canvasx = canvas;
       }


   }
}







