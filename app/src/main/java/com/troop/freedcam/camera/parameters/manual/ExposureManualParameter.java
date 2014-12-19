package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    public ExposureManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public int GetMinValue() {
        return parameters.getMinExposureCompensation();
    }

    @Override
    public int GetMaxValue() {
        return parameters.getMaxExposureCompensation();
    }

    @Override
    public int GetValue() {
        return parameters.getExposureCompensation();
    }

    @Override
    protected void setvalue(int valueToSet) {
        parameters.setExposureCompensation(valueToSet);
        camParametersHandler.SetParametersToCamera();

    }
}
