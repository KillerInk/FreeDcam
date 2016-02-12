package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class FXManualParameter extends BaseManualParameter {
	
	BaseCameraHolder baseCameraHolder;
    public FXManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
  /*  public FXManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }*/
    
    @Override
    public boolean IsSupported()
    {

            return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES);
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
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) );
                i = 0;
        }
        catch (Exception ex)
        {

        }

        return i;
    }

    @Override
    protected void setvalue(int valueToSet)
    {   
    	parameters.put("morpho_effect_type", String.valueOf(valueToSet));
        camParametersHandler.SetParametersToCamera();

    }

}