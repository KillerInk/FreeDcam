package com.troop.freedcam.ui.handler;

import com.troop.apis.SonyCameraFragment;
import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.apis.Camera1Fragment;
import com.troop.freedcam.apis.Camera2Fragment;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 11.12.2014.
 */
public class ApiHandler
{
    private static String TAG = ApiHandler.class.getSimpleName();
    public AbstractCameraFragment getCameraFragment(AppSettingsManager appSettingsManager)
    {
        AbstractCameraFragment ret;
        if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new SonyCameraFragment();

        }
        else if (appSettingsManager.getCamApi().equals(AppSettingsManager.API_2))
        {
            ret = new Camera2Fragment();
        }
        else
        {
            ret = new Camera1Fragment();
        }
        return ret;

    }
}
