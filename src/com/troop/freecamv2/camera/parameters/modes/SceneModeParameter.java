package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 01.09.2014.
 */
public class SceneModeParameter extends BaseModeParameter
{

    public SceneModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        parameters.setSceneMode(valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        firststart = false;
    }

    @Override
    public String GetValue() {
        return parameters.getSceneMode();
    }

    @Override
    public String[] GetValues() {
        return parameters.getSupportedSceneModes().toArray(new String[parameters.getSupportedSceneModes().size()]);
    }
}
