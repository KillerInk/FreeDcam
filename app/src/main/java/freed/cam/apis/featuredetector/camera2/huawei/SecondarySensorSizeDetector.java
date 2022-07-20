package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class SecondarySensorSizeDetector extends BaseParameter2Detector {
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
                settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).setValues(ls.toArray(new String[ls.size()]));
                settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).setIsSupported(true);
                settingsManager.get(SettingKeys.SECONDARY_SENSOR_SIZE).set(ls.get(0));
                Log.d(TAG, "HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE " + ls);
            } else
                Log.d(TAG, "HUAWEI_SENCONDARY_SENSOR_SUPPORTED_SIZE not supported ");
        }
    }
}
