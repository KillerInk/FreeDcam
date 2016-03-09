package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    final static String TAG = BaseFocusManual.class.getSimpleName();
    private String manualFocusModeString;
    private int manualFocusType = 0;

    /**
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param camParametersHandler
     * @param step
     */
    public BaseFocusManual(HashMap<String, String> parameters, String value, String maxValue, String MinValue,String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, camParametersHandler, step);
        this.manualFocusModeString = manualFocusModeString;
        this.manualFocusType = manualFocusType;
    }

    public BaseFocusManual(HashMap<String, String> parameters, String value, int min,int max,String manualFocusModeString, AbstractParameterHandler camParametersHandler, float step,int manualFocusType) {
        super(parameters, value, "", "", camParametersHandler, step);
        this.isSupported = true;
        this.isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        this.stringvalues = createStringArray(min,max,step);
        this.manualFocusType = manualFocusType;
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
            camParametersHandler.FocusMode.SetValue("auto", true);
            Logger.d(TAG, "Set CCT to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !camParametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                camParametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.put("manual-focus-pos-type", manualFocusType+"");

            parameters.put(value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ value +" to : " + stringvalues[currentInt]);
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }

}
