package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 27.04.2016.
 * manual-focus-modes=off,scale-mode,diopter-mode
 *
 * cur-focus-scale = 70
 *
 * max-focus-pos-dac=1023
 * max-focus-pos-diopter=10
 * max-focus-pos-index=1023
 * max-focus-pos-ratio=100
 */
public class FocusManual_QcomM extends BaseManualParameter
{
    private final String TAG = FocusManual_QcomM.class.getSimpleName();
    private final String manualFocusModeString = "manual";
    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     * @param step
     */
    public FocusManual_QcomM(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler, float step)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step);
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add("Auto");
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    protected void setvalue(final int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parameters.put("manual-focus", "off");
            camParametersHandler.SetParametersToCamera(parameters);
            camParametersHandler.FocusMode.SetValue("auto", true);
            Logger.d(TAG, "Set CCT to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) {//do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);
                parameters.put("manual-focus", "scale-mode");
                camParametersHandler.SetParametersToCamera(parameters);
            }

            parameters.put(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ value +" to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }
}
