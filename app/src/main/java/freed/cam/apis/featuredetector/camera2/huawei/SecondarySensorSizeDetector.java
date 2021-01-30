package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.cam.apis.featuredetector.camera2.BaseParameterDetector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class SecondarySensorSizeDetector extends BaseParameterDetector {
    private final String TAG = SecondarySensorSizeDetector.class.getSimpleName();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int[] hdc = cameraCharacteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE);
        if (hdc != null && hdc.length > 0) {
            Log.d(TAG, Arrays.toString(hdc));
            List<String> ls = new ArrayList<>();
            for (int i = 0; i < hdc.length; i += 2) {
                String t = hdc[i] + "x" + hdc[i + 1];
                ls.add(t);
            }
            if (ls.size() > 0) {
                SettingsManager.get(SettingKeys.secondarySensorSize).setValues(ls.toArray(new String[ls.size()]));
                SettingsManager.get(SettingKeys.secondarySensorSize).setIsSupported(true);
                SettingsManager.get(SettingKeys.secondarySensorSize).set(ls.get(0));
                Log.d(TAG, "HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE " + ls.toString());
            } else
                Log.d(TAG, "HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE not supported ");
        }
    }
}
