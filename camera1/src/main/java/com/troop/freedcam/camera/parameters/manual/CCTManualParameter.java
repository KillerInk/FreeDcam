package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.androiddng.DeviceUtils;

import java.util.HashMap;

public class CCTManualParameter extends BaseManualParameter {
	
	I_CameraHolder baseCameraHolder;
    public CCTManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue,AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

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
        if (DeviceUtils.isHTC_M8() || DeviceUtils.isZTEADV())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {
    	if (DeviceUtils.isZTEADV())
    		return Integer.parseInt(parameters.get("max-wb-cct"));
			if (DeviceUtils.isHTC_M8())
    		return Integer.parseInt(parameters.get("max-wb-ct"));
        return 80;
    }
//M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue() {
        if (DeviceUtils.isZTEADV())
			return -1;
		if (DeviceUtils.isHTC_M8())
			return Integer.parseInt(parameters.get("min-wb-ct"));
		return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isHTC_M8())
                i = Integer.parseInt(parameters.get("wb-current-ct"));
            if (DeviceUtils.isZTEADV())
                i = -1;
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
    {   if (DeviceUtils.isZTEADV()){

        if(valueToSet != -1)
        {
            camParametersHandler.WhiteBalanceMode.SetValue("manual-cct", true);
            parameters.put("wb-manual-cct", valueToSet + "");
        }
        else
            camParametersHandler.WhiteBalanceMode.SetValue("auto", true);


    }
        if (DeviceUtils.isHTC_M8())
            parameters.put("wb-ct", valueToSet + "");
        camParametersHandler.SetParametersToCamera();

    }

}


