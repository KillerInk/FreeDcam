package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 24.08.2014.
 */
public class PictureFormatParameter extends BaseModeParameter
{
    CamParametersHandler camParametersHandler;
    AppSettingsManager appSettingsManager;
    public PictureFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, CamParametersHandler camParametersHandler, AppSettingsManager appSettingsManager) {
        super(parameters, parameterChanged, value, values);
        this.camParametersHandler = camParametersHandler;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        //TODO this is ugly need to find a different way.. class design fail
        if (camParametersHandler.rawSupported)
        {
            if (valueToSet.equals("raw") || valueToSet.equals("dng"))
            {
                //galaxy nexus and atrix2
                if (DeviceUtils.isOmap() && !DeviceUtils.isO3d())
                {
                    super.SetValue("raw", true);
                }
                else if (DeviceUtils.isMediaTekTHL5000())
                {
                    //set raw
                    camParametersHandler.setTHL5000Raw(true);
                }
                else if (camParametersHandler.BayerMipiFormat != null)
                    super.SetValue(camParametersHandler.BayerMipiFormat, true);
                else if (DeviceUtils.isXperiaL())
                    super.SetValue("raw", true);
                else
                {
                    super.SetValue(valueToSet, false);
                }
            }
            else
            {
                if (DeviceUtils.isMediaTekTHL5000())
                {
                    //set jpeg
                    camParametersHandler.setTHL5000Raw(false);
                }
                else
                    super.SetValue(valueToSet, true);
            }
        }
        else
            super.SetValue(valueToSet, true);
    }

    @Override
    public String[] GetValues()
    {
        if ((camParametersHandler.dngSupported && camParametersHandler.rawSupported && camParametersHandler.BayerMipiFormat != null)
                || DeviceUtils.isXperiaL())
            return new String[]{"jpeg", "raw", "dng"};
        else
        {
            return parameters.get(values).split(",");
        }
    }

    @Override
    public String GetValue()
    {
        String settingValue = appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
        if (camParametersHandler.rawSupported)
        {

            if (settingValue.equals("raw") || settingValue.equals("dng"))
            {
                if (DeviceUtils.isMediaTekTHL5000())
                {
                    camParametersHandler.setTHL5000Raw(true);
                }
                else
                {
                    //BayerMipiFormat is null if its not in the picture-formats
                    if (camParametersHandler.BayerMipiFormat != null)
                        super.SetValue(camParametersHandler.BayerMipiFormat, false);
                    else
                    {
                        super.SetValue(settingValue, false);
                    }
                }
            }
        }
        //process all other devices
        else
        {
            if (settingValue.equals(""))
                appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, super.GetValue());
            super.SetValue(settingValue, false);
        }
        return settingValue;
    }
}
