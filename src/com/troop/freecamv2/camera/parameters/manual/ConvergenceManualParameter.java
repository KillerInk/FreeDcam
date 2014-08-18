package com.troop.freecamv2.camera.parameters.manual;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class ConvergenceManualParameter extends BaseManualParameter{
    public ConvergenceManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);
        hasSupport();
    }
}
