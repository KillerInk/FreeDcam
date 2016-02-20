package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class BrightnessManualParameter extends BaseManualParameter
{
    private static  String TAG = "freedcam.ManualBrightnessParameter";


    public BrightnessManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler)
    {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        if (DeviceUtils.isSonyM5_MTK())
        {
            //temp disable
            this.isSupported = false;
        }
        if (parameters.containsKey("brightness-values") && parameters.get("brightness-values").contains("middle"))
            this.value = "brightness_value";
        else
            this.value = "brightness";
        if (!hasSupport())
        {
            try
            {
                this.value = "luma-adaptation";
                hasSupport();
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
            stringvalues = createStringArray(Integer.parseInt(parameters.get(min_value)), Integer.parseInt(parameters.get(max_value)), 1);
            currentString = parameters.get(this.value);
            for (int i = 0; i < stringvalues.length; i++) {
                if (stringvalues[i].equals(currentString)) {
                    currentInt = i;
                    Set_Default_Value(i);
                }
            }
        }
        Log.d(TAG, "support brightness:" + isSupported);
    }

    @Override
    public String GetStringValue() {
        return null;
    }
}
