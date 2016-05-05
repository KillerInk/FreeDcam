package com.freedcam.apis.camera1.camera.parameters.manual;


import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.value = "zoom";
        isSupported = false;
        if (parameters.containsKey("zoom-supported"))
            if (parameters.get("zoom-supported").equals("true")) {
                isSupported = true;
                Set_Default_Value(GetValue());
                stringvalues = createStringArray(0,Integer.parseInt(parameters.get("max-zoom")),1);
                currentInt = Integer.parseInt(parameters.get("zoom"));
            }
    }

    @Override
    protected void setvalue(int valueToset) {
        parameters.put(value, valueToset + "");
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
