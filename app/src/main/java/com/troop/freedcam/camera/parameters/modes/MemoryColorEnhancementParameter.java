package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

/**
 * Created by troop on 05.09.2014.
 */
public class MemoryColorEnhancementParameter extends  BaseModeParameter
{

    I_CameraHolder baseCameraHolder;
    public MemoryColorEnhancementParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public MemoryColorEnhancementParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, I_CameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        BaseCameraHolder cameraHolder = (BaseCameraHolder)baseCameraHolder;

        if (cameraHolder.IsPreviewRunning())
            baseCameraHolder.StopPreview();
        super.SetValue(valueToSet, setToCam);
        if (!cameraHolder.IsPreviewRunning())
            baseCameraHolder.StartPreview();
    }
}
