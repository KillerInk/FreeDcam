package com.troop.freecamv2.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freecamv2.utils.DeviceUtils;
import com.troop.freecamv2.camera.BaseCameraHolder;

public class ISOManualParameter extends BaseManualParameter {
	
	BaseCameraHolder baseCameraHolder;
    public ISOManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);

        //TODO add missing logic
    }
    public ISOManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder) {
        super(parameters, value, maxValue, MinValue);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }
    
    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isHTCADV())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {
    	
			if (DeviceUtils.isHTCADV())
				return 6400;
			return 80;
    }

    @Override
    public int GetMinValue() {
        
		if (DeviceUtils.isHTCADV())
			return 64;
		return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isHTCADV())
                i = parameters.getInt("iso_value");
            
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {   	if (DeviceUtils.isHTCADV())
				parameters.set("iso-st", valueToSet);

     }

    }


