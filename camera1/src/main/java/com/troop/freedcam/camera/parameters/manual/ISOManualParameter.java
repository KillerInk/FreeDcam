package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

public class ISOManualParameter extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    public ISOManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public ISOManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
        if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
            this.isSupported = false;
        else if (DeviceUtils.isAlcatel_Idol3() || DeviceUtils.isMoto_MSM8982_8994())
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
    }

    @Override
    public boolean IsSupported()
    {

        return isSupported;

    }

    @Override
    public int GetMaxValue() {

        if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
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

            if (DeviceUtils.isHTC_M8() || DeviceUtils.isHTC_M9())
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

        if (DeviceUtils.isHTC_M8() || DeviceUtils.isHTC_M9()) {
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
    {   if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9()) {
        parameters.put("iso-st", valueToSet + "");
    }

        else
    {
        camParametersHandler.IsoMode.SetValue("manual", true);
        parameters.put("iso", valueToSet + "");
    }

    }

}


