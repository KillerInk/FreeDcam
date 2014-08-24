package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 24.08.2014.
 */
public class JpegQualityParameter extends BaseModeParameter {
    public JpegQualityParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet) {
        super.SetValue(valueToSet);
    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues()
    {
        String[] valuetoreturn = new String[20];
        int count =0;
        for (int i = 0; i < 100; i =  i + 5)
        {
            valuetoreturn[count] = "" + i;
        }
        return valuetoreturn;
    }
}
