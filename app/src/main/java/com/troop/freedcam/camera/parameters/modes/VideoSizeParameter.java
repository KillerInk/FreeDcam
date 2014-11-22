package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoSizeParameter extends BaseModeParameter
{
    String[] sizeString;
    public final String UHDSIZE = "3840x2160";

    public VideoSizeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
        List<Camera.Size> sizes = parameters.getSupportedVideoSizes();
        if (sizes == null || sizes.size() == 0)
            sizes = parameters.getSupportedPreviewSizes();
        isSupported = true;
        List<String> stringList = new ArrayList<String>();

        for (int i = 0; i < sizes.size(); i++)
            stringList.add(sizes.get(i).width + "x" + sizes.get(i).height);
        if (DeviceUtils.isLGADV())
        {
            stringList.add(UHDSIZE);
            Collections.sort(stringList);
        }
        sizeString = stringList.toArray(new String[stringList.size()]);
        sizes =null;
        stringList = null;
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
}
