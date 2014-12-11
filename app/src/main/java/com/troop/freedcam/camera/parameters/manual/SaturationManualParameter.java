package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class SaturationManualParameter extends BaseManualParameter
{
    public SaturationManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "saturation";
        if (hasSupport())
        {
            int max = 0;
            try {
                max = Integer.parseInt(parameters.get("max-saturation"));
                this.max_value = "max-saturation";
            }
            catch (Exception ex)
            {
            }
            try
            {
                max = Integer.parseInt(parameters.get("saturation-max"));
                this.max_value = "saturation-max";
            }
            catch (Exception ex)
            {}

            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("min-saturation"));
                this.min_value = "min-saturation";
            }
            catch (Exception ex)
            {
            }
            try
            {
                min = Integer.parseInt(parameters.get("saturation-min"));
                this.min_value = "saturation-min";
            }
            catch (Exception ex)
            {}
        }
    }
}
