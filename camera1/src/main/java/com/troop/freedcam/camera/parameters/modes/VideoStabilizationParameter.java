package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;
/**
 * Created by Ar4eR on 10.12.15.
 */
public class VideoStabilizationParameter extends  BaseModeParameter {
    I_CameraHolder baseCameraHolder;
    private final String[] vs_values = {"true", "false"};
    public VideoStabilizationParameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value)
    {

        super(handler, parameters, parameterChanged, "video-stabilization", "");


        if (parameters.get("video-stabilization-supported").equals("true"))
        {
            this.isSupported = true;
            this.value = "video-stabilization";
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
        final String vs = parameters.get("video-stabilization");
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