package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

public class FXManualParameter extends BaseManualParameter {

    public FXManualParameter(Camera.Parameters parameters,ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler,1);
    }

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
            Logger.exception(ex);
        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {   
    	parameters.set(KEYS.MORPHO_EFFECT_TYPE, String.valueOf(valueToSet));
        parametersHandler.SetParametersToCamera(parameters);

    }

}