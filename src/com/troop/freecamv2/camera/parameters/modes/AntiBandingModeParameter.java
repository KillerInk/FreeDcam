package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 17.08.2014.
 */
public class AntiBandingModeParameter extends BaseModeParameter
{
    public AntiBandingModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);
        try
        {
            parameters.get(values);
            isSupported = true;
        }
        catch (Exception ex)
        {
        }
    }
}
