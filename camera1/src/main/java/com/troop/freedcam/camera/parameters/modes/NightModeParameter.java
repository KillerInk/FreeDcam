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
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234())
            this.isSupported = true;
        BackgroundSetIsSupportedHasChanged(isSupported);
        return  isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
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
        return parameters.get("night_key");
    }

    @Override
    public String[] GetValues() {
        return new String[] {"off","on","tripod"};
    }
}
