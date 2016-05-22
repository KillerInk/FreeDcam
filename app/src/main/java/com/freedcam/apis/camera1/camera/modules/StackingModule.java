package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;

import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.modes.StackModeParameter;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.imageconverter.ScriptC_imagestack;
import com.imageconverter.ScriptField_MinMaxPixel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GeorgeKiarie on 13/04/2016.
 */
public class StackingModule extends PictureModule implements I_Callbacks.PictureCallback
{
    final String TAG = StackingModule.class.getSimpleName();
    private boolean KeepStacking = false;
    private int FrameCount = 0;
    private String SessionFolder="";

    private Allocation mAllocationInput;
    private Allocation mAllocationOutput;
    private ScriptField_MinMaxPixel medianMinMax;
    private ScriptC_imagestack imagestack;
    private RenderScript mRS;
    private List<File> capturedPics;

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
        Logger.d(TAG, "isWorking: " + isWorking + " KeepStacking: " + KeepStacking);
        if (!isWorking && !KeepStacking)
        {
            FrameCount = 0;
            SessionFolder = StringUtils.GetDCIMFolder(appSettingsManager.GetWriteExternal())+ StringUtils.getStringDatePAttern().format(new Date()) + "/";
            Logger.d(TAG,"Start Stacking");
            KeepStacking = true;
            capturedPics = new ArrayList<>();
            initRsStuff();
            ParameterHandler.ZSL.SetValue("off", true);
            workstarted();
            final String picFormat = ParameterHandler.PictureFormat.GetValue();
            if (!picFormat.equals("jpeg"))
                ParameterHandler.PictureFormat.SetValue("jpeg",true);

            cameraHolder.TakePicture(null, this);
            return true;
        }
        else if (KeepStacking)
        {
            Logger.d(TAG, "Stop Stacking");
            KeepStacking = false;
            return false;
        }
        return false;

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

    }

    @Override
    public void UnloadNeededParameters(){

    }

    private void initRsStuff()
    {
        if(mRS == null)
        {
            mRS = RenderScript.create(context);
            mRS.setPriority(RenderScript.Priority.LOW);
        }
        int mWidth = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[0]);
        int mHeight = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[1]);
        Type.Builder tbIn2 = new Type.Builder(mRS, Element.RGBA_8888(mRS));
        tbIn2.setX(mWidth);
        tbIn2.setY(mHeight);
        if (!ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.MEDIAN))
        {
            mAllocationInput = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mAllocationOutput = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        }
        else
        {
            mAllocationInput = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            mAllocationOutput = Allocation.createTyped(mRS, tbIn2.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            medianMinMax = new ScriptField_MinMaxPixel(mRS, mWidth*mHeight);
        }
        imagestack = new ScriptC_imagestack(mRS);
        imagestack.set_Width(mWidth);
        imagestack.set_Height(mHeight);
    }


    private void processData(byte[] data, File file)
    {
        cameraHolder.StartPreview();
        Logger.d(TAG, "start preview");
        Logger.d(TAG,"The Data Is " + data.length + " bytes Long" + " and the path is " + file.getAbsolutePath());
        File f = new File(SessionFolder+StringUtils.getStringDatePAttern().format(new Date())+".jpg");
        saveBytesToFile(data,f);
        capturedPics.add(f);
        MediaScannerManager.ScanMedia(context, f);
        workfinished(true);


        Logger.d(TAG, "runned stackimage");
        cameraHolder.SendUIMessage("Captured Picture: " + FrameCount++);


        if (KeepStacking)
        {
            workstarted();
            Logger.d(TAG, "keepstacking take next pic");
            cameraHolder.TakePicture(null, this);
        }
        else
        {
            Logger.d(TAG, "End of Stacking create bitmap and compress");
            FrameCount = 0;
            workstarted();
            for (File s : capturedPics)
            {
                cameraHolder.SendUIMessage("Stacked: " + FrameCount++ + "/"+ capturedPics.size());
                stackImage(s);
            }
            int mWidth = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[0]);
            int mHeight = Integer.parseInt(ParameterHandler.PictureSize.GetValue().split("x")[1]);
            final Bitmap outputBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mAllocationOutput.copyTo(outputBitmap);
            File stackedImg = new File(SessionFolder + StringUtils.getStringDatePAttern().format(new Date()) + "_Stack.jpg");
            SaveBitmapToFile(outputBitmap,stackedImg);
            workfinished(true);
            MediaScannerManager.ScanMedia(context, stackedImg);
            eventHandler.WorkFinished(file);
        }
    }

    private void stackImage(File file)
    {
        mAllocationInput.copyFrom(BitmapFactory.decodeFile(file.getAbsolutePath()));
        Logger.d(TAG, "Copied data to inputalloc");
        imagestack.set_gCurrentFrame(mAllocationInput);
        imagestack.set_gLastFrame(mAllocationOutput);
        Logger.d(TAG, "setted inputalloc to RS");
        if (ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.AVARAGE))
            imagestack.forEach_stackimage_avarage(mAllocationOutput);
        else if (ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.AVARAGE1x2))
            imagestack.forEach_stackimage_avarage1x2(mAllocationOutput);
        else if (ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.AVARAGE1x3))
            imagestack.forEach_stackimage_avarage1x3(mAllocationOutput);
        else if (ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.AVARAGE3x3))
            imagestack.forEach_stackimage_avarage3x3(mAllocationOutput);
        else if(ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.LIGHTEN))
            imagestack.forEach_stackimage_lighten(mAllocationOutput);
        else if (ParameterHandler.imageStackMode.GetValue().equals(StackModeParameter.MEDIAN))
        {
            imagestack.bind_medianMinMaxPixel(medianMinMax);
            imagestack.forEach_stackimage_median(mAllocationOutput);
        }
    }


}
