package com.troop.freedcam.ui.handler;

import android.content.Context;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.PreviewHandler;

/**
 * Created by troop on 11.12.2014.
 */
public class ApiHandler
{
    public AbstractCameraUiWrapper getCameraUiWrapper(Context context, PreviewHandler preview, AppSettingsManager appSettingsManager, I_error errorHandler, AbstractCameraUiWrapper ret)
    {
        if (ret != null)
        {
            ret.StopPreviewAndCamera();
            ret = null;
        }
        if (appSettingsManager.getSonyCam())
        {
            appSettingsManager.setSonyCam(true);
            ret = new CameraUiWrapperSony(preview.surfaceView, appSettingsManager, errorHandler);

        }
        else /*if (Build.VERSION.SDK_INT < 21)*/
        {
            appSettingsManager.setSonyCam(false);
            ret = new CameraUiWrapper(preview.surfaceView, appSettingsManager, errorHandler);
        }
        /*else
        {
            appSettingsManager.setSonyCam(false);
            ret = new CameraUiWrapperApi2(context, preview.textureView, appSettingsManager, errorHandler);
        }*/
        return ret;

    }
}
