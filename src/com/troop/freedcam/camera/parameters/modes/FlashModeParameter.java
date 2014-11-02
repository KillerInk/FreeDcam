package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 17.08.2014.
 */
public class FlashModeParameter extends BaseModeParameter {
    public FlashModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);
        try {
            if (!parameters.getFlashMode().equals(""))
                isSupported = true;
        }
        catch (Exception ex)
        {
            isSupported = false;
        }

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        parameters.setFlashMode(valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        firststart = false;
    }

    @Override
    public String[] GetValues() {
        return parameters.getSupportedFlashModes().toArray(new String[parameters.getSupportedFlashModes().size()]);
    }

    @Override
    public String GetValue() {
        return parameters.getFlashMode();
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }
}
