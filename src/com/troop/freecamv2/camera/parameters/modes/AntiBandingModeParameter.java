package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class AntiBandingModeParameter extends BaseModeParameter
{
    public AntiBandingModeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);
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
