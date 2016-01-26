package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeParameter extends BaseModeParameter
{
    public NightModeParameter(Handler handler,HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values) {
        super(handler, parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported()
    {
        this.isSupported = false;
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234()||DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote())
            this.isSupported = true;
        BackgroundSetIsSupportedHasChanged(isSupported);
        return  isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote())
            if (valueToSet == "true")
                {
                    parameters.put("capture-burst-exposures","-10,0,10");
                    parameters.put("ae-bracket-hdr","AE-Bracket");
                    parameters.put("morpho-hdr", "false");
                    parameters.put("morpho-hht", "true");
                }
            else
                {
                    //parameters.put("capture-burst-exposures","-10,0,10");
                    parameters.put("ae-bracket-hdr","Off");
                    parameters.put("morpho-hdr", "false");
                    parameters.put("morpho-hht", "false");
                }
        else
            parameters.put("night_key", valueToSet);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        firststart = false;
    }

    @Override
    public String GetValue() {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote())
            return parameters.get("morpho-hht");
        else
            return parameters.get("night_key");
    }

    @Override
    public String[] GetValues() {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W()||DeviceUtils.isXiaomiMI_Note_Pro()||DeviceUtils.isRedmiNote())
        return new String[] {"false","true"};
    else
        return new String[] {"off","on","tripod"};
    }
}