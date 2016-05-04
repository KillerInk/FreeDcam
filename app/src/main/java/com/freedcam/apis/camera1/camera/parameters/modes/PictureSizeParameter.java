package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    final String TAG = PictureSizeParameter.class.getSimpleName();
    public PictureSizeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String values) {
        super(handler, parameters, parameterChanged, "picture-size", "picture-size-values");
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
            Logger.exception(ex);
        }
    }
}
