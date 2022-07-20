package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.settings.SettingKeys;

public class NonZslManualDetector extends BaseParameter1Detector {
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectNonZslmanual(cameraCharacteristics);
    }

    private void detectNonZslmanual(Camera.Parameters parameters) {
        if(parameters.get("non-zsl-manual-mode")!=null)
        {
            settingsManager.get(SettingKeys.NON_ZSL_MANUAL_MODE).setIsSupported(true);
            settingsManager.get(SettingKeys.NON_ZSL_MANUAL_MODE).setCamera1ParameterKEY("non-zsl-manual-mode");
            settingsManager.get(SettingKeys.NON_ZSL_MANUAL_MODE).setValues(new String[]{FreedApplication.getStringFromRessources(R.string.on_), FreedApplication.getStringFromRessources(R.string.off_)});
        }
    }
}
