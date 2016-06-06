package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    private CameraHolderApi1 baseCameraHolder;
    final String TAG = PreviewSizeParameter.class.getSimpleName();

    public PreviewSizeParameter(Camera.Parameters parameters, CameraHolderApi1 parameterChanged)
    {
        super(parameters, parameterChanged, "preview-size", "preview-size-values");
        this.baseCameraHolder = parameterChanged;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
            baseCameraHolder.StopPreview();
        parameters.set(key_value, valueToSet);
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
        return parameters.get(key_values).split(",");
    }
}
