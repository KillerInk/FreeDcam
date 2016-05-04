package com.freedcam.apis;

import android.os.Build;

import com.freedcam.apis.apis.AbstractCameraFragment;
import com.freedcam.apis.camera1.apis.Camera1Fragment;
import com.freedcam.apis.camera2.apis.Camera2Fragment;
import com.freedcam.apis.camera2.camera2.BaseCameraHolderApi2;
import com.freedcam.apis.sonyremote.apis.SonyCameraFragment;
import com.freedcam.ui.AppSettingsManager;
import com.freedcam.utils.FreeDPool;


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
