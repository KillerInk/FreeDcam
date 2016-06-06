package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 12.04.2015.
 */
public class SkintoneManualPrameter extends BaseManualParameter {
    /**
     * @param parameters
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     */
    public SkintoneManualPrameter(Camera.Parameters parameters, String maxValue, String MinValue, CamParametersHandler camParametersHandler)
    {
        super(parameters, "", "", "", camParametersHandler,1);
        try
        {
            /*final String skin = parameters.get("skinToneEnhancement");
            if (skin != null && !skin.equals("")) {
                this.isSupported = true;
                this.key_value = "skinToneEnhancement";
            }*/
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)
                    ||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994)
                    ||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
                this.isSupported = true;

            if (isSupported)
            {
                stringvalues = createStringArray(-100,100,1);
            }
        }
        catch (Exception ex)
        {
            this.isSupported = false;

        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(int valueToSet) {
        camParametersHandler.SceneMode.SetValue("portrait", true);
        parameters.set("skinToneEnhancement",valueToSet + "");
        if (valueToSet == 0)
            camParametersHandler.SceneMode.SetValue("auto", true);
    }
}
