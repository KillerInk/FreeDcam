package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CameraCharacteristicsQcom;
import freed.settings.SettingKeys;

public class SaturationDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int contrastrange[] = cameraCharacteristics.get(CameraCharacteristicsQcom.saturation_range);
        int min = contrastrange[0];
        int max = contrastrange[1];
        String[] t = new String[max - min + 1];
        for (int i= 0; i < t.length; i++)
            t[i] = ""+(min+i);
        if (contrastrange.length > 0) {
            settingsManager.get(SettingKeys.M_Saturation).setValues(t);
            settingsManager.get(SettingKeys.M_Saturation).set((max/2) + "");
            settingsManager.get(SettingKeys.M_Saturation).setIsSupported(true);
        }
    }
}
