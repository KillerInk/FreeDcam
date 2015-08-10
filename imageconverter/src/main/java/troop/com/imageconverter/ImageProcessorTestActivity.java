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

import troop.com.views.MyHistogram;


/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorTestActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    SurfaceView surfaceView;
    Camera camera;

    ImageProcessorWrapper imageProcessor;
    int w,h;

    Button buttonProcessFrame;
    ImageView maskImageView;
    MyHistogram histogram;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageprocessortestactivity);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView_camera);
        surfaceView.getHolder().addCallback(this);
        maskImageView = (ImageView)findViewById(R.id.imageView);
        buttonProcessFrame = (Button)findViewById(R.id.button_processFrame);
        buttonProcessFrame.setOnClickListener(processFrameClick);
        imageProcessor = new ImageProcessorWrapper();
        histogram = (MyHistogram)findViewById(R.id.Histogram);
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
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.setPreviewCallback(null);
        Date startDate = new Date();
        imageProcessor.Init();
        imageProcessor.ProcessFrame(data.clone(), w, h);
        long ti = new Date().getTime() - startDate.getTime();
        Log.d("ImageProcessor", "YuvtoRgbTIme" + ti);
        //int[][] hist = imageProcessor.GetHistogramData();
        //histogram.SetRgbArrays(hist[0], hist[1], hist[2]);
        /*Bitmap obit = imageProcessor.GetNativeBitmap();
        saveBitmap(obit, "orginalHPFFrame.jpg");
        */
        //int[] jnidata = imageProcessor.GetPixelData();
        //Bitmap map = Bitmap.createBitmap(jnidata, w,h, Bitmap.Config.ARGB_8888);
        //map.setHasAlpha(true);
        imageProcessor.ApplyHPF();
        ti = new Date().getTime() - startDate.getTime();
        Log.d("ImageProcessor", "HPF time" + ti);
        Bitmap map = imageProcessor.GetNativeBitmap();
        //saveBitmap(map, "testHPFFrame.jpg");


        maskImageView.setImageBitmap(map);
        imageProcessor.ReleaseNative();
        ti = new Date().getTime() - startDate.getTime();
        Log.d("ImageProcessor", "ImageDrawn" + ti);
    }

    OnClickListener processFrameClick = new OnClickListener()
    {

        @Override
        public void onClick(View v) {
            camera.setPreviewCallback(ImageProcessorTestActivity.this);
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
