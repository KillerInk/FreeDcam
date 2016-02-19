package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class SharpnessManualParameter extends BaseManualParameter
{
    private int step = 1;

    public SharpnessManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "sharpness";
        if (hasSupport())
        {
            int max = 0;
            int min = 0;
            if (parameters.containsKey("sharpness-max"))
            {
                max = Integer.parseInt(parameters.get("sharpness-max"));
                max_value = "sharpness-max";
            }
            if (parameters.containsKey("max-sharpness"))
            {
                max = Integer.parseInt(parameters.get("max-sharpness"));
                max_value = "max-sharpness";
            }
            if (parameters.containsKey("sharpness-min"))
            {
                min = Integer.parseInt(parameters.get("sharpness-min"));
                max_value = "sharpness-min";
            }
            if (parameters.containsKey("min-sharpness"))
            {
                min = Integer.parseInt(parameters.get("min-sharpness"));
                max_value = "min-sharpness";
            }
            if (parameters.containsKey("sharpness-step"))
                step = Integer.parseInt(parameters.get("sharpness-step"));
            Set_Default_Value(GetValue());
            stringvalues = createStringArray(min,max,step);
        }
    }

}
