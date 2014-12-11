package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class ContrastManualParameter extends BaseManualParameter {
    public ContrastManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "contrast";
        if (hasSupport()) {
            int max = 100;
            try {
                max = Integer.parseInt(parameters.get("max-contrast"));
                max_value = "max-contrast";
            } catch (Exception ex) {
            }
            try {
                max = Integer.parseInt(parameters.get("contrast-max"));
                max_value = "contrast-max";
            } catch (Exception ex) {
            }

            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("min-contrast"));
                min_value = "min-contrast";
            } catch (Exception ex) {
            }
            try {

                min = Integer.parseInt(parameters.get("contrast-min"));
                min_value = "contrast-min";
            } catch (Exception ex) {
            }
        }
    }
}
