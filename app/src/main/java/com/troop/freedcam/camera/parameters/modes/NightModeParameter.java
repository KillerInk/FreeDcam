package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeParameter extends BaseModeParameter
{
    public NightModeParameter(HashMap<String,String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isZTEADV())
            return true;
        else
            return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        parameters.put("night_key", valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
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
