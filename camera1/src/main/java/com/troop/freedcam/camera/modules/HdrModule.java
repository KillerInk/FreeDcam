package com.troop.freedcam.camera.modules;

import android.os.Handler;
import android.util.Log;

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

    public HdrModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler, Handler backgroundHandler) {
        super(cameraHandler, Settings, eventHandler, backgroundHandler);
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
            if (dngcapture && baseCameraHolder.ParameterHandler.ZSL != null && baseCameraHolder.ParameterHandler.ZSL.IsSupported() && baseCameraHolder.ParameterHandler.ZSL.GetValue().equals("on"))
            {
                baseCameraHolder.errorHandler.OnError("Error: Disable ZSL for Raw or Dng capture");
                this.isWorking = false;
                return false;
            }
            startworking();
            LoadAEB();
            if (aeBrackethdr && baseCameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg"))
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
                    e.printStackTrace();
                }

                final String picFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
                if (picFormat.equals("jpeg")) {
                    final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, HdrModule.this, handler, Settings.GetWriteExternal());
                    jpegSaver.TakePicture();
                } else if (!ParameterHandler.IsDngActive() && picFormat.contains("raw")) {
                    final RawSaver rawSaver = new RawSaver(baseCameraHolder, HdrModule.this, handler, Settings.GetWriteExternal());
                    rawSaver.TakePicture();
                } else if (ParameterHandler.IsDngActive() && picFormat.contains("dng")) {
                    DngSaver dngSaver = new DngSaver(baseCameraHolder, HdrModule.this, handler, Settings.GetWriteExternal());
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
        MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
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
                value = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_AEB1));
            } else if (hdrCount == 1)
                value = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_AEB2));
            else if (hdrCount == 2)
                value = Integer.parseInt(Settings.getString(AppSettingsManager.SETTING_AEB3));

            Log.d(TAG, "Set HDR Exposure to :" + value + "for image count " + hdrCount);

            //fix in some future date 03/03/2016 01:27
            //ParameterHandler.ManualExposure.SetValue(value);
            ((CamParametersHandler)ParameterHandler).setString("exposure-compensation", value+"");
            Log.d(TAG, "HDR Exposure SET");
        }
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
            final String picFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg")) {
                final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, aeBracketDone, handler,Settings.GetWriteExternal());
                jpegSaver.saveBytesToFile(data, new File(StringUtils.getFilePathHDR(Settings.GetWriteExternal(), jpegSaver.fileEnding, hdrCount)));
            }
            else if (picFormat.equals("jps")) {
                final JpsSaver jpsSaver = new JpsSaver(baseCameraHolder, aeBracketDone, handler,Settings.GetWriteExternal());
                jpsSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathHDR(Settings.GetWriteExternal(), jpsSaver.fileEnding, hdrCount)));
            }
            else if (!ParameterHandler.IsDngActive() && picFormat.contains("raw")) {
                final RawSaver rawSaver = new RawSaver(baseCameraHolder, aeBracketDone, handler,Settings.GetWriteExternal());
                rawSaver.saveBytesToFile(data,  new File(StringUtils.getFilePathHDR(Settings.GetWriteExternal(), rawSaver.fileEnding, hdrCount)));
            } else if (ParameterHandler.IsDngActive() && picFormat.contains("dng")) {
                DngSaver dngSaver = new DngSaver(baseCameraHolder, aeBracketDone, handler,Settings.GetWriteExternal());
                dngSaver.processData(data, new File(StringUtils.getFilePathHDR(Settings.GetWriteExternal(), dngSaver.fileEnding, hdrCount)));
            }
        }
    };

    I_WorkeDone aeBracketDone = new I_WorkeDone() {
        @Override
        public void OnWorkDone(File file) {
            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
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
        if ((ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported()))
        {
            if (baseCameraHolder.ParameterHandler.PictureFormat.GetValue().equals("jpeg")) {
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
