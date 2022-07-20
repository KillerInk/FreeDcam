package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class ColorModeDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectColorModes(cameraCharacteristics);
    }
    private void detectColorModes(Camera.Parameters parameters)
    {
        detectMode(parameters, R.string.effect,R.string.effect_values, settingsManager.get(SettingKeys.COLOR_MODE));
    }
}
