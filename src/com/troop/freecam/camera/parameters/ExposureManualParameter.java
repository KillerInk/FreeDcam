package com.troop.freecam.camera.parameters;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    public ExposureManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);
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
