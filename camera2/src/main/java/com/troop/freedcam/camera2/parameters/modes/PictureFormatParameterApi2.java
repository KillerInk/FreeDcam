package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    BaseCameraHolderApi2 cameraHolder;
    boolean firststart = true;
    public PictureFormatParameterApi2(BaseCameraHolderApi2 baseCameraHolderApi2)
    {
        super(baseCameraHolderApi2);
        this.cameraHolder = baseCameraHolderApi2;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.equals("jpeg"))
        {

        }
        else if (valueToSet.equals("raw10"))
        {

        }
        else if (valueToSet.equals("raw_sensor"))
        {

        }
        if (!firststart)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }
        else
            firststart = false;

    }

    @Override
    public String GetValue() {
        return "jpeg";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        ArrayList<String> ret = new ArrayList<String>();
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            ret.add("raw_sensor");
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW10))
            ret.add("raw10");
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.JPEG))
            ret.add("jpeg");
        return ret.toArray(new String[ret.size()]);
    }
}
