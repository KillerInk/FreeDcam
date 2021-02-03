package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class DenoiseDetector extends BaseParameter1Detector {
    private final String TAG = DenoiseDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectDenoise(cameraCharacteristics);
    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        Log.d(TAG, "Denoise is Presetted: "+ SettingsManager.get(SettingKeys.Denoise).isPresetted());
        if (SettingsManager.get(SettingKeys.Denoise).isPresetted())
            return;
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            try {
                if (parameters.get(camstring(R.string.mtk_3dnr_mode)) != null) {
                    if (parameters.get(camstring(R.string.mtk_3dnr_mode_values)).equals("on,off")) {
                        SettingsManager.get(SettingKeys.Denoise).setIsSupported(true);
                        SettingsManager.get(SettingKeys.Denoise).setCamera1ParameterKEY(camstring(R.string.mtk_3dnr_mode));
                        SettingsManager.get(SettingKeys.Denoise).setValues(parameters.get(camstring(R.string.mtk_3dnr_mode_values)).split(","));
                    }
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.Denoise).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.Denoise).setIsSupported(false);
            }
        }
        else
        {
            detectMode(parameters,R.string.denoise,R.string.denoise_values, SettingsManager.get(SettingKeys.Denoise));
        }
    }
}
