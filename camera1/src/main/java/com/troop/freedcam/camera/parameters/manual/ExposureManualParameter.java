package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    public ExposureManualParameter(HashMap<String, String> parameters, String value, String maxValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, "exposure-compensation", "max-exposure-compensation", "min-exposure-compensation", camParametersHandler);

        this.value = "exposure-compensation";
        super.hasSupport();

    }



}
