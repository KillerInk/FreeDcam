package com.troop.freedcam.camera2.parameters.modes;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Size;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;

/**
 * Created by troop on 13.12.2014.
 */
public class PictureSizeModeApi2 extends BaseModeApi2 {
    public PictureSizeModeApi2(BaseCameraHolderApi2 baseCameraHolderApi2) {
        super(baseCameraHolderApi2);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        //super.SetValue(valueToSet, setToCamera);
    }

    @Override
    public String GetValue()
    {

        return GetValues()[0];
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
