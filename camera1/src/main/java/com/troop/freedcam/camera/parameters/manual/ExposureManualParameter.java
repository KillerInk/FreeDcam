package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    public ExposureManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        if(hasSupport()) {
            currentString = parameters.get(value);
            for (int i = 0; i < stringvalues.length; i++) {
                if (stringvalues[i].equals(currentString)) {
                    currentInt = i;
                    Set_Default_Value(i);
                }
            }
        }
    }

    protected String[] createStringArray(int min,int max, int step)
    {
        float stepp = 1;
        if (parameters.containsKey("exposure-compensation-step"))
            stepp = Float.parseFloat(parameters.get("exposure-compensation-step"));
        ArrayList<String> ar = new ArrayList<>();
        if (step == 0)
            step = 1;
        for (int i = min; i <= max; i+=step)
        {
            String s = String.format("%.1f",i*stepp );
            ar.add(s);
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        if(stringvalues == null || stringvalues.length == 0)
            return;
        int t = valueToset-(stringvalues.length/2);
        parameters.put(value, t+"");
        ThrowCurrentValueChanged(t);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }



}
