package com.troop.freecam.manager.parameters;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 26.01.14.
 */
public class ExposureModeManager extends BaseParametersManager
{
    public ExposureModeClass ExposureMode;
    boolean supportExposureMode = false;
    public boolean getSupportExposureMode() { return supportExposureMode; }

    public ExposureModeManager(CameraManager cameraManager, SettingsManager preferences) {
        super(cameraManager, preferences);
    }

    @Override
    public void SetCameraParameters(Camera.Parameters parameters) {
        super.SetCameraParameters(parameters);
        ExposureMode = new ExposureModeClass();
    }

    @Override
    protected void loadDefaultOrLastSavedSettings() {
        super.loadDefaultOrLastSavedSettings();
        if (getSupportExposureMode() && !cameraManager.Settings.ExposureMode.Get().equals(""))
            ExposureMode.set(cameraManager.Settings.ExposureMode.Get());
    }

    public class ExposureModeClass
    {
        String[] exposureValues;
        String valuesToGet;
        String valueforExpo;
        public ExposureModeClass()
        {

            try
            {
                exposureValues = parameters.get("exposure-mode-values").split(",");
                supportExposureMode = true;
                valuesToGet = "exposure-mode-values";
                valueforExpo = "exposure";
            }
            catch (Exception ex)
            {
                supportExposureMode = false;
            }
            if (supportExposureMode == false)
            {
                try
                {
                    exposureValues = parameters.get("auto-exposure-values").split(",");
                    supportExposureMode = true;
                    valuesToGet = "auto-exposure-values";
                    valueforExpo = "auto-exposure";
                }
                catch (Exception ex)
                {
                    supportExposureMode = false;
                }
            }
            Log.d(TAG, "support ExposureModes:" + supportExposureMode);
        }

        public String get()
        {
            return parameters.get(valueforExpo);
        }

        public String[] getExposureValues()
        {
            return exposureValues;
        }

        public void set(String value)
        {
            String def = get();
            if (!def.equals(value))
            {
                try {
                    Log.d(TAG, "Try set ExposureMode to " +value);
                    parameters.set(valueforExpo, value);
                    cameraManager.Restart(false);
                }
                catch (Exception ex)
                {
                    Log.e(TAG,"Exposure set failed, set back to"+def);
                    parameters.set(valueforExpo, def);
                    cameraManager.Restart(false);
                }
            }
            onParametersCHanged(enumParameters.ExposureMode);

        }
    }
}
