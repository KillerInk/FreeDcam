package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 28.03.2016.
 */
public class FocusManualMTK extends BaseFocusManual {
    public FocusManualMTK(HashMap<String, String> parameters, String value, String maxValue, String MinValue, String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, manualFocusModeString, camParametersHandler, step, manualFocusType);
        isSupported = true;
        isVisible = isSupported;
    }

    public FocusManualMTK(HashMap<String, String> parameters, String value, int min, int max, String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, value, min, max, manualFocusModeString, camParametersHandler, step, manualFocusType);
        this.isSupported = true;
        this.isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        this.stringvalues = createStringArray(min,max,step);
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
