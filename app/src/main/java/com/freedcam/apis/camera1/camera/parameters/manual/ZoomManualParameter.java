package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

import java.util.HashMap;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Camera.Parameters parameters, String maxValue, String MinValue, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.value = "zoom";
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
    protected void setvalue(int valueToset) {
        parameters.set(value, valueToset + "");
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
