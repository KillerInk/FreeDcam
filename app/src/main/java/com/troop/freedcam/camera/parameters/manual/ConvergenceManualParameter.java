package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class ConvergenceManualParameter extends BaseManualParameter{
    public ConvergenceManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, CamParametersHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        hasSupport();
    }
}
