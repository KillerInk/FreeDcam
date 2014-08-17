package com.troop.freecam.camera.parameters.modes;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public class ColorModeParameter extends BaseModeParameter {
    public ColorModeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);
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
    public void SetValue(String valueToSet) {
        parameters.setColorEffect(valueToSet);
    }
}
