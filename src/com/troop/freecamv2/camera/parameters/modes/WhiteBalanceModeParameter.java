package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 24.08.2014.
 */
public class WhiteBalanceModeParameter extends BaseModeParameter {
    public WhiteBalanceModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }
}
