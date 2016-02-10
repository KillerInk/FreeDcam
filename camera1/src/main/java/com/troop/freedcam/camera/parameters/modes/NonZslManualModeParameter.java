package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 05.10.2014.
 */
public class NonZslManualModeParameter extends BaseModeParameter
{
    private BaseCameraHolder baseCameraHolder;

    public NonZslManualModeParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, I_CameraHolder baseCameraHolder) {
        super(handler,parameters, parameterChanged, "non-zsl-manual-mode", "");
        this.baseCameraHolder = (BaseCameraHolder) baseCameraHolder;
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
            //baseCameraHolder.StopPreview();
            super.SetValue(valueToSet, setToCam);
            //baseCameraHolder.StartPreview();
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
