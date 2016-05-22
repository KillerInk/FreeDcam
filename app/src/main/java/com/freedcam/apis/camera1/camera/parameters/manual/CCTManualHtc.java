package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualHtc extends BaseCCTManual {
    public CCTManualHtc(Camera.Parameters parameters, String value, int max, int min, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, camParametersHandler, step, wbmode);
    }

    public CCTManualHtc(Camera.Parameters parameters, String maxValue, String MinValue, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, CCTManualClassHandler.WB_CT, CCTManualClassHandler.MAX_WB_CT, CCTManualClassHandler.MIN_WB_CT, camParametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.set(value, "-1");
    }
}
