package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;

public class ISOManualParameter extends BaseManualParameter {

    public ISOManualParameter(Camera.Parameters parameters, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        //TODO add missing logic
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            this.isSupported = false;
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994) || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
        {
            this.isSupported = true;
            this.max_value = "min-iso";
            this.value = "iso";
            this.min_value = "max-iso";

            if(min_value.equals(null))
            {
                this.isSupported=false;
            }
            stringvalues = createStringArray(Integer.parseInt(parameters.get(min_value)),Integer.parseInt(parameters.get(max_value)),100);
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
    public int GetValue() {

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9)) {
            return Integer.parseInt(parameters.get("iso-st"));
        } else {
            try {
                return Integer.parseInt(parameters.get(value));
            } catch (NullPointerException ex) {
                return 0;
            }


        }
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
        {
            parameters.set("iso-st", valueToSet + "");
        }
        else
        {
            //camParametersHandler.IsoMode.SetValue("manual", true);
            parameters.set("iso", stringvalues[valueToSet]);
        }
    }

}


