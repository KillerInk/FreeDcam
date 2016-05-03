package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualHtc extends BaseCCTManual {
    public CCTManualHtc(HashMap<String, String> parameters, String value, int max, int min, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, camParametersHandler, step, wbmode);
    }

    public CCTManualHtc(HashMap<String, String> parameters, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, CCTManualClassHandler.WB_CT, CCTManualClassHandler.MAX_WB_CT, CCTManualClassHandler.MIN_WB_CT, camParametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.put(value, "-1");
    }
}
