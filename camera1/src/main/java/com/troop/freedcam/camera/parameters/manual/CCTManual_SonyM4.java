package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManual_SonyM4 extends BaseCCTManual {
    public CCTManual_SonyM4(HashMap<String, String> parameters, int max, int min, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, CCTManualClassHandler.WB_MANUAL, 8000, 2000, camParametersHandler, (float) 100, CCTManualClassHandler.WB_MODE_MANUAL_CCT);
    }

    public CCTManual_SonyM4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step, wbmode);
    }

    @Override
    protected void set_manual() {
        super.set_manual();
        try {
            parameters.put("manual-wb-type", "color-temperature");
            parameters.put("manual-wb-value", stringvalues[currentInt]);
        } catch (Exception ex) {

        }
    }
}
