package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.KEYS;
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 06.03.2016.
 */
public class BaseWB_CCT_QC extends BaseCCTManual {
    public BaseWB_CCT_QC(Camera.Parameters parameters, int max, int min, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, KEYS.WB_MANUAL, 8000, 2000, camParametersHandler, (float) 100, KEYS.WB_MODE_MANUAL_CCT);
    }

    public BaseWB_CCT_QC(Camera.Parameters parameters, String value, String maxValue, String MinValue, CamParametersHandler camParametersHandler, float step, String wbmode) {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step, wbmode);
    }

    @Override
    protected void set_manual() {
        super.set_manual();
        try {
            parameters.set("manual-wb-type", "color-temperature");
            parameters.set("manual-wb-value", stringvalues[currentInt]);
        } catch (Exception ex) {
            Logger.exception(ex);}
    }
}
