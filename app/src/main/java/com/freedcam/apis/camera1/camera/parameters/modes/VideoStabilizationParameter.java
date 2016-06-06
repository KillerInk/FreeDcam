package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.KEYS;

/**
 * Created by Ar4eR on 10.12.15.
 */
public class VideoStabilizationParameter extends  BaseModeParameter {
    I_CameraHolder baseCameraHolder;
    private final String[] vs_values = {KEYS.TRUE, KEYS.FALSE};
    public VideoStabilizationParameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 parameterChanged)
    {
        super(handler, parameters, parameterChanged, KEYS.VIDEO_STABILIZATION, "");
        if (parameters.get(KEYS.VIDEO_STABILIZATION_SUPPORTED).equals(KEYS.TRUE))
        {
            this.isSupported = true;
            this.key_value = KEYS.VIDEO_STABILIZATION;
        }
        else
            this.isSupported = false;

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {
        return vs_values;
    }

    @Override
    public String GetValue()
    {
        final String vs = parameters.get(KEYS.VIDEO_STABILIZATION);
        if (vs != null && !vs.equals(""))
            return vs;
        else
            return "error";
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
    }
}