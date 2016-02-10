package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class ISOManualParameter extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    public ISOManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            this.isSupported = false;
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994))
        {
            this.isSupported = true;
            this.max_value = "min-iso";
            this.value = "cur-iso";
            this.min_value = "max-iso";

            if(min_value.equals(null))
            {
                this.isSupported=false;
            }
        }
        else
            this.isSupported = false;
        isVisible = isSupported;
    }

    @Override
    public boolean IsSupported()
    {

        return isSupported;

    }

    @Override
    public int GetMaxValue() {

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return 6400;
        else
        try {
            return Integer.parseInt(max_value);
        }
        catch (NullPointerException ex)
        {
            return 0;
        }

    }

    @Override
    public int GetMinValue() {

            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
                return 64;
            else
                try {
                    return Integer.parseInt(min_value);
                }
                catch (NullPointerException ex)
                {
                    return 0;
                }

        }

    @Override
    public int GetValue() {

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9)) {
            return Integer.parseInt(parameters.get("iso-st"));
        } else {
            try {
                return Integer.parseInt(value);
            } catch (NullPointerException ex) {
                return 0;
            }


        }
    }

    @Override
    protected void setvalue(int valueToSet)
    {   if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9)) {
        parameters.put("iso-st", valueToSet + "");
    }

        else
    {
        camParametersHandler.IsoMode.SetValue("manual", true);
        parameters.put("iso", valueToSet + "");
    }

    }

}


