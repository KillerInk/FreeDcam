package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    private CameraHolderApi2 cameraHolder;
    boolean firststart = true;
    private String format = CameraHolderApi2.JPEG;
    public PictureFormatParameterApi2(Handler handler,CameraHolderApi2 cameraHolderApi2)
    {
        super(handler, cameraHolderApi2);
        this.cameraHolder = cameraHolderApi2;
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
            ret.add(CameraHolderApi2.RAW10);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            ret.add(CameraHolderApi2.RAW_SENSOR);
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.JPEG))
            ret.add(CameraHolderApi2.JPEG);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW12))
            ret.add(CameraHolderApi2.RAW12);
        return ret.toArray(new String[ret.size()]);
    }
}
