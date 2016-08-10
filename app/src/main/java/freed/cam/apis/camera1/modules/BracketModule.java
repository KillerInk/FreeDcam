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

package freed.cam.apis.camera1.modules;

import android.hardware.Camera;

import java.io.File;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.utils.AppSettingsManager;
import freed.utils.FreeDPool;
import freed.utils.Logger;


/**
 * Created by troop on 16.08.2014.
 */
public class BracketModule extends PictureModule
{

    private final String TAG = BracketModule.class.getSimpleName();

    int hdrCount;
    boolean aeBrackethdr;
    File[] files;
    boolean isManualExpo;
    int ogExpoValue;

    public BracketModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_HDR;
    }

    //ModuleInterface START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
        {
            if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
                cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
            files = new File[3];
            hdrCount = 0;
            String picformat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
            if (picformat.equals(KEYS.DNG) ||picformat.equals(KEYS.BAYER))
            {
                if (cameraUiWrapper.GetParameterHandler().ZSL != null && cameraUiWrapper.GetParameterHandler().ZSL.IsSupported()
                        && cameraUiWrapper.GetParameterHandler().ZSL.GetValue().equals("on")
                        && ((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != CameraHolder.Frameworks.MTK)
                    cameraUiWrapper.GetParameterHandler().ZSL.SetValue("off", true);
            }
            changeCaptureState(CaptureStates.image_capture_start);
            waitForPicture = true;
            loade_ae_bracket();
            if (aeBrackethdr && cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue().equals(KEYS.JPEG))
            {
                cameraHolder.TakePicture(aeBracketCallback);
            }
            else
            {
                setExposureToCamera();
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Logger.exception(e);
                }
                cameraHolder.TakePicture(this);
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
        super.InitModule();
        loade_ae_bracket();
    }

    @Override
    public void DestroyModule()
    {
        if (aeBrackethdr)
            cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue("Off", true);
    }

    //ModuleInterface END

    private void setExposureToCamera()
    {
        ogExpoValue = cameraUiWrapper.GetParameterHandler().ManualExposure.GetValue();

        if(isManualExpo)
        {
            if(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().contains("/")) {
                int value = 0;

                if (hdrCount == 0)
                {
                    System.out.println("Do Nothing");
                    //getStop(1 / Integer.parseInt(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().split("/")[1]), -12);
                }
                else if (hdrCount == 1)
                    getStop(1 / Integer.parseInt(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().split("/")[1]), -12.0f);
                else if (hdrCount == 2)
                    getStop(1 / Integer.parseInt(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().split("/")[1]), 12.0f);
                //cameraUiWrapper.GetParameterHandler().ManualShutter.SetValue();
            }
            else
            {
                if (hdrCount == 0)
                {
                    //getStop(Float.parseFloat(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue()), -12);
                    System.out.println("Do Nothing");
                }
                else if (hdrCount == 1)
                    getStop(Float.parseFloat(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue()), -12.0f);
                else if (hdrCount == 2)
                    getStop(Float.parseFloat(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue()), 12.0f);
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
           // cameraUiWrapper.GetParameterHandler().ManualExposure.SetValue(key_value);

            /*checkAEMODE();
            if(isManualExpo)
            {
                cameraUiWrapper.GetParameterHandler().ManualShutter.SetValue(DoStopCalc(key_value));
            }
           TODO */
            cameraUiWrapper.GetParameterHandler().SetEVBracket(value + "");
            Logger.d(TAG, "HDR Exposure SET");
        }
    }

    private String DoStopCalc(int stop)
    {
        float shutterString = 0.0f;

        if(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().contains("/"))
        {
            shutterString = Float.parseFloat(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().split("/")[1]);
        }
        else
            shutterString = Float.parseFloat(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue());


        float StoppedShift;
        if (stop < 0)
            StoppedShift = shutterString / (stop*4);
        else
            StoppedShift = shutterString * (stop*4);

        return "1/"+ shutterString;
    }

    private void checkAEMODE()
    {
        if (!cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().equals(KEYS.AUTO))
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

    Camera.PictureCallback aeBracketCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (!waitForPicture)
                return;
            hdrCount++;
            String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
            saveImage(data,picFormat);
            if (hdrCount == 3)//handel normal capture
            {
                waitForPicture = false;
                changeCaptureState(CaptureStates.image_capture_stop);
                cameraHolder.StartPreview();

            }
        }
    };

    private void loade_ae_bracket()
    {
        if (cameraUiWrapper.GetParameterHandler().AE_Bracket != null && cameraUiWrapper.GetParameterHandler().AE_Bracket.IsSupported())
        {
            if (cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue().equals(KEYS.JPEG)) {
                aeBrackethdr = true;
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue("AE-Bracket", true);
            }
            else {
                aeBrackethdr = false;
                cameraUiWrapper.GetParameterHandler().AE_Bracket.SetValue("Off", true);
            }

        }

    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera)
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
                String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
                saveImage(data,picFormat);
                cameraHolder.StartPreview();
                if (hdrCount == 3)//handel normal capture
                {
                    waitForPicture = false;
                    isWorking = false;
                    changeCaptureState(CaptureStates.image_capture_stop);
                }
                else
                {
                    setExposureToCamera();
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        Logger.exception(e);
                    }
                    cameraHolder.TakePicture(BracketModule.this);
                }
            }
        });
    }

    protected File getFile(String fileending)
    {
        return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathHDR(appSettingsManager.GetWriteExternal(), fileending, hdrCount));
    }

}
