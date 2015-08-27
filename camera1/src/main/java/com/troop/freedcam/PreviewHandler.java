package com.troop.freedcam;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
import android.view.View;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.Size;
import com.troop.freedcam.camera.TextureViewRatio;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.modes.ExposureLockParameter;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.modules.I_Callbacks;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import troop.com.camera1.ScriptC_focus_peak;

/**
 * Created by troop on 24.08.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class PreviewHandler implements Camera.PreviewCallback, I_CameraChangedListner
{
    final String TAG = PreviewHandler.class.getSimpleName();
    private TextureViewRatio output;
    CameraUiWrapper cameraUiWrapper;

    private int mHeight;
    private int mWidth;
    private RenderScript mRS;
    private Allocation mAllocationOut;
    private Allocation mAllocationIn;

    private boolean mHaveSurface;
    private Surface mSurface;
    private ScriptC_focus_peak mScriptFocusPeak;
    boolean enable = false;
    boolean doWork = false;

    public PreviewHandler(TextureViewRatio output, CameraUiWrapper cameraUiWrapper, Context context)
    {
        this.output = output;
        this.cameraUiWrapper = cameraUiWrapper;
        output.setSurfaceTextureListener(previewSurfaceListner);
        mRS = RenderScript.create(context);

    }

    public void Enable(boolean enable)
    {
        this.enable = enable;
        setEnable(enable);
    }

    private void setEnable(boolean enable)
    {
        if (enable)
        {
            final Size size = new Size(cameraUiWrapper.camParametersHandler.PreviewSize.GetValue());
            reset(size.width, size.height);
            cameraUiWrapper.cameraHolder.SetPreviewCallback(this);
            Log.d(TAG, "enable focuspeak");

        }
        else if (!enable && mAllocationOut != null)
        {
            Log.d(TAG, "stop focuspeak");
            cameraUiWrapper.cameraHolder.ResetPreviewCallback();
            final Size size = new Size(cameraUiWrapper.camParametersHandler.PreviewSize.GetValue());
            final Bitmap map = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(map);
            canvas.drawColor(Color.TRANSPARENT);


            mAllocationOut.copyFrom(map);
            mAllocationOut.ioSend();
            map.recycle();

        }
    }

    public boolean isEnable() { return  enable;}

    private void reset(int width, int height)
    {


        Log.d(TAG, "reset allocs to :" + width + "x" + height);

        doWork = false;
        while (isWorking)
            try {
                Log.d(TAG, "Wait for work finish");
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        mHeight = height;
        mWidth = width;

        Log.d(TAG, "tbin");
        Type.Builder tbIn = new Type.Builder(mRS, Element.U8(mRS));
        tbIn.setX(mWidth);
        tbIn.setY(mHeight);
        tbIn.setYuvFormat(ImageFormat.NV21);

        mAllocationIn = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE,  Allocation.USAGE_SCRIPT & Allocation.USAGE_SHARED);

        Log.d(TAG, "tbout");
        Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbOut.setX(mWidth);
        tbOut.setY(mHeight);

        mAllocationOut = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);
        setupSurface();
        Log.d(TAG, "script");
        mScriptFocusPeak = new ScriptC_focus_peak(mRS);
        Log.d(TAG, "script done");
        doWork = true;
    }


    private void setupSurface() {
        if (mAllocationOut != null && mSurface != null) {
            Log.d(TAG, "SetupSurface");
            mAllocationOut.setSurface(null);
            mAllocationOut.setSurface(mSurface);
            mHaveSurface = true;
        }
        if (mSurface == null)
            mHaveSurface = false;
    }


    TextureView.SurfaceTextureListener previewSurfaceListner = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Log.d(TAG, "SurfaceSizeAvail");
            mSurface = new Surface(surface);
            setupSurface();

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


            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };


    public void SetAspectRatio(int w, int h)
    {
        output.setAspectRatio(w,h);
        reset(w,h);
    }

    boolean isWorking = false;
    @Override
    public void onPreviewFrame(final byte[] data, Camera camera)
    {
        if (isWorking || data == null || !mHaveSurface || !enable && !doWork)
        {
            Log.d(TAG, "skipframe render not fully init");
            return;
        }

        int teosize = mHeight * mWidth *
                ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
        if (teosize != data.length) {
            Log.d(TAG, "frame size does not match rendersize");
            Camera.Size s = camera.getParameters().getPreviewSize();
            reset(s.width, s.height);
            return;
        }
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

    @Override
    public void onCameraOpen(String message)
    {

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
        setEnable(enable);
    }

    @Override
    public void onPreviewClose(String message)
    {
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraStatusChanged(String status) {

    }

    @Override
    public void onModuleChanged(I_Module module)
    {
        if (module.ModuleName().equals(ModuleHandler.MODULE_PICTURE))
            setEnable(enable);
        else if (module.ModuleName().equals(ModuleHandler.MODULE_VIDEO))
            setEnable(false);

    }
}
