package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CameraCharacteristicsQcom;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class SharpnessDetector  extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int sharprange[] = cameraCharacteristics.get(CameraCharacteristicsQcom.sharpness_range);
        int min = sharprange[0];
        int max = sharprange[1];
        String[] t = new String[max - min + 1];
        for (int i= 0; i < t.length; i++)
            t[i] = ""+(min+i);
        if (sharprange.length > 0) {
            SettingsManager.get(SettingKeys.M_Sharpness).setValues(t);
            SettingsManager.get(SettingKeys.M_Sharpness).set((max/2) + "");
            SettingsManager.get(SettingKeys.M_Sharpness).setIsSupported(true);
        }
    }
}
