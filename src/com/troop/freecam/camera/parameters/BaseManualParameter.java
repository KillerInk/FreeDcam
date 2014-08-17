package com.troop.freecam.camera.parameters;

import android.hardware.Camera;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseManualParameter
{
    Camera.Parameters parameters;
    protected String value;
    protected String max_value;
    protected String  min_value;

    boolean isSupported = false;

    public BaseManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue)
    {
        this.parameters = parameters;
        this.value = value;
        this.max_value = maxValue;
        this.min_value = min_value;
    }

    public boolean IsSupported()
    {
        return isSupported;
    }

    public int GetMaxValue()
    {
        return parameters.getInt(max_value);
    }

    public  int GetMinValue()
    {
        return  parameters.getInt(min_value);
    }

    public int GetValue()
    {
        return parameters.getInt(value);
    }
}
