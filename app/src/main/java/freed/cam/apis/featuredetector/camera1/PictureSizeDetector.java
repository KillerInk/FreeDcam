package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class PictureSizeDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectPictureSizes(cameraCharacteristics);
    }

    private void detectPictureSizes(Camera.Parameters parameters)
    {
        detectMode(parameters, R.string.picture_size,R.string.picture_size_values, settingsManager.get(SettingKeys.PICTURE_SIZE));
    }
}
