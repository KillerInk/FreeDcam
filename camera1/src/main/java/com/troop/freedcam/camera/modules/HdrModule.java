package com.troop.freedcam.camera.modules;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.image_saver.DngSaver;
import com.troop.freedcam.camera.modules.image_saver.I_WorkeDone;
import com.troop.freedcam.camera.modules.image_saver.JpegSaver;
import com.troop.freedcam.camera.modules.image_saver.JpsSaver;
import com.troop.freedcam.camera.modules.image_saver.RawSaver;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends PictureModule implements I_WorkeDone
{

    private static String TAG = "freedcam.HdrModule";

    int hdrCount = 0;
    boolean aeBrackethdr = false;
    File[] files;
    boolean isManualExpo = false;

    public HdrModule(BaseCameraHolder cameraHandler, ModuleEventHandler eventHandler, Handler backgroundHandler) {
        super(cameraHandler, eventHandler, backgroundHandler);
        name = ModuleHandler.MODULE_HDR;
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
        {
            files = new File[3];
            hdrCount = 0;
            if (dngcapture && ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
            {
                ParameterHandler.ZSL.SetValue("off",true);
            }
            startworking();
            LoadAEB();
            if (aeBrackethdr && ParameterHandler.PictureFormat.GetValue().equals("jpeg"))
            {
                baseCameraHolder.TakePicture(null, null, aeBracketCallback);
            }
            else {
                takePicture();
            }
        }
        return true;
    }

    @Override
    public String ShortName() {
        return "Bracket";
    }

    @Override
    public String LongName() {
        return "Bracketing";
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void LoadNeededParameters()
    {
        LoadAEB();
    }

    @Override
    public void UnloadNeededParameters(){
        if (aeBrackethdr)
            ParameterHandler.AE_Bracket.SetValue("Off", true);
    }

    //I_Module END

    protected void takePicture()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setExposureToCamera();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }

                final String picFormat = ParameterHandler.PictureFormat.GetValue();
                if (picFormat.equals("jpeg")) {
                    final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, HdrModule.this, handler);
                    jpegSaver.TakePicture();
                } else if (!ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.BAYER)) {
                    final RawSaver rawSaver = new RawSaver(baseCameraHolder, HdrModule.this, handler);
                    rawSaver.TakePicture();
                } else if (ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.DNG)) {
                    DngSaver dngSaver = new DngSaver(baseCameraHolder, HdrModule.this, handler);
                    dngSaver.TakePicture();
                }
            }
        });
    }

    @Override
    public void OnWorkDone(File file)
    {
        ((CamParametersHandler)ParameterHandler).SetParametersToCamera(((CamParametersHandler)ParameterHandler).getParameters());
        baseCameraHolder.StartPreview();
        if (hdrCount == 2)
        {
            stopworking();
            ParameterHandler.ManualExposure.SetValue(0);
        }
        else if (hdrCount < 2)
        {
            hdrCount++;
            takePicture();
        }
        MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext(), file);
        eventHandler.WorkFinished(file);
    }

    @Override
    public void OnError(String error)
    {
        baseCameraHolder.errorHandler.OnError(error);
        stopworking();
    }

    private void setExposureToCamera()
    {
        if(isManualExpo)
        {
            if(ParameterHandler.ManualShutter.GetStringValue().contains("/")) {
                int value = 0;

                if (hdrCount == 0)
                {
                    System.out.println("Do Nothing");
                    //getStop(1 / Integer.parseInt(ParameterHandler.ManualShutter.GetStringValue().split("/")[1]), -12);
                }
                else if (hdrCount == 1)
                    getStop(1 / Integer.parseInt(ParameterHandler.ManualShutter.GetStringValue().split("/")[1]), -12.0f);
                else if (hdrCount == 2)
                    getStop(1 / Integer.parseInt(ParameterHandler.ManualShutter.GetStringValue().split("/")[1]), 12.0f);
                //ParameterHandler.ManualShutter.SetValue();
            }
            else
            {
                if (hdrCount == 0)
                {
                    //getStop(Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue()), -12);
                    System.out.println("Do Nothing");
                }
                else if (hdrCount == 1)
                    getStop(Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue()), -12.0f);
                else if (hdrCount == 2)
                    getStop(Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue()), 12.0f);
            }
        }
        else {
            int value = 0;

            if (hdrCount == 0) {
                value = Integer.parseInt(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_AEB1));
            } else if (hdrCount == 1)
                value = Integer.parseInt(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_AEB2));
            else if (hdrCount == 2)
                value = Integer.parseInt(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_AEB3));

            Logger.d(TAG, "Set HDR Exposure to :" + value + "for image count " + hdrCount);
            //ParameterHandler.ManualExposure.SetValue(value);

            /*checkAEMODE();
            if(isManualExpo)
            {
                ParameterHandler.ManualShutter.SetValue(DoStopCalc(value));
            }
           TODO */
           // ((CamParametersHandler)ParameterHandler).setString("exposure-compensation", value + "");
            Logger.d(TAG, "HDR Exposure SET");
        }
    }

    private String DoStopCalc(int stop)
    {
        float shutterString = 0.0f;

        if(ParameterHandler.ManualShutter.GetStringValue().contains("/"))
        {
            shutterString = Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue().split("/")[1]);
        }
        else
            shutterString = Float.parseFloat(ParameterHandler.ManualShutter.GetStringValue());


        float StoppedShift;
        if (stop < 0)
            StoppedShift = shutterString / (stop*4);
        else
            StoppedShift = shutterString * (stop*4);

        return "1/"+String.valueOf(shutterString);




    }

    private void checkAEMODE()
    {
        if (!ParameterHandler.ManualShutter.GetStringValue().equals("Auto"))
            isManualExpo = true;
    }

    private float getStop(float current,float TargetStop)
    {
        float stop = current;
        int stopT = 0;

        if(Math.signum(TargetStop) >= 1.0)
        {
            for(int i = 0; i < TargetStop;i++)
            {
                stop = stop * 2;
                stopT = i+1;
            }
        }
        else
        {
            for(int i = 0; i < TargetStop;i--)
            {
                stop = stop * 2;
                stopT = i+1;
            }
        }
        return stop;
    }

    I_Callbacks.PictureCallback aeBracketCallback = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            final String picFormat = ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg")) {
                final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, aeBracketDone, handler);
                jpegSaver.saveBytesToFile(data, new File(StringUtils.getFilePathHDR(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), jpegSaver.fileEnding, hdrCount)));
            }
            else if (picFormat.equals(StringUtils.FileEnding.JPS)) {
                final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, aeBracketDone, handler);
                jpsSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathHDR(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), jpsSaver.fileEnding, hdrCount)));
            }
            else if (!ParameterHandler.IsDngActive() && (picFormat.contains(StringUtils.FileEnding.BAYER)|| picFormat.equals(StringUtils.FileEnding.RAW))) {
                final RawSaver rawSaver = new RawSaver(baseCameraHolder, aeBracketDone, handler);
                rawSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathHDR(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), rawSaver.fileEnding, hdrCount)));
            } else if (ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.DNG)) {
                DngSaver dngSaver = new DngSaver(baseCameraHolder, aeBracketDone, handler);
                dngSaver.processData(data, new File(StringUtils.getFilePathHDR(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), dngSaver.fileEnding, hdrCount)));
            }
        }
    };

    I_WorkeDone aeBracketDone = new I_WorkeDone() {
        @Override
        public void OnWorkDone(File file) {
            MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
            if (hdrCount == 2) {
                stopworking();
                baseCameraHolder.StartPreview();
            }
            else if (hdrCount < 2)
                hdrCount++;
        }

        @Override
        public void OnError(String error)
        {
            baseCameraHolder.errorHandler.OnError(error);
            stopworking();
        }
    };
    private void LoadAEB()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
        {
            if (ParameterHandler.PictureFormat.GetValue().equals("jpeg")) {
                aeBrackethdr = true;
                ParameterHandler.AE_Bracket.SetValue("AE-Bracket", true);
            }
            else {
                aeBrackethdr = false;
                ParameterHandler.AE_Bracket.SetValue("Off", true);
            }

        }

    }

}
