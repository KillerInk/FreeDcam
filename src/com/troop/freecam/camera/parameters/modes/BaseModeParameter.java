package com.troop.freecam.camera.parameters.modes;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseModeParameter implements I_ModeParameter
{
    String value;
    String values;
    boolean isSupported = false;
    Camera.Parameters parameters;

    public BaseModeParameter(Camera.Parameters parameters, String value, String values)
    {
        this.parameters = parameters;
        this.value = value;
        this.values = values;
    }

    public boolean IsSupported()
    {
        return isSupported;
    }

    public void SetValue(String valueToSet)
    {
        parameters.set(value, valueToSet);
    }

    public String GetValue()
    {
        return parameters.get(value);
    }

    public String[] GetValues()
    {
        return parameters.get(values).split(",");
    }
}
