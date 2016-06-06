package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera2.camera.CameraHolder;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    private CameraHolder cameraHolder;
    boolean firststart = true;
    private String format = KEYS.JPEG;
    public PictureFormatParameterApi2(CameraHolder cameraHolder)
    {
        super(cameraHolder);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        BackgroundValueHasChanged(valueToSet);
        format = valueToSet;
        if (setToCamera)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }


    }

    @Override
    public String GetValue() {
        return format;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        ArrayList<String> ret = new ArrayList<>();
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW10))
            ret.add(CameraHolder.RAW10);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            ret.add(CameraHolder.RAW_SENSOR);
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.JPEG))
            ret.add(KEYS.JPEG);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW12))
            ret.add(CameraHolder.RAW12);
        return ret.toArray(new String[ret.size()]);
    }
}
