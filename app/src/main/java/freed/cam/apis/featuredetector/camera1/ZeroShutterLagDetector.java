package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class ZeroShutterLagDetector  extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectZeroShutterLagModes(cameraCharacteristics);
    }

    private void detectZeroShutterLagModes(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.zsl_values)) != null)
        {
            detectMode(parameters,R.string.zsl,R.string.zsl_values, SettingsManager.get(SettingKeys.ZSL));

        }
        else if (parameters.get(camstring(R.string.mode_values)) != null)
        {
            detectMode(parameters,R.string.mode,R.string.mode_values, SettingsManager.get(SettingKeys.ZSL));
        }
        else if (parameters.get(camstring(R.string.zsd_mode)) != null) {
            detectMode(parameters, R.string.zsd_mode, R.string.zsd_mode_values, SettingsManager.get(SettingKeys.ZSL));
        }

        if (SettingsManager.get(SettingKeys.ZSL).getValues() != null && SettingsManager.get(SettingKeys.ZSL).getValues().length == 0)
            SettingsManager.get(SettingKeys.ZSL).setIsSupported(false);
    }
}
