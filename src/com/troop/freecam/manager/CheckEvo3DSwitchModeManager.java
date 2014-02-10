package com.troop.freecam.manager;

import android.os.Handler;
import android.provider.Settings;

import com.troop.freecam.MainActivity;
import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 09.02.14.
 */
public class CheckEvo3DSwitchModeManager
{
    private final int interval = 300;
    boolean stop = false;
    private Handler handler = new Handler();
    CameraManager cameraManager;
    boolean is3d = false;

    public CheckEvo3DSwitchModeManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            try {
                boolean last = is3d;
                if (DeviceUtils.isEvo3d() && !cameraManager.Settings.Cameras.isFrontMode() )
                    is3d = android.provider.Settings.System.getInt(cameraManager.activity.getBaseContext().getContentResolver(), "htc_2d_3d_mode") == 0;
                if (is3d != last && !cameraManager.IsWorking && cameraManager.isRdy)
                {
                    if (is3d)
                    {
                        cameraManager.Settings.Cameras.SetCameraEnum(SettingsManager.CameraValues.Back3D);
                    }
                    else
                    {
                        cameraManager.Settings.Cameras.SetCameraEnum(SettingsManager.CameraValues.Back2D);
                    }
                    cameraManager.Stop();
                    cameraManager.activity.mPreview.SwitchViewMode();
                    //cameraManager.activity.drawSurface.SwitchViewMode();

                    cameraManager.Start();
                    cameraManager.Restart(true);
                    //cameraManager.activity.drawSurface.drawingRectHelper.Draw();
                    cameraManager.activity.SwitchCropButton();
                }

            } catch (Settings.SettingNotFoundException ex) {
                //
            }
            if(!stop)
                handler.postDelayed(runnable, interval);
        }
    };

    public  void Start()
    {
        //handler.postAtTime(runnable, System.currentTimeMillis()+interval);
        stop = false;
        handler.postDelayed(runnable, interval);
    }

    public void Stop()
    {
        stop = true;

    }
}
