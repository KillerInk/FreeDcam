package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;


import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;

/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends PictureModule
{
    private static String TAG = "freedcam.HdrModule";

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
            workstarted();
            takePicture();
        }
    }

    @Override
    public String ShortName() {
        return "HDR";
    }

    @Override
    public String LongName() {
        return "HDR";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported() && ParameterHandler.isAeBracketActive)
        {
            aeBrackethdr = true;
            ParameterHandler.AE_Bracket.SetValue("true", true);
        }
    }

    @Override
    public void UnloadNeededParameters()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
        {
            aeBrackethdr = false;
            ParameterHandler.AE_Bracket.SetValue("false", true);
        }
    }

    //I_Module END

    protected void takePicture()
    {
        this.isWorking = true;
        Log.d(TAG, "Start Taking Picture");

        try
        {
            if (!ParameterHandler.isAeBracketActive) {
                if (hdrCount == 0)
                    parametersHandler.ManualExposure.SetValue(-8);
                else if (hdrCount == 1)
                    parametersHandler.ManualExposure.SetValue(0);
                else if (hdrCount == 2)
                    parametersHandler.ManualExposure.SetValue(8);
            }
            //Thread.sleep(400);
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

    public void onPictureTaken(byte[] data)
    {
        if (processCallbackData(data, saveFileRunner)) return;
        if (hdrCount == 3) {
            baseCameraHolder.StartPreview();
            workfinished(true);
            parametersHandler.ManualExposure.SetValue(0);
        }
        if (!ParameterHandler.isAeBracketActive && hdrCount < 3)
        {
            baseCameraHolder.StartPreview();
            takePicture();
        }
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

    protected Runnable saveFileRunner = new Runnable() {
        @Override
        public void run()
        {
            if (OverRidePath == "")
            {
                if (!file.getAbsolutePath().endsWith(".dng")) {
                    saveBytesToFile(bytes, file);
                } else
                {
                    String raw[] = getRawSize();
                    int w = Integer.parseInt(raw[0]);
                    int h = Integer.parseInt(raw[1]);
                    String l;
                    if(lastBayerFormat != null)
                        l = lastBayerFormat.substring(lastBayerFormat.length() -4);
                    else
                        l = parametersHandler.PictureFormat.GetValue().substring(parametersHandler.PictureFormat.GetValue().length() -4);
                    RawToDng.ConvertRawBytesToDng(bytes, file.getAbsolutePath(), w, h, Build.MODEL, 0, 0, l);
                }
            }
            else
            {
                file = new File(OverRidePath);
                saveBytesToFile(bytes, file);
            }

        }
    };
}
