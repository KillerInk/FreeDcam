package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ConvergenceManualParameter extends BaseManualParameter{
    public ConvergenceManualParameter(HashMap<String, String> parameters, String value, String maxValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min", camParametersHandler);
        hasSupport();
    }
}
