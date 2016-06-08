/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks.PictureCallback;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;


/**
 * Created by troop on 16.08.2014.
 */
public class BracketModule extends PictureModule
{

    private final String TAG = BracketModule.class.getSimpleName();

    int hdrCount = 0;
    boolean aeBrackethdr = false;
    File[] files;
    boolean isManualExpo = false;
    int ogExpoValue = 0;
    private Context context;

    public BracketModule(CameraHolder cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler, context,appSettingsManager);
        name = KEYS.MODULE_HDR;
        this.context = context;
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
            String picformat = ParameterHandler.PictureFormat.GetValue();
            if (picformat.equals(KEYS.DNG) ||picformat.equals(KEYS.BAYER))
            {
                if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
                    ParameterHandler.ZSL.SetValue("off", true);
            }
            changeWorkState(CaptureModes.image_capture_start);
            waitForPicture = true;
            loade_ae_bracket();
            if (aeBrackethdr && ParameterHandler.PictureFormat.GetValue().equals(KEYS.JPEG))
            {
                cameraHolder.TakePicture(null, aeBracketCallback);
            }
            else
            {
                setExposureToCamera();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
                cameraHolder.TakePicture(null, this);
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
    public void InitModule()
    {
        loade_ae_bracket();
    }

    @Override
    public void DestroyModule()
    {
        if (aeBrackethdr)
            ParameterHandler.AE_Bracket.SetValue("Off", true);
    }

    //I_Module END

    private void setExposureToCamera()
    {
        ogExpoValue =  ParameterHandler.ManualExposure.GetValue();

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
                value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB1));
            } else if (hdrCount == 1)
                value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB2));
            else if (hdrCount == 2)
                value = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AEB3));

            Logger.d(TAG, "Set HDR Exposure to :" + value + "for image count " + hdrCount);
           // ParameterHandler.ManualExposure.SetValue(key_value);

            /*checkAEMODE();
            if(isManualExpo)
            {
                ParameterHandler.ManualShutter.SetValue(DoStopCalc(key_value));
            }
           TODO */
            ParameterHandler.SetEVBracket(value + "");
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
        if (!ParameterHandler.ManualShutter.GetStringValue().equals(KEYS.AUTO))
            isManualExpo = true;
    }

    private void getStop(float current, float TargetStop)
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
    }

    PictureCallback aeBracketCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data) {
            if (!waitForPicture)
                return;
            hdrCount++;
            String picFormat = ParameterHandler.PictureFormat.GetValue();
            saveImage(data,picFormat);
            if (hdrCount == 3)//handel normal capture
            {
                waitForPicture = false;
                changeWorkState(CaptureModes.image_capture_stop);
                cameraHolder.StartPreview();

            }

        }
    };

    private void loade_ae_bracket()
    {
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
        {
            if (ParameterHandler.PictureFormat.GetValue().equals(KEYS.JPEG)) {
                aeBrackethdr = true;
                ParameterHandler.AE_Bracket.SetValue("AE-Bracket", true);
            }
            else {
                aeBrackethdr = false;
                ParameterHandler.AE_Bracket.SetValue("Off", true);
            }

        }

    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                if (!waitForPicture)
                {
                    isWorking = false;
                    return;
                }
                hdrCount++;
                String picFormat = ParameterHandler.PictureFormat.GetValue();
                saveImage(data,picFormat);
                cameraHolder.StartPreview();
                if (hdrCount == 3)//handel normal capture
                {
                    waitForPicture = false;
                    isWorking = false;
                    changeWorkState(CaptureModes.image_capture_stop);
                }
                else
                {
                    setExposureToCamera();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        Logger.exception(e);
                    }
                    cameraHolder.TakePicture(null,BracketModule.this);
                }
            }
        });
    }

    protected File getFile(String fileending)
    {
        return new File(StringUtils.getFilePathHDR(appSettingsManager.GetWriteExternal(), fileending, hdrCount));
    }

}
