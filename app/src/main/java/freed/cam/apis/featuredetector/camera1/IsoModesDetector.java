package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class IsoModesDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectIsoModes(cameraCharacteristics);
    }

    private void detectIsoModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.iso_mode_values))!= null){
            detectMode(parameters,R.string.iso,R.string.iso_mode_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.iso_speed_values))!= null) {
            detectMode(parameters,R.string.iso_speed,R.string.iso_speed_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.sony_iso_values))!= null) {
            detectMode(parameters,R.string.sony_iso,R.string.sony_iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        else if (parameters.get(camstring(R.string.lg_iso_values))!= null) {
            detectMode(parameters,R.string.iso,R.string.lg_iso_values, SettingsManager.get(SettingKeys.IsoMode));
        }
        if (SettingsManager.get(SettingKeys.IsoMode).getValues() != null && SettingsManager.get(SettingKeys.IsoMode).getValues().length >1)
            SettingsManager.get(SettingKeys.IsoMode).setIsSupported(true);
        else
            SettingsManager.get(SettingKeys.IsoMode).setIsSupported(false);
    }
}
