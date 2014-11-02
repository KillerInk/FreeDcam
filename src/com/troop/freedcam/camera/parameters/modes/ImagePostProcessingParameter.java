package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 19.08.2014.
 */
public class ImagePostProcessingParameter extends BaseModeParameter
{
    public ImagePostProcessingParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);
        try {
            String ipps = parameters.get("ipp-values");
            if (!ipps.isEmpty())
                isSupported = true;
        }
        catch (Exception ex)
        {

        }
    }
}
