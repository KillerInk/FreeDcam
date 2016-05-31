package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 28.03.2016.
 */
public class FocusManualMTK extends BaseFocusManual {
    public FocusManualMTK(Camera.Parameters parameters, String value, String maxValue, String MinValue, CamParametersHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, "focus-fs-fi", "focus-fs-fi-max", "focus-fs-fi-min", FocusManualClassHandler.focusMode_manual, camParametersHandler, (float) 10, 0);
        isSupported = true;
        isVisible = isSupported;
    }

    public FocusManualMTK(Camera.Parameters parameters, CamParametersHandler camParametersHandler) {
        super(parameters, "afeng-pos", 0, 1023, FocusManualClassHandler.focusMode_manual, camParametersHandler, (float) 10, 1);
        this.isSupported = true;
        this.isVisible = true;
        this.manualFocusModeString = FocusManualClassHandler.focusMode_manual;
        this.stringvalues = createStringArray(0, 1023, (float) 10);
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);

            parameters.set(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ value +" to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}
