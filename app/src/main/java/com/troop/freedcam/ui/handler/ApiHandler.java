package com.troop.freedcam.ui.handler;

import android.content.Context;
import android.util.Log;

import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera2.CameraUiWrapperApi2;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.sonyapi.CameraUiWrapperSony;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 11.12.2014.
 */
public class ApiHandler
{
    private static String TAG = ApiHandler.class.getSimpleName();
    public AbstractCameraUiWrapper getCameraUiWrapper(Context context, PreviewHandler preview, AppSettingsManager appSettingsManager, I_error errorHandler, AbstractCameraUiWrapper ret)
    {


        if (ret != null)
        {
            Log.d(TAG, "Stop preview and cam");
            ret.StopPreview();
            ret.StopCamera();
            ret = null;
        }
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new CameraUiWrapperSony(preview.surfaceView, appSettingsManager);

        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_2))
        {
            ret = new CameraUiWrapperApi2(context, preview.textureView, appSettingsManager);
        }
        else
        {
            ret = new CameraUiWrapper(preview.surfaceView, appSettingsManager, errorHandler);
        }
        return ret;

    }
}
