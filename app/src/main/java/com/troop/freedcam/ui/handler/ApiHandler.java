package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.I_error;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;
import com.troop.freedcam.ui.TextureView.PreviewHandler;

/**
 * Created by troop on 11.12.2014.
 */
public class ApiHandler
{
    public AbstractCameraUiWrapper getCameraUiWrapper(Context context, PreviewHandler preview, AppSettingsManager appSettingsManager, I_error errorHandler)
    {
        AbstractCameraUiWrapper ret;
        if (appSettingsManager.getString(AppSettingsManager.SETTING_SONYAPI).equals("true"))
        {
            ret = new CameraUiWrapperSony(preview.surfaceView, appSettingsManager, errorHandler);
        }
        else if (Build.VERSION.SDK_INT < 21)
        {
            ret = new CameraUiWrapper(preview.surfaceView, appSettingsManager, errorHandler);
        }
        else
            ret = new CameraUiWrapperApi2(context, preview.textureView, appSettingsManager, errorHandler);
        return ret;

    }
}
