package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class ManualSharpnessDetector extends BaseParameter1Detector{

    private final String TAG = ManualSharpnessDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectManualSharpness(cameraCharacteristics);
    }

    private void detectManualSharpness(Camera.Parameters parameters) {
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            Log.d(TAG, "Sharpness: MTK");
            if (parameters.get(camstring(R.string.edge))!= null && parameters.get(camstring(R.string.edge_values))!= null) {
                SettingsManager.get(SettingKeys.M_Sharpness).setValues(parameters.get(camstring(R.string.edge_values)).split(","));
                SettingsManager.get(SettingKeys.M_Sharpness).setCamera1ParameterKEY(camstring(R.string.edge));
                SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
                SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.edge)));
            }
        }
        else {
            try {
                int min = 0, max = 0;
                if (parameters.get(camstring(R.string.sharpness_max)) != null) {
                    Log.d(TAG, "Sharpness: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.sharpness_min)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.sharpness_max)));
                    SettingsManager.get(SettingKeys.M_Sharpness).setCamera1ParameterKEY(camstring(R.string.sharpness));
                    SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.sharpness)));
                } else if (parameters.get(camstring(R.string.max_sharpness)) != null) {
                    Log.d(TAG, "Sharpness: Default");
                    min = Integer.parseInt(parameters.get(camstring(R.string.min_sharpness)));
                    max = Integer.parseInt(parameters.get(camstring(R.string.max_sharpness)));
                    SettingsManager.get(SettingKeys.M_Sharpness).setCamera1ParameterKEY(camstring(R.string.sharpness));
                    SettingsManager.get(SettingKeys.M_Sharpness).set(parameters.get(camstring(R.string.sharpness)));
                }
                else if (parameters.get("sharpness-values") != null)
                {
                    SettingsManager.get(SettingKeys.M_Sharpness).setValues(parameters.get("sharpness-values").split(","));
                    SettingsManager.get(SettingKeys.M_Sharpness).setCamera1ParameterKEY("sharpness");
                    SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
                }
                Log.d(TAG, "Sharpness Max:" +max);
                if (max > 0) {
                    SettingsManager.get(SettingKeys.M_Sharpness).setValues(createStringArray(min, max, 1));
                    SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
                }
            }
            catch (NumberFormatException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(false);
            }
            catch(ArrayIndexOutOfBoundsException ex)
            {
                Log.WriteEx(ex);
                SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(false);
            }
        }
    }
}
