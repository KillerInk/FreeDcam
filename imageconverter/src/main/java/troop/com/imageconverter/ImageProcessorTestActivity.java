package troop.com.imageconverter;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by troop on 08.08.2015.
 */
public class ImageProcessorTestActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    SurfaceView surfaceView;
    Camera camera;

    ImageProcessor imageProcessor;
    int w,h;

    Button buttonProcessFrame;
    ImageView maskImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        surfaceView = (SurfaceView)findViewById(R.id.surfaceView_camera);
        surfaceView.getHolder().addCallback(this);
        maskImageView = (ImageView)findViewById(R.id.imageView);
        buttonProcessFrame = (Button)findViewById(R.id.button_processFrame);
        buttonProcessFrame.setOnClickListener(processFrameClick);
        imageProcessor = new ImageProcessor();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open(0);
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(640,480);
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
        imageProcessor.ProcessFrame(data, w, h);
        maskImageView.setImageBitmap(imageProcessor.GetNativeBitmap());
        imageProcessor.ReleaseNative();
    }

    OnClickListener processFrameClick = new OnClickListener()
    {

        @Override
        public void onClick(View v) {
            camera.setPreviewCallback(ImageProcessorTestActivity.this);
        }
    };
}
