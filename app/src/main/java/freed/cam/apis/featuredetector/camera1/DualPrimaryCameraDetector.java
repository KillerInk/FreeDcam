package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class DualPrimaryCameraDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        if (cameraCharacteristics.get("hw-dual-primary-supported") != null)
        {
            SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setValues(cameraCharacteristics.get("hw-dual-primary-supported").split(","));
            SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setCamera1ParameterKEY("hw-dual-primary-mode");
            SettingsManager.get(SettingKeys.dualPrimaryCameraMode).setIsSupported(true);
        }
    }
}
