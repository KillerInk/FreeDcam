package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.utils.DeviceUtils;


import java.util.HashMap;

/**
 * Created by troop on 05.10.2014.
 */
public class NonZslManualModeParameter extends BaseModeParameter
{
    private CameraHolderApi1 cameraHolderApi1;

    public NonZslManualModeParameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged, String values, I_CameraHolder baseCameraHolder) {
        super(handler,parameters, parameterChanged, "non-zsl-manual-mode", "");
        this.cameraHolderApi1 = (CameraHolderApi1) baseCameraHolder;
    }

    @Override
    public boolean IsSupported() {
        return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9);
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true","false"};
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam) {
            super.SetValue(valueToSet, setToCam);
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
