package com.troop.freedcamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcamv2.camera.BaseCameraHolder;
import com.troop.freedcamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 09.09.2014.
 */
public class SkinToneParameter extends BaseModeParameter {
    BaseCameraHolder baseCameraHolder;
    public SkinToneParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public SkinToneParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
        {
            baseCameraHolder.StopPreview();
            super.SetValue(valueToSet, setToCam);
            baseCameraHolder.StartPreview();
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
