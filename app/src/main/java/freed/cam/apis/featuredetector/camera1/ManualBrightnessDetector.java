package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class ManualBrightnessDetector extends BaseParameter1Detector{

    private final String TAG = ManualBrightnessDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualBrightness(cameraCharacteristics);
    }

    private void detectManualBrightness(Camera.Parameters parameters) {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Brightness: MTK");
            if (parameters.get(camstring(R.string.brightness))!= null && parameters.get(camstring(R.string.brightness_values))!= null) {
                settingsManager.get(SettingKeys.M_Brightness).setValues(parameters.get(camstring(R.string.brightness_values)).split(","));
                settingsManager.get(SettingKeys.M_Brightness).setCamera1ParameterKEY(camstring(R.string.brightness));
                settingsManager.get(SettingKeys.M_Brightness).setIsSupported(true);
                settingsManager.get(SettingKeys.M_Brightness).set(parameters.get(camstring(R.string.brightness)));
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(camstring(R.string.brightness_max)) != null && parameters.get(camstring(R.string.brightness_min)) != null) {
                    Log.d(TAG, "Brightness: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.brightness_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.brightness_max)));
                } else if (parameters.get(camstring(R.string.max_brightness)) != null && parameters.get(camstring(R.string.min_brightness)) != null) {
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_brightness)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_brightness)));
                    Log.d(TAG, "Brightness: Default");
                }
                else if (parameters.get("brightness-values") != null)
                {
                    settingsManager.get(SettingKeys.M_Brightness).setValues(parameters.get("brightness-values").split(","));
                    settingsManager.get(SettingKeys.M_Brightness).setCamera1ParameterKEY("brightness");
                    settingsManager.get(SettingKeys.M_Brightness).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_Brightness).set("2");
                }
                Log.d(TAG, "Brightness Max:" + max);
                if (max > 0) {
                    if (parameters.get(camstring(R.string.brightness)) != null)
                        settingsManager.get(SettingKeys.M_Brightness).setCamera1ParameterKEY(camstring(R.string.brightness));
                    else if (parameters.get(camstring(R.string.luma_adaptation)) != null)
                        settingsManager.get(SettingKeys.M_Brightness).setCamera1ParameterKEY(camstring(R.string.luma_adaptation));
                    settingsManager.get(SettingKeys.M_Brightness).setValues(createStringArray(min, max, 1));
                    settingsManager.get(SettingKeys.M_Brightness).set(parameters.get(settingsManager.get(SettingKeys.M_Brightness).getCamera1ParameterKEY()));
                    settingsManager.get(SettingKeys.M_Brightness).setIsSupported(true);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Brightness).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_Brightness).setIsSupported(false);
            }
        }
    }
}
