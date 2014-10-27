package com.troop.freedcamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcamv2.camera.parameters.I_ParameterChanged;
import com.troop.freedcamv2.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by troop on 24.08.2014.
 */
public class PictureFormatParameter extends BaseModeParameter
{

    public PictureFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public String[] GetValues()
    {
        ArrayList<String> toReturn = new ArrayList<String>();
        final String[] vals = parameters.get(values).split(",");
        for (int i = 0; i < vals.length; i++)
        {
            if (DeviceUtils.isLGADV() && !vals[i].startsWith("bayer-qcom"))
                toReturn.add(vals[i]);
            else if (!DeviceUtils.isLGADV())
                toReturn.add(vals[i]);
        }
        return toReturn.toArray(new String[toReturn.size()]);
    }
}
