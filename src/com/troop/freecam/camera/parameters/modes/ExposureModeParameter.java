package com.troop.freecam.camera.parameters.modes;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureModeParameter extends BaseModeParameter {
    public ExposureModeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);

        try
        {
            parameters.get("exposure-mode-values").split(",");
            isSupported = true;
            values = "exposure-mode-values";
            value = "exposure";
        }
        catch (Exception ex)
        {

        }
        if (isSupported == false)
        {
            try
            {
                parameters.get("auto-exposure-values").split(",");
                isSupported = true;
                values = "auto-exposure-values";
                value = "auto-exposure";
            }
            catch (Exception ex)
            {

            }
        }
    }
}
