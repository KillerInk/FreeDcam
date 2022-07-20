package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class DenoiseDetector extends BaseParameter1Detector {
    private final String TAG = DenoiseDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectDenoise(cameraCharacteristics);
    }

    private void detectDenoise(Camera.Parameters parameters)
    {
        Log.d(TAG, "Denoise is Presetted: "+ settingsManager.get(SettingKeys.DENOISE).isPresetted());
        if (settingsManager.get(SettingKeys.DENOISE).isPresetted())
            return;
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            try {
                if (parameters.get(camstring(R.string.mtk_3dnr_mode)) != null) {
                    if (parameters.get(camstring(R.string.mtk_3dnr_mode_values)).equals("on,off")) {
                        settingsManager.get(SettingKeys.DENOISE).setIsSupported(true);
                        settingsManager.get(SettingKeys.DENOISE).setCamera1ParameterKEY(camstring(R.string.mtk_3dnr_mode));
                        settingsManager.get(SettingKeys.DENOISE).setValues(parameters.get(camstring(R.string.mtk_3dnr_mode_values)).split(","));
                    }
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.DENOISE).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.DENOISE).setIsSupported(false);
            }
        }
        else
        {
            detectMode(parameters,R.string.denoise,R.string.denoise_values, settingsManager.get(SettingKeys.DENOISE));
        }
    }
}
