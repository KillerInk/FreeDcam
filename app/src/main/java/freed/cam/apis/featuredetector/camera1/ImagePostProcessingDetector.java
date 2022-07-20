package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class ImagePostProcessingDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.ipp,R.string.ipp_values, settingsManager.get(SettingKeys.IMAGE_POST_PROCESSING));
    }
}
