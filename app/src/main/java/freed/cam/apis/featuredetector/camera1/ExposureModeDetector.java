package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ExposureModeDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectExposureModes(cameraCharacteristics);
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.exposure))!= null) {
            detectMode(parameters,R.string.exposure,R.string.exposure_mode_values, settingsManager.get(SettingKeys.ExposureMode));
        }
        else if (parameters.get(camstring(R.string.auto_exposure_values))!= null) {
            detectMode(parameters,R.string.auto_exposure,R.string.auto_exposure_values, settingsManager.get(SettingKeys.ExposureMode));
        }
        else if(parameters.get(camstring(R.string.sony_metering_mode))!= null) {
            detectMode(parameters,R.string.sony_metering_mode,R.string.sony_metering_mode_values, settingsManager.get(SettingKeys.ExposureMode));
        }
        else if(parameters.get(camstring(R.string.exposure_meter))!= null) {
            detectMode(parameters,R.string.exposure_meter,R.string.exposure_meter_values, settingsManager.get(SettingKeys.ExposureMode));
        }
        else if (parameters.get(camstring(R.string.hw_exposure_mode_values)) != null)
            detectMode(parameters, R.string.hw_exposure_mode,R.string.hw_exposure_mode_values, settingsManager.get(SettingKeys.ExposureMode));
        if (!TextUtils.isEmpty(settingsManager.get(SettingKeys.ExposureMode).getCamera1ParameterKEY()))
            settingsManager.get(SettingKeys.ExposureMode).setIsSupported(true);
        else
            settingsManager.get(SettingKeys.ExposureMode).setIsSupported(false);
    }
}
