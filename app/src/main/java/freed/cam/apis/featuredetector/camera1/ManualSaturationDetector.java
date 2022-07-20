package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ManualSaturationDetector extends BaseParameter1Detector{

    private final String TAG = ManualSaturationDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualSaturation(cameraCharacteristics);
    }

    private void detectManualSaturation(Camera.Parameters parameters)
    {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            try {
                Log.d(TAG, "Saturation: MTK");
                if (parameters.get(camstring(R.string.saturation)) != null && parameters.get(camstring(R.string.saturation_values)) != null) {
                    settingsManager.get(SettingKeys.M_SATURATION).setValues(parameters.get(camstring(R.string.saturation_values)).split(","));
                    settingsManager.get(SettingKeys.M_SATURATION).setCamera1ParameterKEY(camstring(R.string.saturation));
                    settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_SATURATION).set(parameters.get(camstring(R.string.saturation)));
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(false);
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(FreedApplication.getStringFromRessources(R.string.lg_color_adjust_max)) != null
                        && parameters.get(FreedApplication.getStringFromRessources(R.string.lg_color_adjust_min)) != null) {
                    Log.d(TAG, "Saturation: LG");
                    min = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.lg_color_adjust_min)));
                    max = Integer.parseInt(parameters.get(FreedApplication.getStringFromRessources(R.string.lg_color_adjust_max)));
                    settingsManager.get(SettingKeys.M_SATURATION).setCamera1ParameterKEY(FreedApplication.getStringFromRessources(R.string.lg_color_adjust));
                    settingsManager.get(SettingKeys.M_SATURATION).set(parameters.get(camstring(R.string.lg_color_adjust)));
                } else if (parameters.get(camstring(R.string.saturation_max)) != null) {
                    Log.d(TAG, "Saturation: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.saturation_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.saturation_max)));
                    settingsManager.get(SettingKeys.M_SATURATION).setCamera1ParameterKEY(camstring(R.string.saturation));
                    settingsManager.get(SettingKeys.M_SATURATION).set(parameters.get(camstring(R.string.saturation)));
                } else if (parameters.get(camstring(R.string.max_saturation)) != null && parameters.get(camstring(R.string.min_saturation)) != null) {
                    Log.d(TAG, "Saturation: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_saturation)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_saturation)));
                    settingsManager.get(SettingKeys.M_SATURATION).setCamera1ParameterKEY(camstring(R.string.saturation));
                    settingsManager.get(SettingKeys.M_SATURATION).set(parameters.get(camstring(R.string.saturation)));
                }
                else if (parameters.get("saturation-values") != null)
                {
                    settingsManager.get(SettingKeys.M_SATURATION).setValues(parameters.get("saturation-values").split(","));
                    settingsManager.get(SettingKeys.M_SATURATION).setCamera1ParameterKEY("saturation");
                    settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_SATURATION).set("2");
                }
                Log.d(TAG, "Saturation Max:" + max);
                if (max > 0) {
                    settingsManager.get(SettingKeys.M_SATURATION).setValues(createStringArray(min, max, 1));
                    settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(true);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SATURATION).setIsSupported(false);
            }
        }
    }
}
