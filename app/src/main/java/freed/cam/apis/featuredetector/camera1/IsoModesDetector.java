package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class IsoModesDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectIsoModes(cameraCharacteristics);
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.iso_mode_values))!= null){
            detectMode(parameters,R.string.iso,R.string.iso_mode_values, settingsManager.get(SettingKeys.ISO_MODE));
        }
        else if (parameters.get(camstring(R.string.iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.iso_values, settingsManager.get(SettingKeys.ISO_MODE));
        }
        else if (parameters.get(camstring(R.string.iso_speed_values))!= null) {
            detectMode(parameters,R.string.iso_speed,R.string.iso_speed_values, settingsManager.get(SettingKeys.ISO_MODE));
        }
        else if (parameters.get(camstring(R.string.sony_iso_values))!= null) {
            detectMode(parameters,R.string.sony_iso,R.string.sony_iso_values, settingsManager.get(SettingKeys.ISO_MODE));
        }
        else if (parameters.get(camstring(R.string.lg_iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.lg_iso_values, settingsManager.get(SettingKeys.ISO_MODE));
        }
        settingsManager.get(SettingKeys.ISO_MODE).setIsSupported(settingsManager.get(SettingKeys.ISO_MODE).getValues() != null && settingsManager.get(SettingKeys.ISO_MODE).getValues().length > 1);
    }
}
