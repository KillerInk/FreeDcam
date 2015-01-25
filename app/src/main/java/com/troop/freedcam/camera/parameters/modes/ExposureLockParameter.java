package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.HashMap;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter {
    public ExposureLockParameter(HashMap<String,String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        if (parameters.containsKey("auto-exposure-lock-supported"))
        {
            if (parameters.get("auto-exposure-lock-supported").equals("true"))
            {
                return true;
            }
        }
            return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (parameters.get("auto-exposure-lock-supported").equals("true"))
            parameters.put("auto-exposure-lock", valueToSet);
        if (parameters.get("auto-whitebalance-lock-supported").equals("true"))
            parameters.put("auto-whitebalance-lock", valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
    }

    @Override
    public String GetValue()
    {
        return parameters.get("auto-exposure-lock");
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true", "false"};
    }

    @Override
    public void BackgroundValueHasChanged(String value) {
            super.BackgroundValueHasChanged(value);
    }
}
