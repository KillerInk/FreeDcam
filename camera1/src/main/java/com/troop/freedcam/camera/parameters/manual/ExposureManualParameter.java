package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    final String TAG = ExposureManualParameter.class.getSimpleName();
    public ExposureManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler,float step) {
        super(parameters, value, maxValue, MinValue, camParametersHandler,step);
        Logger.d(TAG, "Is Supported:" + isSupported);
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

        if(stringvalues == null || stringvalues.length == 0)
            return;
        if (valueToset < 0 && parameters.get(min_value).contains("-"))
            valueToset = valueToset+stringvalues.length/2;
        currentInt = valueToset;
        int t = valueToset;
        if (parameters.get(min_value).contains("-"))
            t = t-(stringvalues.length/2);
        Logger.d(TAG, "Set "+ value +" to: " +t);
        parameters.put(value, t + "");
        try
        {
            camParametersHandler.SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        ThrowCurrentValueChanged(currentInt);
        ThrowCurrentValueStringCHanged(stringvalues[valueToset]);

    }



}
