package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import freed.settings.SettingKeys;

public class DualPrimaryCameraDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        if (cameraCharacteristics.get("hw-dual-primary-supported") != null)
        {
            settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).setValues(cameraCharacteristics.get("hw-dual-primary-supported").split(","));
            settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).setCamera1ParameterKEY("hw-dual-primary-mode");
            settingsManager.get(SettingKeys.DUAL_PRIMARY_CAMERA_MODE).setIsSupported(true);
        }
    }
}
