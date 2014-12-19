package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    I_CameraHolder cameraHolder;
    public PreviewFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, I_CameraHolder cameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        BaseCameraHolder baseCameraHolder = (BaseCameraHolder) cameraHolder;
        if (baseCameraHolder.IsPreviewRunning())
            cameraHolder.StopPreview();
        parameters.set(value, valueToSet);
        if (throwParameterChanged != null && setToCam)
            throwParameterChanged.ParameterChanged();
        if (!baseCameraHolder.IsPreviewRunning())
            cameraHolder.StartPreview();


    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return super.GetValues();
    }

    public int GetFormat()
    {
        return parameters.getPreviewFormat();
    }
}
