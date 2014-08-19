package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

/**
 * Created by troop on 19.08.2014.
 */
public class ImagePostProcessingParameter extends BaseModeParameter
{
    public ImagePostProcessingParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);
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
