package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;

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
