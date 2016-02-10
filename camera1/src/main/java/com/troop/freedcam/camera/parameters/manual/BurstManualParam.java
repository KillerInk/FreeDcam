package com.troop.freedcam.camera.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import android.os.Build;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class BurstManualParam extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    int curr = 0;
    public BurstManualParam(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) ||
                DeviceUtils.IS(DeviceUtils.Devices.LG_G3)|| DeviceUtils.IS(DeviceUtils.Devices.LG_G2)|| DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            isSupported = true;

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
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetMaxValue()
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) || DeviceUtils.IS(DeviceUtils.Devices.LG_G2))
            return 7;
        else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G3)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W) )
            return 9;
        else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W))
            if (Build.VERSION.SDK_INT < 23)
                return 6;
            else
                return 10;
        else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
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
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
            parameters.put("num-snaps-per-shutter", String.valueOf(1));
        curr = valueToSet;
        parameters.put("snapshot-burst-num", String.valueOf(valueToSet));
        camParametersHandler.SetParametersToCamera();

    }

    @Override
    public String GetStringValue() {
        return curr +"";
    }

    @Override
    public I_ModuleEvent GetModuleListner() {
        return moduleListner;
    }

    private I_ModuleEvent moduleListner =new I_ModuleEvent() {
        @Override
        public String ModuleChanged(String module)
        {
            if ((module.equals(AbstractModuleHandler.MODULE_VIDEO) || module.equals(AbstractModuleHandler.MODULE_HDR)) && isSupported)
                BackgroundIsSupportedChanged(false);
            else if ((module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    )&& isSupported)
            {
                BackgroundIsSupportedChanged(true);
            }
            return null;
        }
    };

}
