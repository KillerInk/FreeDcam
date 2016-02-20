package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class SaturationManualParameter extends BaseManualParameter
{
    public SaturationManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "saturation";
        if (hasSupport())
        {
            if (parameters.containsKey("saturation-value") && parameters.get("saturation-values").contains("middle"))
            {
                this.isSupported = false;
                return;
            }
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
            Set_Default_Value(GetValue());
            stringvalues = createStringArray(min,max,1);
        }
    }
}
