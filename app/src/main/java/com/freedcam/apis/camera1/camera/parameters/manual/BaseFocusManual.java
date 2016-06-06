package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    final static String TAG = BaseFocusManual.class.getSimpleName();
    protected String manualFocusModeString;
    private int manualFocusType = 0;

    /**
     * checks if the key_value maxvalue and minvalues are contained in the cameraparameters
     * and creates depending on it the stringarray
     * NOTE:if super fails the parameter is unsupported
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param parametersHandler
     * @param step
     */
    public BaseFocusManual(Camera.Parameters parameters, String value, String maxValue, String MinValue, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, parametersHandler, step);
        this.manualFocusModeString = manualFocusModeString;
        this.manualFocusType = manualFocusType;
    }

    /**
     * this allows to hardcode devices wich support manual focus but the parameters are messed up.
     * @param parameters
     * @param value
     * @param min
     * @param max
     * @param manualFocusModeString
     * @param parametersHandler
     * @param step
     * @param manualFocusType
     */
    public BaseFocusManual(Camera.Parameters parameters, String value, int min, int max, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, value, "", "", parametersHandler, step);
        this.isSupported = true;
        this.isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        this.stringvalues = createStringArray(min,max,step);
        this.manualFocusType = manualFocusType;
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
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
            Logger.d(TAG, "Set Focus to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !parametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                parametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.set(KEYS.KEY_MANUAL_FOCUS_TYPE, manualFocusType+"");

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            parametersHandler.SetParametersToCamera(parameters);
        }
    }

}
