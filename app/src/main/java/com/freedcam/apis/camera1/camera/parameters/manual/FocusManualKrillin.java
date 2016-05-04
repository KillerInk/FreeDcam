package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class FocusManualKrillin extends BaseFocusManual {
    public FocusManualKrillin(HashMap<String, String> parameters, String maxValue, String MinValue, String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, "hw-manual-focus-step-value", "hw-vcm-end-value", "hw-vcm-start-value", FocusManualClassHandler.focusMode_manual, camParametersHandler, (float) 10, 0);
    }

    public FocusManualKrillin(HashMap<String, String> parameters, String value, int min, int max, String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, value, min, max, manualFocusModeString, camParametersHandler, step, manualFocusType);
    }

    @Override
    protected void setvalue(final int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
            parameters.put("hw-hwcamera-flag","on");
            parameters.put("hw-manual-focus-mode","off");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.put("hw-hwcamera-flag","on");
            parameters.put("hw-manual-focus-mode","on");
            parameters.put(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set " + value + " to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}