package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;


import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;

/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends PictureModule
{
    String TAG = "freedcam.HdrModule";

    int hdrCount = 0;
    boolean aeBrackethdr = false;

    public HdrModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_HDR;
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!isWorking) {
            hdrCount = 0;
            takePicture();
        }
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        if (baseCameraHolder.ParameterHandler.AE_Bracket.IsSupported())
        {
            aeBrackethdr = true;
            baseCameraHolder.ParameterHandler.AE_Bracket.SetValue("AE-Bracket", true);
        }
    }

    @Override
    public void UnloadNeededParameters()
    {
        if (baseCameraHolder.ParameterHandler.AE_Bracket.IsSupported())
        {
            aeBrackethdr = true;
            baseCameraHolder.ParameterHandler.AE_Bracket.SetValue("Off", true);
        }
    }

    //I_Module END

    protected void takePicture()
    {
        this.isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try
        {
            //soundPlayer.PlayShutter();
            baseCameraHolder.TakePicture(shutterCallback,rawCallback,this);
            Log.d(TAG, "Picture Taking is Started");
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }

    public void onPictureTaken(byte[] data, Camera camera)
    {
        if (processCallbackData(data)) return;
        if (hdrCount == 2)
            baseCameraHolder.StartPreview();
        if (!aeBrackethdr && hdrCount < 2)
            takePicture();
    }

    @Override
    protected File createFileName()
    {
        Log.d(TAG, "Create FileName");
        String s1 = getStringAddTime();
        s1 += "HDR" + this.hdrCount;
        hdrCount++;
        return  getFileAndChooseEnding(s1);
    }
}
