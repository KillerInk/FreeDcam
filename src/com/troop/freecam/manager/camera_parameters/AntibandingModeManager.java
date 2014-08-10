package com.troop.freecam.manager.camera_parameters;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.AppSettingsManager;

/**
 * Created by troop on 27.01.14.
 */
public class AntibandingModeManager extends MeteringModeManager
{
    boolean supportAntibanding = false;
    public boolean getSupportAntibanding(){return  supportAntibanding;}
    public AntibandingClass Antibanding;
    public AntibandingModeManager(CameraManager cameraManager, AppSettingsManager preferences) {
        super(cameraManager, preferences);
    }

    @Override
    protected void loadDefaultOrLastSavedSettings() {
        super.loadDefaultOrLastSavedSettings();
        if (getSupportAntibanding() && !cameraManager.Settings.Antibanding.Get().equals(""))
        {
            parameters.set("antibanding", cameraManager.Settings.Antibanding.Get());
        }
    }

    @Override
    public void SetCameraParameters(Camera.Parameters parameters) {
        super.SetCameraParameters(parameters);
        Antibanding = new AntibandingClass();
    }

    public class AntibandingClass
    {
        String[] values;
        public AntibandingClass()
        {

            try
            {
                values = parameters.get("antibanding-values").split(",");
                supportAntibanding = true;
            }
            catch (Exception ex)
            {
                supportAntibanding = false;
            }
            Log.d(TAG, "Antibanding supported:" + supportAntibanding);
        }

        public String[] GetValues()
        {
            return  values;
        }

        public String Get()
        {
            return parameters.get("antibanding");
        }

        public void Set(String value)
        {
            Log.d(TAG,"try to set antibanding to:" + value);
            try {
                parameters.set("antibanding", value);
                cameraManager.ReloadCameraParameters(false);
                onParametersCHanged(enumParameters.AntiBanding);
                Log.d(TAG, "antibanding set");
            }
            catch (Exception ex)
            {
                Log.e(TAG, "antibanding set failed");
                ex.printStackTrace();
            }
        }
    }
}
