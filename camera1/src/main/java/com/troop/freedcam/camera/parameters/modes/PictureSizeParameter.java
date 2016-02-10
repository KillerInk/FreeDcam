package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    public PictureSizeParameter(Handler handler,HashMap<String, String> parameters,BaseCameraHolder parameterChanged, String value, String values) {
        super(handler, parameters, parameterChanged, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.put("picture-size" , valueToSet);

        try {
            baseCameraHolder.SetCameraParameters(parameters);
            BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
