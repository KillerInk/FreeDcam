package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import freed.settings.SettingKeys;

public class DualPrimaryCameraDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        if (cameraCharacteristics.get("hw-dual-primary-supported") != null)
        {
            settingsManager.get(SettingKeys.dualPrimaryCameraMode).setValues(cameraCharacteristics.get("hw-dual-primary-supported").split(","));
            settingsManager.get(SettingKeys.dualPrimaryCameraMode).setCamera1ParameterKEY("hw-dual-primary-mode");
            settingsManager.get(SettingKeys.dualPrimaryCameraMode).setIsSupported(true);
        }
    }
}
