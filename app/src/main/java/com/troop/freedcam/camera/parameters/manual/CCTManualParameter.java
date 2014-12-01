package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.camera.BaseCameraHolder;

public class CCTManualParameter extends BaseManualParameter {
	
	BaseCameraHolder baseCameraHolder;
    public CCTManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);

        //TODO add missing logic
    }
    public CCTManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder) {
        super(parameters, value, maxValue, MinValue);

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
    		return parameters.getInt("max-wb-cct");
			if (DeviceUtils.isHTC_M8())
    		return parameters.getInt("max-wb-ct");
        return 80;
    }
//M8 Step values "wb-ct-step"
    @Override
    public int GetMinValue() {
        if (DeviceUtils.isZTEADV())
			return parameters.getInt("min-wb-cct");
		if (DeviceUtils.isHTC_M8())
			return parameters.getInt("min-wb-ct");
		return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isHTC_M8())
                i = parameters.getInt("wb-current-ct");
            if (DeviceUtils.isZTEADV());
                i = parameters.getInt("wb-current-cct");
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {   if (DeviceUtils.isZTEADV())
			parameters.setWhiteBalance("manual-cct");   
			parameters.set("wb-manual-cct", valueToSet); 
		if (DeviceUtils.isHTC_M8())
			parameters.set("wb-ct", valueToSet);

     }

    }


