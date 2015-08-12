package troop.com.imageconverter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.views.MyHistogram;


/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorTestActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    SurfaceView surfaceView;
    Camera camera;
    SurfaceView nativeSurface;


    int w,h;

    Button buttonProcessFrame;
    ImageView maskImageView;
    MyHistogram histogram;

    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    private final BlockingQueue<int[]> mFocusPeakQueue = new ArrayBlockingQueue<int[]>(2);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageprocessortestactivity);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView_camera);
        surfaceView.getHolder().addCallback(this);
        maskImageView = (ImageView)findViewById(R.id.imageView);
        buttonProcessFrame = (Button)findViewById(R.id.button_processFrame);
        buttonProcessFrame.setOnClickListener(processFrameClick);
        histogram = (MyHistogram)findViewById(R.id.Histogram);
        nativeSurface = (SurfaceView)findViewById(R.id.surfaceView_focusPeak);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open(0);
        Camera.Parameters parameters = camera.getParameters();
        //parameters.setPreviewSize(640,480);
        parameters.set("preview-format","yuv420sp");
        camera.setParameters(parameters);
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());

        } catch (IOException e) {
            e.printStackTrace();
        }
        w = camera.getParameters().getPreviewSize().width;
        h = camera.getParameters().getPreviewSize().height;
        camera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
    {
        if (mYuvFrameQueue.size() == 2)
        {
            mYuvFrameQueue.remove();
        }
        mYuvFrameQueue.add(data);

        /*camera.setPreviewCallback(null);
        ImageProcessorWrapper imageProcessor = new ImageProcessorWrapper();
        imageProcessor.ProcessFrame(data.clone(), w, h);
        imageProcessor.ApplyHPF();
        maskImageView.setImageBitmap(Bitmap.createBitmap(imageProcessor.GetPixelData(), w, h, Bitmap.Config.ARGB_8888));*/
    }
    boolean doWork = false;
    private void start()
    {
        final ImageProcessorWrapper imageProcessor = new ImageProcessorWrapper();
        imageProcessor.SetSurface(nativeSurface);
        doWork = true;
        new Thread() {
            @Override
            public void run() {
                byte[] data = null;
                try {

                    //imageProcessor.Init();
                    while (doWork) {
                        data = mYuvFrameQueue.take();
                        if (data != null)
                        {
                            imageProcessor.ProcessFrame(data, w, h);
                            imageProcessor.ApplyHPF();
                            /*if (mFocusPeakQueue.size() == 2)
                            {
                                mFocusPeakQueue.remove();
                            }
                            mFocusPeakQueue.add(imageProcessor.GetPixelData());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        maskImageView.setImageBitmap(Bitmap.createBitmap(mFocusPeakQueue.take(), w,h, Bitmap.Config.ARGB_8888));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });*/
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    camera.setPreviewCallback(null);
                    mYuvFrameQueue.clear();
                    mFocusPeakQueue.clear();
                    doWork = false;
                    imageProcessor.ReleaseNative();

                }
            }
        }.start();
    }

    private void stop()
    {
        doWork = false;
    }

    private void ProcessOnce()
    {
        new Thread() {
            @Override
            public void run() {
                byte[] data = null;
                try {
                    final ImageProcessorWrapper imageProcessor = new ImageProcessorWrapper();
                    imageProcessor.Init();

                        data = mYuvFrameQueue.take();
                        if (data != null)
                        {
                            Log.d("ImageProcessor", "ProcessFrame");
                            imageProcessor.ProcessFrame(data.clone(), w, h);
                            Log.d("ImageProcessor", "ProcessedFrame");
                            imageProcessor.ApplyHPF();
                            Log.d("ImageProcessor", "HPF done");
                            if (mFocusPeakQueue.size() == 2)
                            {
                                mFocusPeakQueue.remove();
                            }
                            mFocusPeakQueue.add(imageProcessor.GetPixelData().clone());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        maskImageView.setImageBitmap(Bitmap.createBitmap(mFocusPeakQueue.take(), w,h, Bitmap.Config.ARGB_8888));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    camera.setPreviewCallback(null);
                    mYuvFrameQueue.clear();
                    mFocusPeakQueue.clear();
                    doWork = false;

                }
            }
        }.start();
    }

    OnClickListener processFrameClick = new OnClickListener()
    {

        @Override
        public void onClick(View v)
        {
            //camera.setPreviewCallback(ImageProcessorTestActivity.this);
            if (!doWork) {
                camera.setPreviewCallback(ImageProcessorTestActivity.this);
                start();
                //ProcessOnce();
            }
            else
                stop();
        }
    };


    private void saveBitmap(Bitmap bimap, String filename)
    {
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path, filename); // the File to save to
        try {
            fOut = new FileOutputStream(file);
            bimap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
            fOut.flush();
            fOut.close(); // do not forget to close the stream
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
