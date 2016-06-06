package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Size;

import com.freedcam.apis.camera2.camera.CameraHolder;

/**
 * Created by troop on 26.11.2015.
 */
public class VideoSizeModeApi2 extends BaseModeApi2 {
    public VideoSizeModeApi2(CameraHolder cameraHolder) {
        super(cameraHolder);
    }

    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        BackgroundValueHasChanged(valueToSet);
        cameraHolder.VideoSize = valueToSet;
        if (setToCamera)
        {
            cameraHolder.StopPreview();
            cameraHolder.StartPreview();
        }
    }

    @Override
    public String GetValue()
    {

        return cameraHolder.VideoSize;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String[] GetValues()
    {
        Size[] sizes = cameraHolder.map.getOutputSizes(MediaRecorder.class);
        String[] ret = new String[sizes.length];
        for(int i = 0; i < sizes.length; i++)
        {
            ret[i] = sizes[i].getWidth()+ "x" + sizes[i].getHeight();
        }

        return ret;
    }
}
