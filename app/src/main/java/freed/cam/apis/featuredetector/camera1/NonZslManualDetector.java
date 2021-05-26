package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class NonZslManualDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectNonZslmanual(cameraCharacteristics);
    }

    private void detectNonZslmanual(Camera.Parameters parameters) {
        if(parameters.get("non-zsl-manual-mode")!=null)
        {
            settingsManager.get(SettingKeys.NonZslManualMode).setIsSupported(true);
            settingsManager.get(SettingKeys.NonZslManualMode).setCamera1ParameterKEY("non-zsl-manual-mode");
            settingsManager.get(SettingKeys.NonZslManualMode).setValues(new String[]{FreedApplication.getStringFromRessources(R.string.on_), FreedApplication.getStringFromRessources(R.string.off_)});
        }
    }
}
