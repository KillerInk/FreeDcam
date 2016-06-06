package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;

/**
 * Created by Ingo on 06.03.2016.
 */
public class CCTManualHtc extends BaseCCTManual {
    public CCTManualHtc(Camera.Parameters parameters, String value, int max, int min, ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, value, max, min, parametersHandler, step, wbmode);
    }

    public CCTManualHtc(Camera.Parameters parameters, ParametersHandler parametersHandler) {
        super(parameters, KEYS.WB_CT, KEYS.MAX_WB_CT, KEYS.MIN_WB_CT, parametersHandler, (float) 100, "");
    }

    @Override
    protected void set_to_auto() {
        parameters.set(key_value, "-1");
    }
}
