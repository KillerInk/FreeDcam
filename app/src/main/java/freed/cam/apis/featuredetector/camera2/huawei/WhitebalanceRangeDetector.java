package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.FreedApplication;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class WhitebalanceRangeDetector extends BaseParameter2Detector {
    private final String TAG = WhitebalanceRangeDetector.class.getSimpleName();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int[] hdc = cameraCharacteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENSOR_WB_RANGE);
        if (hdc != null && hdc.length >0) {
            Log.d(TAG, Arrays.toString(hdc));
            settingsManager.get(SettingKeys.useHuaweiWhiteBalance).set(true);
            int min= hdc[0];
            int max = hdc[1];
            List<String> wblist = new ArrayList<>();
            wblist.add(FreedApplication.getStringFromRessources(R.string.auto_));
            for (int i = min; i <= max; i+=50)
            {
                wblist.add(i+"");
            }
            settingsManager.get(SettingKeys.M_Whitebalance).setValues(wblist.toArray(new String[wblist.size()]));
            settingsManager.get(SettingKeys.M_Whitebalance).set(0+"");
            settingsManager.get(SettingKeys.M_Whitebalance).setIsSupported(true);
        }
    }
}
