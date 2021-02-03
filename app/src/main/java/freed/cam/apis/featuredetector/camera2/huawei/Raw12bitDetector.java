package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.cam.apis.featuredetector.BaseParameterDetector;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class Raw12bitDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int[] raw12 = cameraCharacteristics.get(CameraCharacteristicsHuawei.HUAWEI_PROFESSIONAL_RAW12_SUPPORTED);
        if (raw12!= null)
            SettingsManager.get(SettingKeys.support12bitRaw).set(true);
    }
}
