package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class WhiteBalanceModeDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectWhiteBalanceModes(cameraCharacteristics);
    }

    private void detectWhiteBalanceModes(Camera.Parameters parameters)
    {
        detectMode(parameters, R.string.whitebalance,R.string.whitebalance_values, settingsManager.get(SettingKeys.WhiteBalanceMode));
    }
}
