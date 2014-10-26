package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 23.10.2014.
 */
public class AE_Bracket_HdrModeParameter extends BaseModeParameter {
    public AE_Bracket_HdrModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }
}
