package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;

public class TruePortraitDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectTruePotrait(cameraCharacteristics);
    }

    private void detectTruePotrait(Camera.Parameters parameters)
    {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            settingsManager.get(SettingKeys.TRUE_POTRAIT).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.truepotrait,R.string.truepotrait_mode, settingsManager.get(SettingKeys.TRUE_POTRAIT));
        }

    }
}
