package com.troop.freedcamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcamv2.camera.BaseCameraHolder;
import com.troop.freedcamv2.camera.parameters.I_ParameterChanged;
import com.troop.freedcamv2.utils.DeviceUtils;

/**
 * Created by troop on 05.10.2014.
 */
public class NonZslManualModeParameter extends BaseModeParameter
{
    BaseCameraHolder baseCameraHolder;

    public NonZslManualModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public NonZslManualModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, BaseCameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public boolean IsSupported() {
        if (DeviceUtils.isHTCADV())
            return true;
        else
            return false;
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true","false"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam) {
            baseCameraHolder.StopPreview();
            super.SetValue(valueToSet, setToCam);
            baseCameraHolder.StartPreview();
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
