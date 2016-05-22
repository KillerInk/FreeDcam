package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;

/**
 * Created by troop on 27.10.2014.
 */
public class LongExposureSetting extends BaseModeParameter
{
    public LongExposureSetting(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String value, String values) {
        super( handler,parameters, parameterChanged, value, values);
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
