package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class OptizoomDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectOptizoom(cameraCharacteristics);
    }

    private void detectOptizoom(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.OptiZoom).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.optizoom,R.string.optizoom_mode, SettingsManager.get(SettingKeys.OptiZoom));
        }

    }
}
