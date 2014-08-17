package com.troop.freecam.camera.parameters.modes;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class FlashModeParameter extends BaseModeParameter {
    public FlashModeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);
        if (!parameters.getFlashMode().equals(""))
            isSupported = true;
    }

    @Override
    public void SetValue(String valueToSet) {
        parameters.setFlashMode(valueToSet);
    }

    @Override
    public String[] GetValues() {
        return parameters.getSupportedFlashModes().toArray(new String[parameters.getSupportedFlashModes().size()]);
    }

    @Override
    public String GetValue() {
        return parameters.getFlashMode();
    }
}
