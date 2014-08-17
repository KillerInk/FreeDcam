package com.troop.freecam.camera.parameters;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by troop on 17.08.2014.
 */
public class BrightnessManualParameter extends BaseManualParameter
{
    final  String TAG = "freecam.ManualBrightnessParameter";

    public BrightnessManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue)
    {
        super(parameters, value, maxValue, MinValue);
        try
        {
            int i = parameters.getInt("brightness");
            isSupported = true;
            value = "brightness";
        }
        catch (Exception ex)
        {
            isSupported = false;
        }
        if (!isSupported)
        {
            try
            {
                int i = parameters.getInt("luma-adaptation");
                isSupported = true;
                value = "luma-adaptation";
            }
            catch (Exception ex)
            {
                isSupported = false;
            }
        }
        if (isSupported)
        {
            max_value = "max-brightness";
            min_value = "min-brightness";

        }
        Log.d(TAG, "support brightness:" + isSupported);

    }

    @Override
    public int GetMaxValue()
    {
        int max = 100;
        try {
            max = super.GetMaxValue();
        }
        catch (Exception ex)
        {

        }
        if (max > 0)
            return max;
        else return 100;
    }

    @Override
    public int GetMinValue() {
        int min = 100;
        try {
            min = super.GetMinValue();
        }
        catch (Exception ex)
        {

        }
        if (min > 0)
            return min;
        else return 0;
    }
}
