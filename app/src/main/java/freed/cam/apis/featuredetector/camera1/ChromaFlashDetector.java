package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;

public class ChromaFlashDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectChromaFlash(cameraCharacteristics);
    }

    private void detectChromaFlash(Camera.Parameters parameters)
    {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            settingsManager.get(SettingKeys.ChromaFlash).setIsSupported(false);
            return;
        }
        else
        {
            detectMode(parameters, R.string.chroma,R.string.chroma_mode, settingsManager.get(SettingKeys.ChromaFlash));
        }

    }
}
