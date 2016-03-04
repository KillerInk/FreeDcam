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
    public ExposureManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler,float step) {
        super(parameters, value, maxValue, MinValue, camParametersHandler,step);
    }

    @Override
    protected String[] createStringArray(int min,int max, float step)
    {
        ArrayList<String> ar = new ArrayList<>();
        for (int i = min; i <= max; i++)
        {
            String s = String.format("%.1f",i*step );
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
        parameters.put(value, t + "");
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        ThrowCurrentValueChanged(t);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);

    }



}
