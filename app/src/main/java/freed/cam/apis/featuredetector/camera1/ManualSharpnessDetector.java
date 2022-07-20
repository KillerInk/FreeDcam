package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class ManualSharpnessDetector extends BaseParameter1Detector{

    private final String TAG = ManualSharpnessDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualSharpness(cameraCharacteristics);
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Sharpness: MTK");
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                settingsManager.get(SettingKeys.M_SHARPNESS).setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                settingsManager.get(SettingKeys.M_SHARPNESS).setCamera1ParameterKEY(camstring(R.string.edge));
                settingsManager.get(SettingKeys.M_SHARPNESS).setIsSupported(true);
                settingsManager.get(SettingKeys.M_SHARPNESS).set(parameters.get(camstring(R.string.edge)));
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                    Log.d(TAG, "Sharpness: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                    settingsManager.get(SettingKeys.M_SHARPNESS).setCamera1ParameterKEY(camstring(R.string.sharpness));
                    settingsManager.get(SettingKeys.M_SHARPNESS).set(parameters.get(camstring(R.string.sharpness)));
                } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                    Log.d(TAG, "Sharpness: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                    settingsManager.get(SettingKeys.M_SHARPNESS).setCamera1ParameterKEY(camstring(R.string.sharpness));
                    settingsManager.get(SettingKeys.M_SHARPNESS).set(parameters.get(camstring(R.string.sharpness)));
                }
                else if (parameters.get("sharpness-values") != null)
                {
                    settingsManager.get(SettingKeys.M_SHARPNESS).setValues(parameters.get("sharpness-values").split(","));
                    settingsManager.get(SettingKeys.M_SHARPNESS).setCamera1ParameterKEY("sharpness");
                    settingsManager.get(SettingKeys.M_SHARPNESS).setIsSupported(true);
                }
                Log.d(TAG, "Sharpness Max:" +max);
                if (max > 0) {
                    settingsManager.get(SettingKeys.M_SHARPNESS).setValues(createStringArray(min, max, 1));
                    settingsManager.get(SettingKeys.M_SHARPNESS).setIsSupported(true);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SHARPNESS).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                settingsManager.get(SettingKeys.M_SHARPNESS).setIsSupported(false);
            }
        }
    }
}
