package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeMeteringModeDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int meteringareas = cameraCharacteristics.get(CameraCharacteristics.CONTROL_MAX_REGIONS_AE);
        if (meteringareas > 0)
        {
            settingsManager.get(SettingKeys.AE_METERING).setIsSupported(true);
            settingsManager.get(SettingKeys.AE_METERING).setValues(new String[]{"Frame Average","Spot Center","Spot Focus", "Spot"});
            settingsManager.get(SettingKeys.AE_METERING).set("Frame Average");
        }
        else
            settingsManager.get(SettingKeys.AE_METERING).setIsSupported(false);
    }
}
