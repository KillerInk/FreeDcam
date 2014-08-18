package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.manager.camera_parameters.BaseParametersManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    public PictureSizeParameter(Camera.Parameters parameters, String value, String values) {
        super(parameters, value, values);
    }

    @Override
    public void SetValue(String valueToSet)
    {

        String[] widthHeight = valueToSet.split("x");
        int w = Integer.parseInt(widthHeight[0]);
        int h = Integer.parseInt(widthHeight[1]);
        parameters.setPictureSize(w,h);
    }

    @Override
    public String GetValue()
    {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        String picsize = parameters.getPictureSize().width + "x" + parameters.getPictureSize().height;
        return picsize;
    }

    @Override
    public String[] GetValues()
    {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        List<String> stringList = new ArrayList<String>();

        for (int i = 0; i < sizes.size(); i++)
            stringList.add(sizes.get(i).width + "x" + sizes.get(i).width);

        return stringList.toArray(new String[sizes.size()]);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}
