package troop.com.imageconverter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.views.MyHistogram;

/**
 * Created by troop on 18.08.2015.
 */
public class RenderScriptTestActivity extends Activity implements SurfaceHolder.Callback, Camera.PreviewCallback
{
    SurfaceView surfaceView;
    Camera camera;
    SurfaceView nativeSurface;

    NativeDrawView drawView;
    int w,h;

    Button buttonProcessFrame;
    ImageView maskImageView;
    MyHistogram histogram;

    RenderScript mRS;
    private Allocation mInputAllocation;
    private Allocation mOutputAllocation;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    private ScriptC_focus_peak mScriptFocusPeak;
    Bitmap drawBitmap;

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
        drawView = (NativeDrawView)findViewById(R.id.view_to_draw);
        mRS = RenderScript.create(this.getApplicationContext());

    }

    private void initRenderScript()
    {
        drawBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Type.Builder tbIn = new Type.Builder(mRS, Element.U8(mRS));
        tbIn.setX(w);
        tbIn.setY(h);
        tbIn.setYuvFormat(ImageFormat.NV21);

        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(w);
        tbOut.setY(h);

        mInputAllocation = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
        mOutputAllocation = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
        mScriptFocusPeak = new ScriptC_focus_peak(mRS);
        maskImageView.setImageBitmap(drawBitmap);
    }

    boolean dowork = false;
    View.OnClickListener processFrameClick = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (!dowork)
            {
                camera.setPreviewCallback(RenderScriptTestActivity.this);
                start();
            }
            else stop();

        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        camera = Camera.open(0);
        Camera.Parameters parameters = camera.getParameters();
        //parameters.setPreviewSize(640,480);
        parameters.set("preview-format", "yuv420sp");
        camera.setParameters(parameters);
        Camera.Size size = parameters.getPreviewSize();
        this.w = size.width;
        this.h = size.height;
        try {

            camera.setPreviewDisplay(surfaceView.getHolder());

        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
        initRenderScript();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (dowork)
            stop();
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

    }

    private void start()
    {
        dowork = true;
        new Thread() {
            @Override
            public void run() {
                byte[] data = null;
                try {

                    //imageProcessor.Init();
                    while (dowork) {
                        data = mYuvFrameQueue.take();
                        if (data != null)
                        {
                            mInputAllocation.copyFrom(data);
                            mScriptFocusPeak.set_gCurrentFrame(mInputAllocation);
                            mScriptFocusPeak.forEach_peak(mOutputAllocation);
                            mOutputAllocation.copyTo(drawBitmap);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    maskImageView.invalidate();
                                }
                            });

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    dowork = false;
                    camera.setPreviewCallback(null);
                    mYuvFrameQueue.clear();
                    Log.d("ImageProcessor", " Releasenative");


                }
            }
        }.start();
    }

    private void stop()
    {
        dowork = false;
        camera.setPreviewCallback(null);
    }


}
