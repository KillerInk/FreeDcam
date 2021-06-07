package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class MemColorEnhancDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.mce,R.string.mce_values, settingsManager.get(SettingKeys.MemoryColorEnhancement));
    }
}
