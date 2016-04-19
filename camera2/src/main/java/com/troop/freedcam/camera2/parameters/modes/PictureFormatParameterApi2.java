package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

import java.util.ArrayList;

/**
 * Created by troop on 12.12.2014.
 */
public class PictureFormatParameterApi2 extends BaseModeApi2
{
    BaseCameraHolderApi2 cameraHolder;
    boolean firststart = true;
    private String format = BaseCameraHolderApi2.JPEG;
    public PictureFormatParameterApi2(Handler handler,BaseCameraHolderApi2 baseCameraHolderApi2)
    {
        super(handler,baseCameraHolderApi2);
        this.cameraHolder = baseCameraHolderApi2;
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
        ArrayList<String> ret = new ArrayList<String>();
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW10))
            ret.add(BaseCameraHolderApi2.RAW10);
        if (cameraHolder.map.isOutputSupportedFor(ImageFormat.RAW_SENSOR))
            ret.add(BaseCameraHolderApi2.RAW_SENSOR);
        if(cameraHolder.map.isOutputSupportedFor(ImageFormat.JPEG))
            ret.add(BaseCameraHolderApi2.JPEG);
        return ret.toArray(new String[ret.size()]);
    }
}
