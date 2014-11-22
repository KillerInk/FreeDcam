package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusModeParameter extends BaseModeParameter
{
    public FocusModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        parameters.setFocusMode(valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        firststart = false;
    }

    @Override
    public String GetValue()
    {

        return parameters.getFocusMode();
    }

    @Override
    public String[] GetValues() {
        return parameters.getSupportedFocusModes().toArray(new String[parameters.getSupportedFocusModes().size()]);
    }
}
