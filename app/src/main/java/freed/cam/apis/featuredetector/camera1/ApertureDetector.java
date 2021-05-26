package freed.cam.apis.featuredetector.camera1;


import android.hardware.Camera;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ApertureDetector extends BaseParameter1Detector {

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        if (cameraCharacteristics.get("hw-supported-aperture-value") != null)
        {
            settingsManager.get(SettingKeys.M_Aperture).setCamera1ParameterKEY("hw-set-aperture-value");
            settingsManager.get(SettingKeys.M_Aperture).setValues(cameraCharacteristics.get("hw-supported-aperture-value").split(","));
            settingsManager.get(SettingKeys.M_Aperture).setIsSupported(true);
        }
    }
}
