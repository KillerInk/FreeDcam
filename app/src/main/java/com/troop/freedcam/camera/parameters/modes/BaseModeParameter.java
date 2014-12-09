package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;

/**
 * Created by troop on 17.08.2014.
 */
public abstract class BaseModeParameter implements I_ModeParameter
{
    protected String value;
    protected String values;
    boolean isSupported = false;
    Camera.Parameters parameters;
    I_ParameterChanged throwParameterChanged;
    protected boolean firststart = true;

    public BaseModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        this.parameters = parameters;
        this.value = value;
        this.values = values;
        this.throwParameterChanged = parameterChanged;
    }

    @Override
    public boolean IsSupported()
    {
        try
        {
            String tmp = parameters.get(values);
            if (!tmp.isEmpty())
                isSupported = true;
        }
        catch (Exception ex)
        {
            isSupported = false;
        }
        return isSupported;
    }

    public void SetValue(String valueToSet,  boolean setToCam)
    {
        parameters.set(value, valueToSet);
        Log.d("freedcam.BaseModeParameter", "set "+value+" to "+ valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        firststart = false;
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
