package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class FocusModeDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectFocusModes(cameraCharacteristics);
    }

    private void detectFocusModes(Camera.Parameters parameters)
    {
        detectMode(parameters, R.string.focus_mode,R.string.focus_mode_values, settingsManager.get(SettingKeys.FocusMode));
    }
}
