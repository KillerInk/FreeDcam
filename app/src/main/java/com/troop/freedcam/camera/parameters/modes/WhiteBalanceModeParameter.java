package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.HashMap;

/**
 * Created by troop on 24.08.2014.
 */
public class WhiteBalanceModeParameter extends BaseModeParameter {
    public WhiteBalanceModeParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
        if (!parameters.get("whitebalance").equals(""))
            isSupported = true;
    }
}
