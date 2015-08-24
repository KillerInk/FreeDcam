package com.troop.freedcam;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.modules.I_Callbacks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.imageconverter.ScriptC_focus_peak;

/**
 * Created by troop on 24.08.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class PreviewHandler implements TextureView.SurfaceTextureListener, I_Callbacks.PreviewCallback
{
    private TextureView input;
    private TextureView output;
    CameraUiWrapper cameraUiWrapper;

    private int mHeight;
    private int mWidth;
    private RenderScript mRS;
    private Allocation mAllocationOut;
    private Allocation mAllocationIn;

    private boolean mHaveSurface;
    private Surface mSurface;
    private ScriptC_focus_peak mScriptFocusPeak;
    private final BlockingQueue<byte[]> mYuvFrameQueue = new ArrayBlockingQueue<byte[]>(2);
    boolean doWork = false;

    public PreviewHandler(TextureView input, TextureView output, CameraUiWrapper cameraUiWrapper)
    {
        this.input = input;
        this.output = output;
        this.cameraUiWrapper = cameraUiWrapper;
        output.setSurfaceTextureListener(this);
        mRS = RenderScript.create(output.getContext());
    }

    public TextureView getInput(){return input; }


    public void reset(int width, int height)
    {
        stop();
        if (mHeight == height && mWidth == width)
            return;
        mHeight = height;
        mWidth = width;
        if (mAllocationOut != null) {
            mAllocationOut.destroy();
        }
        Type.Builder tbIn = new Type.Builder(mRS, Element.createPixel(mRS, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        tbIn.setX(width);
        tbIn.setY(height);
        tbIn.setYuvFormat(ImageFormat.NV21);
        mAllocationIn = Allocation.createTyped(mRS, tbIn.create(), Allocation.USAGE_SCRIPT);

        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(width);
        tbOut.setY(height);
        mAllocationOut = Allocation.createTyped(mRS, tbOut.create(), Allocation.USAGE_IO_OUTPUT | Allocation.USAGE_SCRIPT);
        setupSurface();

        mScriptFocusPeak = new ScriptC_focus_peak(mRS);
        start();

    }

    void setupSurface() {
        if (mAllocationOut != null) {
            mAllocationOut.setSurface(mSurface);
        }
        if (mSurface != null) {
            mHaveSurface = true;
        } else {
            mHaveSurface = false;
        }
    }

    void execute(byte[] yuv) {
        mAllocationIn.copyFrom(yuv);
        if (mHaveSurface) {
            mScriptFocusPeak.set_gCurrentFrame(mAllocationIn);
            mScriptFocusPeak.forEach_peak(mAllocationOut);
            mAllocationOut.ioSend();
            //mYuv.forEach(mAllocationOut);
            //mScript.forEach_root(mAllocationOut, mAllocationOut);
            //mAllocationOut.ioSend();
        }
    }

    private void start()
    {
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
                            execute(data);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    doWork = false;
                    mYuvFrameQueue.clear();
                    Log.d("ImageProcessor", " Releasenative");


                }
            }
        }.start();
    }

    private void stop()
    {
        doWork = false;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        setupSurface();
        cameraUiWrapper.StartCamera();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {

        mSurface = new Surface(surface);
        setupSurface();

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        stop();
        cameraUiWrapper.StopCamera();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onPreviewFrame(byte[] data, int imageFormat) {
        if (mYuvFrameQueue.size() == 2)
        {
            mYuvFrameQueue.remove();
        }
        mYuvFrameQueue.add(data);
    }
}
