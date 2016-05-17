package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

public class FXManualParameter extends BaseManualParameter {
	
	CameraHolderApi1 cameraHolderApi1;
    public FXManualParameter(Camera.Parameters parameters, String maxValue, String MinValue, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);

        //TODO add missing logic
    }
  /*  public FXManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, CameraHolderApi1 cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.cameraHolderApi1 = cameraHolder;
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
    	parameters.set("morpho_effect_type", String.valueOf(valueToSet));
        camParametersHandler.SetParametersToCamera(parameters);

    }

}