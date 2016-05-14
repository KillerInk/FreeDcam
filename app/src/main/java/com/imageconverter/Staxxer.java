package com.imageconverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.view.Surface;

import com.freedcam.Native.StaxxerJNI;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_Module;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class Staxxer implements Camera.PreviewCallback, I_CameraChangedListner,I_ModuleEvent
{
    private final String TAG = Staxxer.class.getSimpleName();


    private int mHeight;
    private int mWidth;
    private RenderScript mRS;
    private Allocation mAllocationMain;
    private Allocation mAllocationSub;
    private Surface mSurface;
    private ScriptC_imagestack imagestack;
    private boolean enable = false;
    private boolean doWork = false;
    private Context context;
    private Size size;
    private boolean isWorking = false;
    private StaxxerJNI jpg2rgb;

    private Bitmap merged;
    private byte[] mergedByteStream;
    public Staxxer(Size size, Context context)
    {
        Logger.d(TAG, "Ctor");
        this.size = size;

        this.context = context;
        jpg2rgb = StaxxerJNI.GetInstance();
    }

    public void Enable()
    {
        Logger.d(TAG, "Enable:" + true);
        this.enable = true;
        setEnable(this.enable);
    }

    private void setEnable(boolean enabled)
    {
        Logger.d(TAG, "setEnable" + enabled);
        if (enabled)
        {
            if(mRS == null) {
                mRS = RenderScript.create(context.getApplicationContext());
                mRS.setPriority(RenderScript.Priority.LOW);
            }
            reset(size.width, size.height);
            Logger.d(TAG, "Set PreviewCallback");
            Logger.d(TAG, "enable focuspeak");
        }
        else
        {
            if (mRS != null)
                mRS.finish();
        }
    }


    public boolean isEnable() { return  enable;}

    private void reset(int width, int height)
    {
        try {
            mHeight = height;
            mWidth = width;
            if (mRS == null) {
                Logger.d(TAG, "rest called but mRS is null");
              //  clear_preview("reset");
                return;
            }
            Logger.d(TAG, "reset allocs to :" + width + "x" + height);
            try {
                //cameraUiWrapper.cameraHolder.ResetPreviewCallback();
            } catch (NullPointerException ex) {
            }

            Type.Builder tbIn = new Type.Builder(mRS, Element.RGB_888(mRS));
            tbIn.setX(mWidth);
            tbIn.setY(mHeight);

            Type.Builder tbIn2 = new Type.Builder(mRS, Element.RGBA_8888(mRS));
            tbIn2.setX(mWidth);
            tbIn2.setY(mHeight);

            mAllocationMain = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mAllocationSub = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

            imagestack = new ScriptC_imagestack(mRS);
            Logger.d(TAG, "script done enabled: " + enable);
        }
        catch (RSRuntimeException ex)
        {
            onCameraError("RenderScript Failed");
        }
    }



    public void Process(final byte[] frameA,final byte[] frameB , final boolean bufferInStaxxer, final String SessionPath)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                isWorking = true;
                if (!bufferInStaxxer)
                {
                    mAllocationMain.copyFrom(frameA);
                    mAllocationSub.copyFrom(frameB);
                    imagestack.set_gCurrentFrame(mAllocationMain);
                }

                else
                {
                    mAllocationMain.copyFrom(frameB);
                    mAllocationSub.copyFrom(jpg2rgb.ExtractRGB(mergedByteStream));
                    imagestack.set_gCurrentFrame(mAllocationMain);
                }
                imagestack.forEach_stackimage_avarage(mAllocationSub);
                //that will cause oom! you cant create allocs direct from bitmap
                merged = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                mAllocationSub.copyTo(merged);
                try
                {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    merged.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    mergedByteStream = bos.toByteArray();
                    Merged2File(mergedByteStream,SessionPath);
                }
                catch (Exception x)
                {
                    Logger.exception(x);
                }

                //  mAllocationOut.ioSend();
                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                isWorking = false;
                System.out.println("Im In Line"+new Exception().getStackTrace()[0].getLineNumber());
            }
        });
    }

    private  void Merged2File(byte[] fromRS,String SessionFolder )
    {
        try {
            FileOutputStream fos = new FileOutputStream(new File(SessionFolder+ StringUtils.getStringDatePAttern().format(new Date())+"_Stack.jpg"));
            fos.write(fromRS);
        }
        catch (IOException e)
        {

        }
    }


    @Override
    public void onPreviewFrame(final byte[] data, Camera camera)
    {
       /* if (enable == false)
        {
            Logger.d(TAG, "onPreviewFrame enabled:" +enable);
            camera.addCallbackBuffer(data);
            return;
        }
        if (doWork == false) {
            camera.addCallbackBuffer(data);
            return;
        }
        if (data == null)
            return;
        if (isWorking == true) {
            camera.addCallbackBuffer(data);
            return;
        }*/

       /*int teosize = mHeight * mWidth *
                ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
        if (teosize != data.length) {
            Logger.d(TAG, "frame size does not match rendersize");
            Camera.Size s = camera.getParameters().getPreviewSize();

            reset(4208, 3120);
            return;
        } */

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
        Logger.d(TAG, "onPreviewOpen enable:" + enable);
     //   clear_preview("onPreviewOpen");
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
    public void onModuleChanged(I_Module module) {
    }

    @Override
    public String ModuleChanged(String module)
    {
        Logger.d(TAG, "ModuleChanged(String):" + module + " enabled:" +enable);
        if (module.equals(AbstractModuleHandler.MODULE_STACKING)
                ||module.equals(AbstractModuleHandler.MODULE_HDR)
                ||module.equals(AbstractModuleHandler.MODULE_INTERVAL))
        {
            setDoWork(true);
            setEnable(enable);

        }
        else {
            setDoWork(false);
            setEnable(enable);
        }
        return null;
    }

    private void setDoWork(boolean work) {this.doWork = work;}
}
