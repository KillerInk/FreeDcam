package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureModeParameter extends BaseModeParameter {
    public ExposureModeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);

        try
        {
            String tmp = parameters.get("exposure-mode-values");
            if(tmp != null && !tmp.equals("")) {
                isSupported = true;
                this.values = "exposure-mode-values";
                this.value = "exposure";
            }
        }
        catch (Exception ex)
        {

        }
        if (isSupported == false)
        {
            try
            {
                String tmp = parameters.get("auto-exposure-values");
                if(tmp != null && !tmp.equals("")) {
                    isSupported = true;
                    this.values = "auto-exposure-values";
                    this.value = "auto-exposure";
                }
            }
            catch (Exception ex)
            {

            }
        }
    }
}
