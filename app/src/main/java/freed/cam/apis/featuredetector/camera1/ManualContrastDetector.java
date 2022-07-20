package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ManualContrastDetector  extends BaseParameter1Detector{
    private final String TAG = ManualContrastDetector.class.getSimpleName();
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualContrast(cameraCharacteristics);
    }

    private void detectManualContrast(Camera.Parameters parameters) {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            if (parameters.get(camstring(R.string.contrast))!= null && parameters.get(camstring(R.string.contrast_values))!= null) {
                settingsManager.get(SettingKeys.M_CONTRAST).setValues(parameters.get(camstring(R.string.contrast_values)).split(","));
                settingsManager.get(SettingKeys.M_CONTRAST).setCamera1ParameterKEY(camstring(R.string.contrast));
                settingsManager.get(SettingKeys.M_CONTRAST).setIsSupported(true);
                settingsManager.get(SettingKeys.M_CONTRAST).set(parameters.get(camstring(R.string.contrast)));
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(camstring(R.string.contrast_max)) != null) {
                    min = Integer.parseInt(parameters.get(camstring(R.string.contrast_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.contrast_max)));
                } else if (parameters.get(camstring(R.string.max_contrast)) != null) {
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_contrast)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_contrast)));

                }
                else if (parameters.get("contrast-values") != null)
                {
                    settingsManager.get(SettingKeys.M_CONTRAST).setValues(parameters.get("contrast-values").split(","));
                    settingsManager.get(SettingKeys.M_CONTRAST).setCamera1ParameterKEY("contrast"); // constrast is not a typo. on huawei side it is
                    settingsManager.get(SettingKeys.M_CONTRAST).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_CONTRAST).set("2");
                }
                Log.d(TAG, "Contrast Max:" +max);
                if (max > 0) {
                    settingsManager.get(SettingKeys.M_CONTRAST).setCamera1ParameterKEY(camstring(R.string.contrast));
                    settingsManager.get(SettingKeys.M_CONTRAST).setValues(createStringArray(min, max, 1));
                    settingsManager.get(SettingKeys.M_CONTRAST).setIsSupported(true);
                    settingsManager.get(SettingKeys.M_CONTRAST).set(parameters.get(camstring(R.string.contrast)));
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_CONTRAST).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_CONTRAST).setIsSupported(false);
            }
        }
    }
}
