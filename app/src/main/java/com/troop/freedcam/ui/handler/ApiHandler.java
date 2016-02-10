package com.troop.freedcam.ui.handler;
import android.os.Build;

import com.troop.apis.SonyCameraFragment;
import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.apis.Camera1Fragment;
import com.troop.freedcam.apis.Camera2Fragment;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.ui.AppSettingsManager;


/**
 * Created by troop on 11.12.2014.
 */

public class ApiHandler
{
    private static String TAG = ApiHandler.class.getSimpleName();
    private AppSettingsManager appSettingsManager;

    private ApiEvent event;

    public ApiHandler(final AppSettingsManager appSettingsManager, ApiEvent event) {
        this.appSettingsManager = appSettingsManager;
        this.event = event;
    }

    public void CheckApi()
    {
        if (appSettingsManager.IsCamera2FullSupported().equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                boolean legacy = BaseCameraHolderApi2.IsLegacy(appSettingsManager);
                if (legacy) {
                    appSettingsManager.SetCamera2FullSupported("false");
                    appSettingsManager.setCamApi(AppSettingsManager.API_1);
                }
                else {
                    appSettingsManager.SetCamera2FullSupported("true");
                    appSettingsManager.setCamApi(AppSettingsManager.API_2);
                }
                event.apiDetectionDone();
            }
            else {
                appSettingsManager.SetCamera2FullSupported("false");
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
    }


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

    public interface ApiEvent
    {
        void apiDetectionDone();
    }


}
