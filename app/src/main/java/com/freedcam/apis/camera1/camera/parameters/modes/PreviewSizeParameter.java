package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.utils.Logger;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    private CameraHolderApi1 baseCameraHolder;
    final String TAG = PreviewSizeParameter.class.getSimpleName();

    public PreviewSizeParameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged)
    {
        super(handler, parameters, parameterChanged, "preview-size", "preview-size-values");
        this.baseCameraHolder = parameterChanged;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
            baseCameraHolder.StopPreview();
        parameters.set(value, valueToSet);
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
