package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualG4 extends BaseCCTManual {
    public CCTManualG4(HashMap<String, String> parameters, String value, int max, int min, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, camParametersHandler, step, wbmode);
    }

    public CCTManualG4(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, CCTManualClassHandler.LG_WB, CCTManualClassHandler.LG_Max, CCTManualClassHandler.LG_Min, camParametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.put(value, "0");
    }
}
