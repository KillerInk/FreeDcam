package com.troop.freedcamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 05.09.2014.
 */
public class RedEyeParameter extends BaseModeParameter {
    public RedEyeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }
}
