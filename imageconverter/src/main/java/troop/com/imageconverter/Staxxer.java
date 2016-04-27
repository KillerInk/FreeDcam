package troop.com.imageconverter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptC;
import android.renderscript.Type;
import android.view.Surface;
import android.view.TextureView;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.Size;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.ParameterExternalShutter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.ui.I_AspectRatio;
import com.troop.freedcam.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import jni.staxxer.StaxxerJNI;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class Staxxer implements Camera.PreviewCallback, I_CameraChangedListner,I_ModuleEvent
{
    final String TAG = PreviewHandler.class.getSimpleName();


    private int mHeight;
    private int mWidth;
    private RenderScript mRS;
    private Allocation mAllocationOut;
    private Allocation mAllocationMain;
    private Allocation mAllocationSub;
    private Surface mSurface;
    private ScriptC_imagestack_rgb_to_argb imagestack;
    private boolean enable = false;
    private boolean doWork = false;
    Context context;
    Size size;
    boolean isWorking = false;
    private StaxxerJNI jpg2rgb;

    private Bitmap merged;
    private byte[] mergedByteStream;
    public Staxxer(Size size, Context context)
    {
        Logger.d(TAG, "Ctor");
        this.size = size;

        this.context = context;
        jpg2rgb = StaxxerJNI.GetInstance();
        //this.size = size;

       // cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
       // output.setSurfaceTextureListener(previewSurfaceListner);
       // clear_preview("Ctor");
    }

    public void Enable(boolean enable)
    {
        Logger.d(TAG, "Enable:" + enable);
        this.enable = enable;
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

            Type.Builder tbIn2 = new Type.Builder(mRS, Element.RGB_888(mRS));
            tbIn2.setX(mWidth);
            tbIn2.setY(mHeight);



            if (mAllocationOut != null)
                mAllocationOut.setSurface(null);

            mAllocationMain = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mAllocationSub = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
           // mAllocationMerged = Allocation.createTyped(mRS, tbIn3.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

            Type.Builder tbOut = new Type.Builder(mRS, Element.RGBA_8888(mRS));
            tbOut.setX(mWidth);
            tbOut.setY(mHeight);

            mAllocationOut = Allocation.createTyped(mRS, tbOut.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT | Allocation.USAGE_IO_OUTPUT);
            if (mSurface != null)
                mAllocationOut.setSurface(mSurface);
            else
                Logger.d(TAG, "surfaceNull");
            imagestack = new ScriptC_imagestack_rgb_to_argb(mRS);
            Logger.d(TAG, "script done enabled: " + enable);
        }
        catch (RSRuntimeException ex)
        {
            onCameraError("RenderScript Failed");
        }
    }



    public void Process(final byte[] frameA,final byte[] frameB , final boolean bufferInStaxxer, final String SessionPath)
    {
//        System.out.println("Entered RS Classs" +" ImageA="+frameA.length+" ImageB="+frameB.length);
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                isWorking = true;
                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                if (!bufferInStaxxer){
                    System.out.println("Im In Line"+new Exception().getStackTrace()[0].getLineNumber());
                    mAllocationMain.copyFrom(frameA);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                    mAllocationSub.copyFrom(frameB);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                imagestack.set_gCurrentFrame(mAllocationMain);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                imagestack.set_gLastFrame(mAllocationSub);
                    System.out.println("Im In Line"+new Exception().getStackTrace()[0].getLineNumber());}

                else
                {
                    System.out.println("Im In Line"+new Exception().getStackTrace()[0].getLineNumber());
                    mAllocationMain.copyFrom(frameB);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                    mAllocationSub.copyFrom(jpg2rgb.ExtractRGB(mergedByteStream));
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                    imagestack.set_gCurrentFrame(mAllocationMain);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                    imagestack.set_gLastFrame(mAllocationSub);
                    System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                }


                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                imagestack.forEach_stackimage(mAllocationOut);
                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                merged = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());
                mAllocationOut.copyTo(merged);

                System.out.println("Im In Line" + new Exception().getStackTrace()[0].getLineNumber());


               try {


                   ByteArrayOutputStream bos = new ByteArrayOutputStream();

                   merged.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                   mergedByteStream = bos.toByteArray();
                   Merged2File(mergedByteStream,SessionPath);

               }
               catch (Exception x)
               {
x.printStackTrace();
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
