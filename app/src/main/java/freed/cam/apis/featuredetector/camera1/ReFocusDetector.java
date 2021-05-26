package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ReFocusDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectRefocus(cameraCharacteristics);
    }

    private void detectRefocus(Camera.Parameters parameters)
    {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            settingsManager.get(SettingKeys.ReFocus).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.refocus,R.string.refocus_mode, settingsManager.get(SettingKeys.ReFocus));
        }

    }
}
