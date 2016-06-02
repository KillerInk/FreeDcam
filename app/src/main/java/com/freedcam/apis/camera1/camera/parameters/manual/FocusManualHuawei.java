package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.KEYS;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class FocusManualHuawei extends BaseFocusManual {
    public FocusManualHuawei(Camera.Parameters parameters, String maxValue, String MinValue, String manualFocusModeString, CamParametersHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, "hw-manual-focus-step-value", "hw-vcm-end-value", "hw-vcm-start-value", KEYS.KEY_FOCUS_MODE_MANUAL, camParametersHandler, (float) 10, 0);
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
            camParametersHandler.FocusMode.SetValue("auto", true);
            parameters.set("hw-hwcamera-flag","on");
            parameters.set("hw-manual-focus-mode","off");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.set("hw-hwcamera-flag","on");
            parameters.set("hw-manual-focus-mode","on");
            parameters.set(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set " + value + " to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}