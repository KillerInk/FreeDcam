package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    public PictureSizeParameter(HashMap<String, String> parameters,BaseCameraHolder parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        String tmp = parameters.get("picture-size");
        parameters.put("picture-size" , valueToSet);

        try {
            baseCameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            try
            {
                parameters.put("picture-size" , tmp);
                baseCameraHolder.SetCameraParameters(parameters);
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();

            }
        }
    }
}
