package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 05.09.2014.
 */
public class MemoryColorEnhancementParameter extends  BaseModeParameter
{

    BaseCameraHolder baseCameraHolder;
    public MemoryColorEnhancementParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public MemoryColorEnhancementParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StopPreview();
        super.SetValue(valueToSet, setToCam);
        if (!baseCameraHolder.IsPreviewRunning())
            baseCameraHolder.StartPreview();
    }
}
