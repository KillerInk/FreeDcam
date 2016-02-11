package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class SharpnessManualParameter extends BaseManualParameter
{
    public SharpnessManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "sharpness";
        if (hasSupport()) {
            int max = 0;
            try {
                max = Integer.parseInt(parameters.get("sharpness-max"));
                max_value = "sharpness-max";
            } catch (Exception ex) {
            }
            try {
                max = Integer.parseInt(parameters.get("max-sharpness"));
                max_value = "max-sharpness";
            } catch (Exception ex) {
            }

            try {
                max = Integer.parseInt(parameters.get("sharpness-min"));
                min_value = "sharpness-min";
            } catch (Exception ex) {
            }
            try {
                max = Integer.parseInt(parameters.get("min-sharpness"));
                min_value = "min-sharpness";
            } catch (Exception ex) {
            }
            Set_Default_Value(GetValue());
        }
    }
}
