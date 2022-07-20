package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class PreviewFpsDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.preview_frame_rate,R.string.preview_frame_rate_values, settingsManager.get(SettingKeys.PREVIEW_FPS));
    }
}
