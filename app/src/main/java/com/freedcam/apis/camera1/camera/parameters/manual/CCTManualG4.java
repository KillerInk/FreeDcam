package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualG4 extends BaseCCTManual {
    public CCTManualG4(Camera.Parameters parameters, String value, int max, int min, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, camParametersHandler, step, wbmode);
    }

    public CCTManualG4(Camera.Parameters parameters, String maxValue, String MinValue, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, CCTManualClassHandler.LG_WB, CCTManualClassHandler.LG_Max, CCTManualClassHandler.LG_Min, camParametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.set(value, "0");
    }
}
