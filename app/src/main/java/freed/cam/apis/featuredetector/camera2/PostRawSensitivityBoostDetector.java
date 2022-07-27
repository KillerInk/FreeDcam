package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Range;

import freed.settings.SettingKeys;

public class PostRawSensitivityBoostDetector extends BaseParameter2Detector{

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Range<Integer> prsb = cameraCharacteristics.get(CameraCharacteristics.CONTROL_POST_RAW_SENSITIVITY_BOOST_RANGE);
            if (prsb == null || (prsb.getUpper() == 100 && prsb.getLower() == 100))
                settingsManager.get(SettingKeys.SUPPORT_POST_RAW_SENSITIVITY_BOOST).set(false);
            else
                settingsManager.get(SettingKeys.SUPPORT_POST_RAW_SENSITIVITY_BOOST).set(true);
        }
        else
            settingsManager.get(SettingKeys.SUPPORT_POST_RAW_SENSITIVITY_BOOST).set(false);
    }
}
