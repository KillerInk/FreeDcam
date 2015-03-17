package com.troop.freedcam.camera.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.androiddng.DeviceUtils;

import java.util.HashMap;

public class BurstManualParam extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    int curr = 1;
    public BurstManualParam(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public BurstManualParam(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isZTEADV())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {

        return 7;
    }

    @Override
    public int GetMinValue() {
        return 1;
    }

    @Override
    public int GetValue()
    {
        return curr;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        curr = valueToSet;
        parameters.put("snapshot-burst-num", String.valueOf(valueToSet));
        camParametersHandler.SetParametersToCamera();

    }
}
