package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class TemporalNoiseReductionDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectTNR(cameraCharacteristics);
    }

    private void detectTNR(Camera.Parameters parameters)
    {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            SettingsManager.get(SettingKeys.TNR).setIsSupported(false);
            SettingsManager.get(SettingKeys.TNR_V).setIsSupported(false);
            return;
        }
        else
        {
            try {
                detectMode(parameters, R.string.tnr, R.string.tnr_mode, SettingsManager.get(SettingKeys.TNR));
                detectMode(parameters, R.string.tnr_v, R.string.tnr_mode_v, SettingsManager.get(SettingKeys.TNR_V));
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.TNR).setIsSupported(false);
                SettingsManager.get(SettingKeys.TNR_V).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.TNR).setIsSupported(false);
                SettingsManager.get(SettingKeys.TNR_V).setIsSupported(false);
            }
        }

    }
}
