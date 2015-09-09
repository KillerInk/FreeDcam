package troop.com.imageconverter;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.Image;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;


/**
 * Created by troop on 25.08.2015.
 */
public class RS_TextureviewActivity extends Activity implements Camera.PreviewCallback
{
    TextureView output;
    TextureView input;

    Surface outputSurface;

    RenderScript mRS;
    private Allocation mInputAllocation;
    private Allocation mOutputAllocation;
    private ScriptC_focus_peak mScriptFocusPeak;
    int w,h;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    Camera camera;

    private boolean dowork = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rs_textureview_activity);
        output = (TextureView)findViewById(R.id.textureView_previewoutput);
        output.setSurfaceTextureListener(previewTextureListner);
        input = (TextureView)findViewById(R.id.textureView_camerainput);
        input.setSurfaceTextureListener(cameraTextureListner);
        mRS = RenderScript.create(this.getApplicationContext());

    }

    private void initRenderScript()
    {

        Type.Builder tbIn = new Type.Builder(mRS, Element.U8(mRS));
        tbIn.setX(w);
        tbIn.setY(h);
        tbIn.setYuvFormat(ImageFormat.NV21);

        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(w);
        tbOut.setY(h);

        mInputAllocation = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);
        mOutputAllocation = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);
        mScriptFocusPeak = new ScriptC_focus_peak(mRS);
    }

    TextureView.SurfaceTextureListener previewTextureListner = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            outputSurface = new Surface(surface);
            setupSurface();
            start();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            outputSurface = new Surface(surface);
            setupSurface();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
        {
            stop();
            outputSurface = null;
            setupSurface();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    private void setupSurface() {
        if (outputSurface != null) {
            if (mOutputAllocation != null) {
                mOutputAllocation.setSurface(outputSurface);
            }

        }
    }

    TextureView.SurfaceTextureListener cameraTextureListner = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            camera = Camera.open(0);
            Camera.Parameters parameters = camera.getParameters();
            //parameters.setPreviewSize(640,480);
            parameters.setPreviewFormat(ImageFormat.NV21);
            camera.setParameters(parameters);
            Camera.Size size = parameters.getPreviewSize();
            w = size.width;
            h = size.height;
            try {

                camera.setPreviewTexture(input.getSurfaceTexture());
                camera.setPreviewCallback(RS_TextureviewActivity.this);

            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            initRenderScript();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
        {
            if (dowork)
                stop();
            camera.stopPreview();
            camera.release();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
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
                            mOutputAllocation.ioSend();



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
        //camera.setPreviewCallback(null);
    }
}
