package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ChromaFlashDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectChromaFlash(cameraCharacteristics);
    }

    private void detectChromaFlash(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.ChromaFlash).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.chroma,R.string.chroma_mode, SettingsManager.get(SettingKeys.ChromaFlash));
        }

    }
}
