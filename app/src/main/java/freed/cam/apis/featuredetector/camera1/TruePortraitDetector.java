package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class TruePortraitDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectTruePotrait(cameraCharacteristics);
    }

    private void detectTruePotrait(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.TruePotrait).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.truepotrait,R.string.truepotrait_mode, SettingsManager.get(SettingKeys.TruePotrait));
        }

    }
}
