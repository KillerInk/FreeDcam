package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class DigitalImageStabDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectDisModes(cameraCharacteristics);
    }

    private void detectDisModes(Camera.Parameters parameters) {
        if (settingsManager.get(SettingKeys.DigitalImageStabilization).isPresetted())
            return;
        if (settingsManager.getFrameWork() == Frameworks.MTK) {
            settingsManager.get(SettingKeys.DigitalImageStabilization).setIsSupported(false);
        } else{
            detectMode(parameters, R.string.dis,R.string.dis_values, settingsManager.get(SettingKeys.DigitalImageStabilization));
        }
    }
}
