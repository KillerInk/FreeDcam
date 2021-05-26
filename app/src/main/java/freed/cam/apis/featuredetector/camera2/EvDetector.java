package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.settings.mode.SettingMode;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EvDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectManualExposure(cameraCharacteristics);
    }


    private void detectManualExposure(CameraCharacteristics characteristics)
    {
        SettingMode exposure = settingsManager.get(SettingKeys.M_ExposureCompensation);
        int max = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
        int min = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
        float step = characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();

        List<String> strings = new ArrayList<>();
        int t = 0;
        for (int i = min; i <= max; i++) {
            strings.add(String.format("%.1f", i * step));
        }
        if (strings.size() > 0) {
            exposure.setIsSupported(true);
            exposure.setValues(strings.toArray(new String[strings.size()]));
            exposure.set(strings.size()/2+"");
        }
        else
            exposure.setIsSupported(false);

    }
}
