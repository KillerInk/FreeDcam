package com.freedcam.apis;

import android.content.Context;
import android.os.Build;

import com.freedcam.apis.basecamera.apis.AbstractCameraFragment;
import com.freedcam.apis.camera1.apis.Camera1Fragment;
import com.freedcam.apis.camera2.apis.Camera2Fragment;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.sonyremote.apis.SonyCameraFragment;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FreeDPool;


/**
 * Created by troop on 11.12.2014.
 */

public class ApiHandler
{
    private final  String TAG = ApiHandler.class.getSimpleName();
    private Context context;
    private AppSettingsManager appSettingsManager;

    private ApiEvent event;

    public ApiHandler(Context context, ApiEvent event,AppSettingsManager appSettingsManager) {
        this.event = event;
        this.context = context;
        this.appSettingsManager = appSettingsManager;
    }

    public void CheckApi()
    {
        if (appSettingsManager.IsCamera2FullSupported().equals(""))
        {
            if (Build.VERSION.SDK_INT >= 21)
            {
                FreeDPool.Execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean legacy = CameraHolderApi2.IsLegacy(appSettingsManager,context);
                        if (legacy) {
                            appSettingsManager.SetCamera2FullSupported("false");
                            appSettingsManager.setCamApi(AppSettingsManager.API_1);
                        } else {
                            appSettingsManager.SetCamera2FullSupported("true");
                            appSettingsManager.setCamApi(AppSettingsManager.API_2);
                        }
                        event.apiDetectionDone();
                    }
                });

            }
            else {
                appSettingsManager.SetCamera2FullSupported("false");
                appSettingsManager.setCamApi(AppSettingsManager.API_1);
                event.apiDetectionDone();
            }
        }
        else
            event.apiDetectionDone();
    }


    public AbstractCameraFragment getCameraFragment()
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
        ret.SetAppSettingsManager(appSettingsManager);
        return ret;
    }

    public interface ApiEvent
    {
        void apiDetectionDone();
    }


}
