package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;

import com.freedcam.Native.StaxxerJNI;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.modules.image_saver.I_WorkeDone;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.imageconverter.ScriptC_imagestack_rgb_to_argb;
import com.imageconverter.Staxxer;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackingModule extends PictureModule implements I_Callbacks.PictureCallback
{
    final String TAG = StackingModule.class.getSimpleName();
    private boolean KeepStacking = true;
    private int FrameCount = 0;

    private StaxxerJNI jpg2rgb;
    private boolean NewSession = false;
    private String SessionFolder="";

    private Allocation mAllocationInput;
    private Allocation mAllocationOutput;
    private ScriptC_imagestack_rgb_to_argb imagestack;
    private RenderScript mRS;

    public StackingModule(CameraHolderApi1 cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        name = ModuleHandler.MODULE_STACKING;

    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {


        if (!isWorking)
        {
            initRsStuff();
            ParameterHandler.ZSL.SetValue("off", true);
            workstarted();
            final String picFormat = ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg"))
                cameraHolder.TakePicture(null, this);
            return true;
        }

        else {
            KeepStacking = false;
            return false;
        }

    }

    @Override
    public void onPictureTaken(final byte[] data) {
        Logger.d(TAG, "Take Picture Callback");
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                File f = new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".jpg"));
                processData(data, f);
            }
        });
    }

    @Override
    public String ShortName() {
        return "Stack";
    }

    @Override
    public String LongName() {
        return "Stacking";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        jpg2rgb = StaxxerJNI.GetInstance();
    }

    @Override
    public void UnloadNeededParameters(){

    }


    private void processData(byte[] data, File file) {
        Logger.d(TAG,"The Data Is " + data.length + " bytes Long" + " and the path is " + file.getAbsolutePath());
        if(!NewSession) {
            SessionFolder = "/sdcard/DCIM/FreeDcam/" + StringUtils.getStringDatePAttern().format(new Date()) + "/";

            NewSession = true;
        }
        File f = new File(SessionFolder+StringUtils.getStringDatePAttern().format(new Date())+".jpg");
        saveBytesToFile(data,f);

        byte[] tmp = jpg2rgb.ExtractRGB(data);
        Logger.d(TAG, "RGB data size :" + tmp.length);
        int size =mAllocationInput.getBytesSize();
        Logger.d(TAG, "InputAllocation Size:" + size);
        mAllocationInput.copyFrom(tmp);
        Logger.d(TAG, "Copied data to inputalloc");
        imagestack.set_gCurrentFrame(mAllocationInput);
        imagestack.set_gLastFrame(mAllocationOutput);
        Logger.d(TAG, "setted inputalloc to RS");
        imagestack.forEach_stackimage(mAllocationOutput);
        Logger.d(TAG, "runned stackimage");

        cameraHolder.StartPreview();
        Logger.d(TAG, "start preview");

        if(KeepStacking)
        {
            Logger.d(TAG, "keepstacking take next pic");
            cameraHolder.TakePicture(null, this);
        }
        else
        {
            Logger.d(TAG, "End of Stacking create bitmap and compress");
            int mWidth = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[0]);
            int mHeight = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[1]);
            Bitmap map = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mAllocationOutput.copyTo(map);
            try {
                FileOutputStream fos = new FileOutputStream(new File(SessionFolder+ StringUtils.getStringDatePAttern().format(new Date())+"_Stack.jpg"));
                map.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            workfinished(true);
            MediaScannerManager.ScanMedia(context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
        }
    }

    private void initRsStuff()
    {
        if(mRS == null)
        {
            mRS = RenderScript.create(context.getApplicationContext());
            mRS.setPriority(RenderScript.Priority.LOW);
        }
        int mWidth = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[0]);
        int mHeight = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[1]);
        Type.Builder tbIn = new Type.Builder(mRS, Element.U8(mRS));
        tbIn.setX(mWidth*3);
        tbIn.setY(mHeight);

        Type.Builder tbIn2 = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbIn2.setX(mWidth);
        tbIn2.setY(mHeight);

        mAllocationInput = Allocation.createTyped(mRS, tbIn.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        mAllocationOutput = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        imagestack = new ScriptC_imagestack_rgb_to_argb(mRS);
    }

}
