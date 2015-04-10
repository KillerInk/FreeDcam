package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter {
	
	I_CameraHolder baseCameraHolder;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        if (DeviceUtils.isZTEADV())
            this.isSupported = true;
        /*try {
            String t = parameters.get("max-wb-cct");
            if (t != null || t.equals(""))
            {
                this.value = "wb-cct";
                this.max_value = "max-wb-cct";
                this.min_value = "min-wb-cct";
                this.isSupported = true;
            }
        }
        catch (Exception ex)
        {}
        if (!isSupported)
        {
            try {
                String t = parameters.get("max-wb-ct");
                if (t != null || t.equals(""))
                {
                    this.value = "wb-ct";
                    this.max_value = "max-wb-ct";
                    this.min_value = "min-wb-ct";
                    this.isSupported = true;
                }
            }
            catch (Exception ex)
            {}
        }*/
        //TODO add missing logic
    }
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
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
    public int GetMaxValue() {
    	return Integer.parseInt(parameters.get(max_value));

    }
//M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue() {
	    return Integer.parseInt(parameters.get(min_value));
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isZTEADV())
                i = -1;
            else
                i = Integer.parseInt(parameters.get(value));
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    public String GetStringValue() {
        return null;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (DeviceUtils.isZTEADV())
        {
            if(valueToSet != -1)
            {
                camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);
                parameters.put("wb-manual-cct", valueToSet + "");
            }
            else
                camParametersHandler.WhiteBalanceMode.SetValue("auto", true);
        }
        if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
            parameters.put("wb-ct", valueToSet + "");
        camParametersHandler.SetParametersToCamera();

    }

}


