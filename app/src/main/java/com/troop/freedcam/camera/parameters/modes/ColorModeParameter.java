package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 17.08.2014.
 */
public class ColorModeParameter extends BaseModeParameter {
    public ColorModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);
        if (!parameters.getColorEffect().equals(""))
            isSupported = true;
    }

    @Override
    public String GetValue()
    {
        return parameters.getColorEffect();
    }

    @Override
    public String[] GetValues() {
        return parameters.getSupportedColorEffects().toArray(new String[parameters.getSupportedColorEffects().size()]);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        parameters.setColorEffect(valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        firststart = false;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }
}
