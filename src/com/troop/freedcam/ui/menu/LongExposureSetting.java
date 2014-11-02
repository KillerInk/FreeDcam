package com.troop.freedcam.ui.menu;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.camera.parameters.modes.BaseModeParameter;

/**
 * Created by troop on 27.10.2014.
 */
public class LongExposureSetting extends BaseModeParameter
{
    public LongExposureSetting(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {

    }

    @Override
    public String GetValue() {
        return "";
    }

    @Override
    public String[] GetValues() {
        return new String[]{"1","2","3","4","5","10", "15", "20","30","60","90","180" };
    }
}
