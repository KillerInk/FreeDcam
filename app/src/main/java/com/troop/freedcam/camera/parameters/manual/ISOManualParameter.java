package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.camera.BaseCameraHolder;

public class ISOManualParameter extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    public ISOManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public ISOManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isHTC_M8())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {

        if (DeviceUtils.isHTC_M8())
            return 6400;
        return 80;
    }

    @Override
    public int GetMinValue() {

        if (DeviceUtils.isHTC_M8())
            return 64;
        return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 400;
        try {
                i = parameters.getInt("iso-st");

        }
        catch (Exception ex)
        {

        }
        return i;
    }

    @Override
    protected void setvalue(int valueToSet)
    {   	if (DeviceUtils.isHTC_M8())
        parameters.set("iso-st", valueToSet);

    }

}


