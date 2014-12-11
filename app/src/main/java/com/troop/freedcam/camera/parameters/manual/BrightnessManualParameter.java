package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class BrightnessManualParameter extends BaseManualParameter
{
    final  String TAG = "freedcam.ManualBrightnessParameter";

    public BrightnessManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.value = "brightness";
        if (!hasSupport())
        {
            try
            {
                int i = parameters.getInt("luma-adaptation");
                isSupported = true;
                this.value = "luma-adaptation";
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
        int min = 0;
        try {
            min = super.GetMinValue();
        }
        catch (Exception ex)
        {

        }
        if (min < 0)
            return min;
        else return 0;
    }
}
