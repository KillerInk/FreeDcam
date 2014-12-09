package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    public ExposureManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, CamParametersHandler camParametersHandler) {
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
    public void SetValue(int valueToSet) {
        parameters.setExposureCompensation(valueToSet);
    }
}
