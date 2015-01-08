package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class PictureFormatParameter extends BaseModeParameter
{
    CamParametersHandler camParametersHandler;
    AppSettingsManager appSettingsManager;
    public PictureFormatParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values, CamParametersHandler camParametersHandler, AppSettingsManager appSettingsManager) {
        super(parameters, parameterChanged, value, values);
        this.camParametersHandler = camParametersHandler;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (DeviceUtils.isMediaTekDevice())
        {
            if (valueToSet.equals("raw"))
                camParametersHandler.setTHL5000Raw(true);
            else
                camParametersHandler.setTHL5000Raw(false);
        }
        else
        {
            super.SetValue(valueToSet, true);
        }

    }

    @Override
    public String[] GetValues()
    {
        if (DeviceUtils.isMediaTekDevice())
            return new String[]{"jpeg", "raw"};
        else
            return super.GetValues();

    }

    @Override
    public String GetValue()
    {
        if (DeviceUtils.isMediaTekDevice())
        {
            return appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
        }
        else
            return super.GetValue();

    }
}
