package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;
import com.troop.freecamv2.ui.AppSettingsManager;

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

    public BaseModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        this.parameters = parameters;
        this.value = value;
        this.values = values;
        this.throwParameterChanged = parameterChanged;
    }

    public boolean IsSupported()
    {
        return isSupported;
    }

    public void SetValue(String valueToSet)
    {
        parameters.set(value, valueToSet);
        if (throwParameterChanged != null)
            throwParameterChanged.ParameterChanged();
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
