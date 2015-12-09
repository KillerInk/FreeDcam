package com.troop.freedcam.camera.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class BurstManualParam extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    int curr = 0;
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
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234() || DeviceUtils.isLG_G3()|| DeviceUtils.isG2()|| DeviceUtils.isXiaomiMI3W())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {
        if (DeviceUtils.isZTEADV()||DeviceUtils.isZTEADVIMX214()||DeviceUtils.isZTEADV234()|| DeviceUtils.isG2())
            return 7;
        if (DeviceUtils.isLG_G3()||DeviceUtils.isXiaomiMI4W())
            return 9;
        if (DeviceUtils.isXiaomiMI3W())
            //if (baseCameraHolder.ParameterHandler.PictureFormat.GetValue().contains("jpeg"))
            //return 100;
            //else
                return 6;
        else
            return 0;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue()
    {
        return curr;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        if (DeviceUtils.isXiaomiMI3W()||DeviceUtils.isXiaomiMI4W())
            parameters.put("num-snaps-per-shutter", String.valueOf(1));
        curr = valueToSet;
        parameters.put("snapshot-burst-num", String.valueOf(valueToSet));
        camParametersHandler.SetParametersToCamera();

    }

    @Override
    public String GetStringValue() {
        return curr +"";
    }
}
