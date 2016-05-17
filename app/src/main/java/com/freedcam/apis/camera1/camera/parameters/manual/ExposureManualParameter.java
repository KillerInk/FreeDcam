package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureManualParameter extends BaseManualParameter
{
    private final String TAG = ExposureManualParameter.class.getSimpleName();
    public ExposureManualParameter(Camera.Parameters parameters, String value, String MinValue, CamParametersHandler camParametersHandler, float step) {
        super(parameters, value, "max-exposure-compensation", "min-exposure-compensation", camParametersHandler,step);
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
        parameters.set(value, t + "");
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
