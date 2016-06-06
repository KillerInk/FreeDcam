package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseWB_CCT_QC extends BaseCCTManual {
    public BaseWB_CCT_QC(Camera.Parameters parameters, int max, int min, ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, KEYS.WB_MANUAL_CCT, 8000, 2000, parametersHandler, (float) 100, KEYS.WB_MODE_MANUAL_CCT);
    }

    public BaseWB_CCT_QC(Camera.Parameters parameters, String value, String maxValue, String MinValue, ParametersHandler parametersHandler, float step, String wbmode) {
        super(parameters, value, maxValue, MinValue, parametersHandler, step, wbmode);
    }

    @Override
    protected void set_manual() {
        super.set_manual();
        try {
            parameters.set(KEYS.MANUAL_WB_TYPE, KEYS.MANUAL_WB_TYPE_COLOR_TEMPERATURE);
            parameters.set(KEYS.MANUAL_WB_VALUE, stringvalues[currentInt]);
        } catch (Exception ex) {
            Logger.exception(ex);}
    }
}
