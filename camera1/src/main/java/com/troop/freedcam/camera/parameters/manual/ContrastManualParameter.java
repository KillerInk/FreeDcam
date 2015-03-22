package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ContrastManualParameter extends BaseManualParameter
{
    private static String TAG = ContrastManualParameter.class.getSimpleName();
    public ContrastManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "contrast";
        if (hasSupport()) {
            int max = 100;
            try {
                max = Integer.parseInt(parameters.get("max-contrast"));
                max_value = "max-contrast";
            } catch (Exception ex) {
            }
            try {
                max = Integer.parseInt(parameters.get("contrast-max"));
                max_value = "contrast-max";
            } catch (Exception ex) {
            }

            int min = 0;
            try {
                min = Integer.parseInt(parameters.get("min-contrast"));
                min_value = "min-contrast";
            } catch (Exception ex) {
            }
            try {

                min = Integer.parseInt(parameters.get("contrast-min"));
                min_value = "contrast-min";
            } catch (Exception ex) {
            }
        }
    }

    protected boolean hasSupport()
    {
        try
        {
            if (parameters.containsKey(value))
            {
                int t = Integer.parseInt(parameters.get(value));
                this.isSupported = true;
            }
            else
                this.isSupported = false;
        }
        catch (Exception ex)
        {
            isSupported = false;
        }
        Log.d(TAG, "issupported " + value + ": " + isSupported);
        return isSupported;
    }
}
