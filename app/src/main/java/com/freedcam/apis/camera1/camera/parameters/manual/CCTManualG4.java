package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.KEYS;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualG4 extends BaseCCTManual {
    public CCTManualG4(Camera.Parameters parameters, String value, int max, int min, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, camParametersHandler, step, wbmode);
    }

    public CCTManualG4(Camera.Parameters parameters,CamParametersHandler camParametersHandler) {
        super(parameters, KEYS.LG_WB, KEYS.LG_WB_SUPPORTED_MAX, KEYS.LG_WB_SUPPORTED_MIN, camParametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.set(key_value, "0");
    }
}
