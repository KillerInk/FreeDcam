package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class PictureFormatParameter extends BaseModeParameter
{
    CamParametersHandler camParametersHandler;
    AppSettingsManager appSettingsManager;
    public PictureFormatParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CamParametersHandler camParametersHandler, AppSettingsManager appSettingsManager) {
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
        {
            String[] supervals = super.GetValues();
            ArrayList<String> list = new ArrayList<String>();
            for (String s : supervals)
            {
                //if (s.contains("bayer") || s.contains("jpeg") || s.contains("jps") || s.contains("raw"))
                    list.add(s);
            }
            return list.toArray(new String[list.size()]);
        }

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
