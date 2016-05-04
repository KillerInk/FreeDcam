package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

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
    public SkintoneManualPrameter(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(parameters, "", "", "", camParametersHandler,1);
        try
        {
            /*final String skin = parameters.get("skinToneEnhancement");
            if (skin != null && !skin.equals("")) {
                this.isSupported = true;
                this.value = "skinToneEnhancement";
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
    protected void setvalue(int valueToSet) {
        camParametersHandler.SceneMode.SetValue("portrait", true);
        parameters.put("skinToneEnhancement",valueToSet + "");
        if (valueToSet == 0)
            camParametersHandler.SceneMode.SetValue("auto", true);
    }
}
