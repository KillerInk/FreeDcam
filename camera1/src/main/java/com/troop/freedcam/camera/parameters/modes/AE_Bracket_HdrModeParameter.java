package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 23.10.2014.
 */
public class AE_Bracket_HdrModeParameter extends BaseModeParameter
{
    public AE_Bracket_HdrModeParameter(HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value, String values) {
        super(parameters, cameraHolder, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (valueToSet.equals("false"))
            super.SetValue("Off", setToCam);
        else
            super.SetValue("AE-Bracket", setToCam);
    }

    @Override
    public String GetValue()
    {
        if (super.GetValue().equals("Off"))
            return "false";
        else if (super.GetValue().equals("AE-Bracket"))
            return "true";
        return super.GetValue();
    }
}
