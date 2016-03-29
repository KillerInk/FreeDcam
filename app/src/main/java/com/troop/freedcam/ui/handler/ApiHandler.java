package com.troop.freedcam.ui.handler;
import android.os.Build;
import android.os.Handler;

import com.troop.apis.SonyCameraFragment;
import com.troop.freedcam.apis.AbstractCameraFragment;
import com.troop.freedcam.apis.Camera1Fragment;
import com.troop.freedcam.apis.Camera2Fragment;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;


/**
 * Created by troop on 11.12.2014.
 */

public class ApiHandler
{
    private static String TAG = ApiHandler.class.getSimpleName();

    ApiEvent event;

    public ApiHandler(ApiEvent event) {
        this.event = event;
    }

    public void CheckApi()
    {
        if (AppSettingsManager.APPSETTINGSMANAGER.IsCamera2FullSupported().equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                FreeDPool.Execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean legacy = BaseCameraHolderApi2.IsLegacy(AppSettingsManager.APPSETTINGSMANAGER);
                        if (legacy) {
                            AppSettingsManager.APPSETTINGSMANAGER.SetCamera2FullSupported("false");
                            AppSettingsManager.APPSETTINGSMANAGER.setCamApi(AppSettingsManager.API_1);
                        } else {
                            AppSettingsManager.APPSETTINGSMANAGER.SetCamera2FullSupported("true");
                            AppSettingsManager.APPSETTINGSMANAGER.setCamApi(AppSettingsManager.API_2);
                        }
                        event.apiDetectionDone();
                    }
                });

            }
            else {
                AppSettingsManager.APPSETTINGSMANAGER.SetCamera2FullSupported("false");
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
    }


    public AbstractCameraFragment getCameraFragment()
    {
        AbstractCameraFragment ret;
        if (AppSettingsManager.APPSETTINGSMANAGER.getCamApi().equals(AppSettingsManager.API_SONY))
        {
            ret = new SonyCameraFragment();

        }
        else if (AppSettingsManager.APPSETTINGSMANAGER.getCamApi().equals(AppSettingsManager.API_2))
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
