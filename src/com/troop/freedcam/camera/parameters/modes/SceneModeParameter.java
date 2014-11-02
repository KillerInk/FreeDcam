package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.List;

/**
 * Created by troop on 01.09.2014.
 */
public class SceneModeParameter extends BaseModeParameter
{

    public SceneModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);

        try
        {
            List<String> scenes =  parameters.getSupportedSceneModes();
            if (scenes.size() > 0)
                this.isSupported = true;
            else this.isSupported = false;
        }
        catch (Exception ex)
        {
            this.isSupported = false;
        }
    }

    @Override
    public boolean IsSupported()
    {

        return isSupported;
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
    public String[] GetValues()
    {
        String scenes[]= parameters.getSupportedSceneModes().toArray(new String[parameters.getSupportedSceneModes().size()]);
        return scenes;
    }
}
