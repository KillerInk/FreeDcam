package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;
import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    public PictureSizeParameter(Camera.Parameters parameters,I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {

        String[] widthHeight = valueToSet.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
    }

    @Override
    public String GetValue()
    {

        String picsize = parameters.getPictureSize().width + "x" + parameters.getPictureSize().height;
        return picsize;
    }

    @Override
    public String[] GetValues()
    {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        List<String> stringList = new ArrayList<String>();

        for (int i = 0; i < sizes.size(); i++)
            stringList.add(sizes.get(i).width + "x" + sizes.get(i).height);

        return stringList.toArray(new String[sizes.size()]);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}
