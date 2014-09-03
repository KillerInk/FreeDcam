package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 24.08.2014.
 */
public class PreviewFormatParameter extends BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    public PreviewFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFormatParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder cameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void SetValue(String valueToSet)
    {
        if (cameraHolder.IsPreviewRunning())
            cameraHolder.StopPreview();
        parameters.set(value, valueToSet);
        if (throwParameterChanged != null)
            throwParameterChanged.ParameterChanged();
        if (!cameraHolder.IsPreviewRunning())
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
