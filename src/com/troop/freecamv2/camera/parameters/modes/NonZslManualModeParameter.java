package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;
import com.troop.freecamv2.utils.DeviceUtils;

/**
 * Created by troop on 05.10.2014.
 */
public class NonZslManualModeParameter extends BaseModeParameter{
    public NonZslManualModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
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
}
