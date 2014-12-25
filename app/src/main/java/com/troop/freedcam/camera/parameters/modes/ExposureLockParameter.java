package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter {
    public ExposureLockParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        if (parameters.isAutoExposureLockSupported())
            return true;
        else
            return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        boolean toset = false;
        if (valueToSet.equals("true"))
            toset = true;
        if (parameters.isAutoExposureLockSupported())
            parameters.setAutoExposureLock(toset);
        if (parameters.isAutoWhiteBalanceLockSupported())
            parameters.setAutoWhiteBalanceLock(toset);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
    }

    @Override
    public String GetValue()
    {
        if (parameters.getAutoExposureLock())
            return "true";
        else
            return "false";
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true", "false"};
    }
}
