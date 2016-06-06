package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Camera.Parameters parameters, ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler,1);
        this.key_value = "zoom";
        isSupported = false;
        if (parameters.get("zoom-supported")!= null)
            if (parameters.get("zoom-supported").equals("true")) {
                isSupported = true;
                Set_Default_Value(GetValue());
                stringvalues = createStringArray(0,Integer.parseInt(parameters.get("max-zoom")),1);
                currentInt = Integer.parseInt(parameters.get("zoom"));
            }
    }

    @Override
    public void SetValue(int valueToset) {
        parameters.set(key_value, valueToset + "");
        parametersHandler.SetParametersToCamera(parameters);
    }
}
