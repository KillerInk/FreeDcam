package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 11.12.2014.
 */
public class ApiHandler
{
    public AbstractCameraUiWrapper getCameraUiWrapper(Context context, ExtendedSurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler)
    {
        AbstractCameraUiWrapper ret;
        if (Build.VERSION.SDK_INT < 21)
        {
            ret = new CameraUiWrapper(preview, appSettingsManager, errorHandler);
        }
        else
            ret = new CameraUiWrapperApi2(context, preview, appSettingsManager, errorHandler);
        return ret;

    }
}
