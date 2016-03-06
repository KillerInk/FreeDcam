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

    public CCTManualHtc(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step, wbmode);
    }

    @Override
    protected void set_to_auto() {
        parameters.put(value, "-1");
    }
}
