package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

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
    /**
     * @param parameters
     * @param parametersHandler
     * @param step
     */
    public FocusManual_QcomM(Camera.Parameters parameters, ParametersHandler parametersHandler, float step)
    {
        super(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION, KEYS.MAX_FOCUS_POS_RATIO, KEYS.MIN_FOCUS_POS_RATIO, parametersHandler, (float) 1);
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.AUTO);
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public void SetValue(final int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parameters.set(KEYS.MANUAL_FOCUS, KEYS.OFF);
            parametersHandler.SetParametersToCamera(parameters);
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
            Logger.d(TAG, "Set Focusmode to : auto");
        }
        else
        {
            if (!parametersHandler.FocusMode.GetValue().equals(KEYS.KEY_FOCUS_MODE_MANUAL)) {//do not set "manual" to "manual"
                parametersHandler.FocusMode.SetValue(KEYS.KEY_FOCUS_MODE_MANUAL, false);
                parameters.set(KEYS.MANUAL_FOCUS, KEYS.MANUAL_FOCUS_SCALE_MODE);
                parametersHandler.SetParametersToCamera(parameters);
            }

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            parametersHandler.SetParametersToCamera(parameters);
        }
    }
}
