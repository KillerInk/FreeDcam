package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class IsoModeParameter extends BaseModeParameter
{
    public IsoModeParameter(Camera.Parameters parameters, String value, String values)
    {
        super(parameters, value, values);
        try
        {
            parameters.get("iso-mode-values");
            value = "iso";
            values = "iso-mode-values";
            isSupported = true;
        }
        catch (Exception ex){}
        if (!isSupported)
        {
            try {
                parameters.get("iso-values").split(",");
                value = "iso";
                values = "iso-values";
                isSupported = true;
            } catch (Exception ex) {}
        }
    }
}
