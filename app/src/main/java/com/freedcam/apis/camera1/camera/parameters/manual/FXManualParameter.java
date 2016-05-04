package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class FXManualParameter extends BaseManualParameter {
	
	BaseCameraHolder baseCameraHolder;
    public FXManualParameter(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);

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
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
        {
            this.isSupported = true;
            this.isVisible = true;
            stringvalues = createStringArray(0,38,1);
            return true;
        }
        else
            return false;

    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
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
        camParametersHandler.SetParametersToCamera(parameters);

    }

}