package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeParameter extends BaseModeParameter
{
    String[] sizeString;
    public final String UHDSIZE = "3840x2160";
    String TAG = VideoSizeParameter.class.getSimpleName();

    public VideoSizeParameter(HashMap<String,String> parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
        String[] sizes = parameters.get("video-size-values").split(",");
        if (sizes == null || sizes.length == 0)
        {
            Log.d(TAG, "Couldnt finde Video Size Values loading Preview Size Values");
            sizes =  parameters.get("preview-size-values").split(",");
        }
        if (sizes == null || sizes.length == 0)
            this.isSupported = true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
    }

    @Override
    public String GetValue() {
        return super.GetValue();
    }

    @Override
    public String[] GetValues() {
        return sizeString;
    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }
}
