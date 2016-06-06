package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    final String TAG = PictureSizeParameter.class.getSimpleName();
    public PictureSizeParameter(Camera.Parameters  parameters, CameraHolderApi1 parameterChanged) {
        super(parameters, parameterChanged, "picture-size", "picture-size-values");
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set("picture-size" , valueToSet);

        try {
            cameraHolderApi1.SetCameraParameters(parameters);
            BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }
}
