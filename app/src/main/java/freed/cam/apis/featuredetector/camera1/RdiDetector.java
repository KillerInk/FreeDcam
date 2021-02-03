package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class RdiDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectRDI(cameraCharacteristics);
    }

    private void detectRDI(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.RDI).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.rdi,R.string.rdi_mode, SettingsManager.get(SettingKeys.RDI));
        }

    }
}
