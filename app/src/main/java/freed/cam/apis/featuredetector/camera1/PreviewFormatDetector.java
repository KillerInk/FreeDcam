package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class PreviewFormatDetector  extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectMode(cameraCharacteristics, R.string.preview_format,R.string.preview_format_values, settingsManager.get(SettingKeys.PREVIEW_FORMAT));
    }
}
