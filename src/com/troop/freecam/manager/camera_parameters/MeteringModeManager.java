package com.troop.freecam.manager.camera_parameters;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.manager.AppSettingsManager;

/**
 * Created by troop on 26.01.14.
 */
public class MeteringModeManager extends ExposureModeManager
{
    boolean supportMeteringMode = false;
    public boolean getSupportMeteringMode() { return supportMeteringMode; }
    public MeteringModeClass MeteringMode;

    public MeteringModeManager(CameraManager cameraManager, AppSettingsManager preferences) {
        super(cameraManager, preferences);
    }

    @Override
    public void SetCameraParameters(Camera.Parameters parameters) {
        super.SetCameraParameters(parameters);
        MeteringMode = new MeteringModeClass();
    }

    @Override
    protected void loadDefaultOrLastSavedSettings() {
        super.loadDefaultOrLastSavedSettings();
        if (!supportMeteringMode && !cameraManager.Settings.MeteringMode.Get().equals(""))
            MeteringMode.Set(cameraManager.Settings.MeteringMode.Get());
    }

    public class MeteringModeClass
    {
        public MeteringModeClass()
        {
            try
            {
                if (!parameters.get("meter-mode-values").equals(""))
                    supportMeteringMode= true;
            }
            catch (Exception ex)
            {
                supportMeteringMode = false;
            }
            Log.d(TAG, "support meteringMode:" + supportMeteringMode);
        }

        public String Get()
        {
            return parameters.get("meter-mode");
        }

        public void Set(String mode)
        {
            try {
                parameters.set("meter-mode", mode);
                cameraManager.ReloadCameraParameters(false);
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Set MeteringMode failed");
            }
        }

        public String[] getValues()
        {
            return parameters.get("meter-mode-values").split(",");
        }
    }
}
