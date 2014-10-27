package com.troop.freedcamv2.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freedcamv2.utils.DeviceUtils;
import com.troop.freedcamv2.camera.BaseCameraHolder;

public class FXManualParameter extends BaseManualParameter {
	
	BaseCameraHolder baseCameraHolder;
    public FXManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);

        //TODO add missing logic
    }
    public FXManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder) {
        super(parameters, value, maxValue, MinValue);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }
    
    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isZTEADV())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {
    	
        return 38;
    }

    @Override
    public int GetMinValue() {
         return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isLGADV())
                i = 0;
            if (DeviceUtils.isZTEADV());
                i = 0;
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {   
    	parameters.set("morpho_effect_type", valueToSet);  	

     }

    }