package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.utils.StringUtils;

import java.util.List;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeParameter extends BaseModeParameter
{
    String[] sizeString;

    public VideoSizeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
        List<Camera.Size> sizes = parameters.getSupportedVideoSizes();
        if (sizes == null || sizes.size() == 0)
            sizes = parameters.getSupportedPreviewSizes();
        isSupported = true;
        sizeString = StringUtils.getStringArrayFromCameraSizes(sizes);
        sizes =null;
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
        return sizeString;
    }
}
