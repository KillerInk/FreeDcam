package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.KEYS;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class FocusManualHuawei extends BaseFocusManual {
    public FocusManualHuawei(Camera.Parameters parameters, String maxValue, String MinValue, String manualFocusModeString, CamParametersHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, KEYS.HW_MANUAL_FOCUS_STEP_VALUE, KEYS.HW_VCM_END_VALUE, KEYS.HW_VCM_START_VALUE, KEYS.KEY_FOCUS_MODE_MANUAL, camParametersHandler, (float) 10, 0);
    }

    public FocusManualHuawei(Camera.Parameters parameters, String value, int min, int max, String manualFocusModeString, CamParametersHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, value, min, max, manualFocusModeString, camParametersHandler, step, manualFocusType);
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
            parameters.set(KEYS.HW_HWCAMERA_FLAG,KEYS.ON);
            parameters.set(KEYS.HW_MANUAL_FOCUS_MODE,KEYS.OFF);
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.set(KEYS.HW_HWCAMERA_FLAG,KEYS.ON);
            parameters.set(KEYS.HW_MANUAL_FOCUS_MODE,KEYS.ON);
            parameters.set(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set " + value + " to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}