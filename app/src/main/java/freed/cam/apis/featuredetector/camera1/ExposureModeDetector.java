package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class ExposureModeDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectExposureModes(cameraCharacteristics);
    }

    private void detectExposureModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.exposure))!= null) {
            detectMode(parameters,R.string.exposure,R.string.exposure_mode_values, settingsManager.get(SettingKeys.EXPOSURE_MODE));
        }
        else if (parameters.get(camstring(R.string.auto_exposure_values))!= null) {
            detectMode(parameters,R.string.auto_exposure,R.string.auto_exposure_values, settingsManager.get(SettingKeys.EXPOSURE_MODE));
        }
        else if(parameters.get(camstring(R.string.sony_metering_mode))!= null) {
            detectMode(parameters,R.string.sony_metering_mode,R.string.sony_metering_mode_values, settingsManager.get(SettingKeys.EXPOSURE_MODE));
        }
        else if(parameters.get(camstring(R.string.exposure_meter))!= null) {
            detectMode(parameters,R.string.exposure_meter,R.string.exposure_meter_values, settingsManager.get(SettingKeys.EXPOSURE_MODE));
        }
        else if (parameters.get(camstring(R.string.hw_exposure_mode_values)) != null)
            detectMode(parameters, R.string.hw_exposure_mode,R.string.hw_exposure_mode_values, settingsManager.get(SettingKeys.EXPOSURE_MODE));
        settingsManager.get(SettingKeys.EXPOSURE_MODE).setIsSupported(!TextUtils.isEmpty(settingsManager.get(SettingKeys.EXPOSURE_MODE).getCamera1ParameterKEY()));
    }
}
