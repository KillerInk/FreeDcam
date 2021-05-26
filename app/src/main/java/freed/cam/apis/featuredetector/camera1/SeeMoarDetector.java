package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class SeeMoarDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectSEEMoar(cameraCharacteristics);
    }

    private void detectSEEMoar(Camera.Parameters parameters)
    {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            settingsManager.get(SettingKeys.SeeMore).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.seemore,R.string.seemore_mode, settingsManager.get(SettingKeys.SeeMore));
        }

    }
}
