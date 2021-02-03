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
        if (SettingsManager.get(SettingKeys.DigitalImageStabilization).isPresetted())
            return;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK) {
            SettingsManager.get(SettingKeys.DigitalImageStabilization).setIsSupported(false);
        } else{
            detectMode(parameters, R.string.dis,R.string.dis_values, SettingsManager.get(SettingKeys.DigitalImageStabilization));
        }
    }
}
