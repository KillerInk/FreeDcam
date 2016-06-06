package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;
import android.util.Size;

import com.freedcam.apis.camera2.camera.CameraHolder;

/**
 * Created by troop on 13.12.2014.
 */
public class PictureSizeModeApi2 extends BaseModeApi2
{
    private String size = "1920x1080";
    public PictureSizeModeApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
    }
    boolean firststart = true;
    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        BackgroundValueHasChanged(valueToSet);
        size = valueToSet;
        if (setToCamera)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }
    }

    @Override
    public String GetValue()
    {
        return size;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        Size[] sizes = cameraHolder.map.getOutputSizes(ImageFormat.JPEG);
        String[] ret = new String[sizes.length];
        for(int i = 0; i < sizes.length; i++)
        {
            ret[i] = sizes[i].getWidth()+ "x" + sizes[i].getHeight();
        }

        return ret;
    }
}
