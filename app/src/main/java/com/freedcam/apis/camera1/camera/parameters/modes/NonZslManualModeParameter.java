package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 05.10.2014.
 */
public class NonZslManualModeParameter extends BaseModeParameter
{
    private CameraHolder cameraHolder;

    public NonZslManualModeParameter(Camera.Parameters parameters, CameraHolder parameterChanged, String values, I_CameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, "non-zsl-manual-mode", "");
        this.cameraHolder = (CameraHolder) baseCameraHolder;
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
