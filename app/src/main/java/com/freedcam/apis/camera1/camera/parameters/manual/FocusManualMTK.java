package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by troop on 28.03.2016.
 */
public class FocusManualMTK extends BaseFocusManual {
    public FocusManualMTK(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, "focus-fs-fi", "focus-fs-fi-max", "focus-fs-fi-min", FocusManualClassHandler.focusMode_manual, camParametersHandler, (float) 10, 0);
        isSupported = true;
        isVisible = isSupported;
    }

    public FocusManualMTK(HashMap<String, String> parameters, int min, int max, String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, "afeng-pos", 0, 1023, FocusManualClassHandler.focusMode_manual, camParametersHandler, (float) 10, 1);
        this.isSupported = true;
        this.isVisible = true;
        this.manualFocusModeString = FocusManualClassHandler.focusMode_manual;
        this.stringvalues = createStringArray(0, 1023, (float) 10);
        //this.manualFocusType = manualFocusType;
    }

    @Override
    protected void setvalue(final int valueToSet)
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

            parameters.put(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ value +" to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}
