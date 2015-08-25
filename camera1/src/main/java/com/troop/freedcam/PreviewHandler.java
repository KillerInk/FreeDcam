package com.troop.freedcam;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Paint;
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
import com.troop.freedcam.camera.parameters.modes.ExposureLockParameter;
import com.troop.freedcam.i_camera.modules.I_Callbacks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.camera1.ScriptC_focus_peak;

/**
 * Created by troop on 24.08.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class PreviewHandler implements Camera.PreviewCallback
{
    final String TAG = PreviewHandler.class.getSimpleName();
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
    private Bitmap drawBitmap;

    public PreviewHandler(TextureView input, TextureView output, CameraUiWrapper cameraUiWrapper, Context context)
    {
        this.input = input;
        this.output = output;
        this.cameraUiWrapper = cameraUiWrapper;
        output.setSurfaceTextureListener(previewSurfaceListner);
        input.setSurfaceTextureListener(cameraSurfaceListner);
        mRS = RenderScript.create(context);

    }

    public TextureView getInput(){return input; }


    public void reset(int width, int height)
    {
        Log.d(TAG, "reset allocs to :" + width + "x" + height);
        if (mHeight == height && mWidth == width)
            return;
        stop();
        mHeight = height;
        mWidth = width;


        Log.d(TAG, "tbin");
        Type.Builder tbIn = new Type.Builder(mRS, Element.U8(mRS));
        tbIn.setX(width);
        tbIn.setY(height);
        tbIn.setYuvFormat(ImageFormat.NV21);

        mAllocationIn = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);

        Log.d(TAG, "tbout");
        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(width);
        tbOut.setY(height);

        mAllocationOut = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);
        setupSurface();
        Log.d(TAG, "script");
        mScriptFocusPeak = new ScriptC_focus_peak(mRS);
        start();
        Log.d(TAG, "script done");
    }


    private void setupSurface() {
        if (mAllocationOut != null) {
            Log.d(TAG, "SetupSurface");
            mAllocationOut.setSurface(null);
            mAllocationOut.setSurface(mSurface);
            mHaveSurface = true;
        }
        if (mSurface == null)
            mHaveSurface = false;
    }

    private void start()
    {
        doWork = true;
    }

    private void stop()
    {
        doWork = false;
    }

    TextureView.SurfaceTextureListener previewSurfaceListner = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "SurfaceSizeAvail");
            mSurface = new Surface(surface);
            setupSurface();

            start();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "SurfaceSizeChanged");
            mSurface = new Surface(surface);
            setupSurface();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            Log.d(TAG, "SurfaceDestroyed");
            mSurface = null;
            setupSurface();
            stop();


            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    TextureView.SurfaceTextureListener cameraSurfaceListner = new TextureView.SurfaceTextureListener()
    {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            cameraUiWrapper.StartCamera();
            cameraUiWrapper.cameraHolder.SetPreviewCallback(PreviewHandler.this);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
        {
            cameraUiWrapper.StopCamera();
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    boolean isWorking = false;
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera)
    {
        final Camera.Size size = camera.getParameters().getPreviewSize();
        if (mHeight != size.height && mWidth != size.width)
            reset(size.width,size.height);
        if (isWorking || !doWork || data == null || !mHaveSurface)
            return;
        Log.d(TAG, "Process Frame");


        new Thread(new Runnable() {
            @Override
            public void run()
            {
                isWorking = true;
                mAllocationIn.copyFrom(data);
                mScriptFocusPeak.set_gCurrentFrame(mAllocationIn);
                mScriptFocusPeak.forEach_peak(mAllocationOut);
                mAllocationOut.ioSend();
                isWorking = false;
            }
        }).start();
    }
}
