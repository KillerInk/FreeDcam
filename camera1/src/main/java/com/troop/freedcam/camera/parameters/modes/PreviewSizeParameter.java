package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    AbstractCameraHolder baseCameraHolder;
    final String TAG = PreviewSizeParameter.class.getSimpleName();

    public PreviewSizeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, AbstractCameraHolder cameraHolder)
    {
        super(handler, parameters, parameterChanged, value, values);
        this.baseCameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
            baseCameraHolder.StopPreview();
        parameters.put(value, valueToSet);
        BackgroundValueHasChanged(valueToSet);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        if (setToCam)
            baseCameraHolder.StartPreview();
    }

    @Override
    public String[] GetValues() {
        return parameters.get(values).split(",");
    }
}
