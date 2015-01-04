package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.HashMap;

/**
 * Created by troop on 21.08.2014.
 */
public class PreviewFpsParameter extends  BaseModeParameter
{
    BaseCameraHolder cameraHolder;
    public PreviewFpsParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public PreviewFpsParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder holder) {
        super(parameters, parameterChanged, value, values);
        this.cameraHolder = holder;
    }



    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (cameraHolder.IsPreviewRunning())
            cameraHolder.StopPreview();
        parameters.put(value, valueToSet);
        if (throwParameterChanged != null && setToCam)
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
}
