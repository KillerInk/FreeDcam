package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    AbstractCameraHolder baseCameraHolder;

    public PreviewSizeParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewSizeParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values, AbstractCameraHolder cameraHolder)
    {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //if (baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StopPreview();

        parameters.put(value, valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        //baseCameraHolder.SetCameraParameters(parameters);
        //if (!baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StartPreview();
    }
}
