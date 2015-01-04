package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.HashMap;

/**
 * Created by troop on 02.09.2014.
 */
public class FocusModeParameter extends BaseModeParameter
{
    public FocusModeParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }
}
