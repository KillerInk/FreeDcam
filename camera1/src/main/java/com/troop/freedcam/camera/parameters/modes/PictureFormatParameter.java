package com.troop.freedcam.camera.parameters.modes;

import android.os.Build;
import android.os.Handler;

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
    public PictureFormatParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CamParametersHandler camParametersHandler, AppSettingsManager appSettingsManager) {
        super(handler, parameters, parameterChanged, value, values);
        try
        {
            final String t = parameters.get("picture-format");
            if (t != null && !t.equals(""))
            {
                this.value = "picture-format";
                this.values = "picture-format-values";
                this.isSupported = true;
            }
        }
        catch (Exception ex)
        {
            isSupported =false;
        }
        if (!isSupported) {
            try {
                final String t = parameters.get("sony-postview-format");
                if (t != null && !t.equals("")) {
                    this.value = "sony-postview-format";
                    this.values = "sony-postview-format-values";
                    this.isSupported = true;
                }
            } catch (Exception ex) {

            }
        }

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
            if(baseCameraHolder.hasLGFrameWork)
            {
                baseCameraHolder.StopPreview();
                baseCameraHolder.StartPreview();
            }
        }

    }

    @Override
    public String[] GetValues()
    {
        if((Build.MANUFACTURER.contains("samsung") || Build.MANUFACTURER.contains("sony") || Build.MANUFACTURER.contains("Sony")) && !DeviceUtils.isXperiaL())
            return  new String[]{"jpeg"};
        if (DeviceUtils.isMediaTekDevice())
            return new String[]{"jpeg", "raw"};
        /*else if(DeviceUtils.isG2())
        {
            return new String[]{"jpeg", "bayer-mipi-10bggr"};
        }*/
        else
        {
            String[] supervals = super.GetValues();
            ArrayList<String> list = new ArrayList<String>();
            for (String s : supervals)
            {
                if (!s.contains("yuv"))
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
