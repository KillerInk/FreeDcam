package com.freedcam.apis.camera1.camera.parameters.modes;
//defcomg was here

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;

/**
 * Created by troop on 24.08.2014.
 */
public class JpegQualityParameter extends BaseModeParameter {
    public JpegQualityParameter(Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String values) {
        super(parameters, parameterChanged, "jpeg-quality", "");
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public String[] GetValues()
    {
        String[] valuetoreturn = new String[20];
        for (int i = 1; i < 21; i++)
        {
            valuetoreturn[i-1] = "" + i*5;
        }
        return valuetoreturn;
    }
}
