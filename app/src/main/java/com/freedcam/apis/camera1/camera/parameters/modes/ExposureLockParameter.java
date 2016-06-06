package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter
{
    final String TAG = ExposureLockParameter.class.getSimpleName();
    public ExposureLockParameter(Camera.Parameters parameters, CameraHolder parameterChanged, String values) {
        super(parameters, parameterChanged, "", "");
    }

    @Override
    public boolean IsSupported() {
        return parameters.isAutoExposureLockSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (parameters.isAutoExposureLockSupported())
            parameters.setAutoExposureLock(Boolean.parseBoolean(valueToSet));
        try {
            cameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue()
    {

        return parameters.getAutoExposureLock()+"";
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true", "false"};
    }

    @Override
    public void BackgroundValueHasChanged(String value) {
            super.BackgroundValueHasChanged(value);
    }
}
