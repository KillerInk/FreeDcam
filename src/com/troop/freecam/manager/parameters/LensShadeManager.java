package com.troop.freecam.manager.parameters;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.SettingsManager;

/**
 * Created by troop on 28.01.14.
 */
public class LensShadeManager extends AntibandingModeManager
{
    boolean supportLensShade = false;
    public boolean getSupportLensShade() { return  supportLensShade;}
    public LensShadeClass LensShade;

    public LensShadeManager(CameraManager cameraManager, SettingsManager preferences) {
        super(cameraManager, preferences);
        if (supportLensShade)
            LensShade.set(cameraManager.Settings.LensShade.get());
    }

    @Override
    public void SetCameraParameters(Camera.Parameters parameters) {
        super.SetCameraParameters(parameters);
        LensShade = new LensShadeClass();
    }

    public class LensShadeClass
    {
        public LensShadeClass()
        {
            try
            {
                String l = parameters.get("lensshade");
                if (!l.equals(""))
                    supportLensShade = true;
            }
            catch (Exception ex)
            {
                supportLensShade = false;
            }
            Log.d(TAG, "LensShade supported:" + supportLensShade);
        }

        public boolean Get()
        {
            String l = parameters.get("lensshade");
            if (l.equals("enable"))
                return  true;
            else
                return false;
        }

        public void set(boolean mode)
        {
            if (mode)
                parameters.set("lensshade", "enable");
            else
                parameters.set("lensshade", "disable");
            cameraManager.ReloadCameraParameters(false);
            Log.d(TAG, "Set LensShade TO:" + parameters.get("lensshade"));
            onParametersCHanged(enumParameters.LensShade);
        }

    }
}
