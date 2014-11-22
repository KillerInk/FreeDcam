package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 05.09.2014.
 */
public class LensshadeParameter extends BaseModeParameter
{
    public LensshadeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
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
        return super.IsSupported();
    }
}
