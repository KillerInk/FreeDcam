package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
    }

    @Override
    public boolean IsSupported()
    {
        return parameters.isZoomSupported();
    }

    @Override
    public int GetMaxValue() {
        return parameters.getMaxZoom();
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue() {
        return parameters.getZoom();
    }

    @Override
    protected void setvalue(int valueToset) {
        parameters.setZoom(valueToset);
        camParametersHandler.SetParametersToCamera();
    }
}
