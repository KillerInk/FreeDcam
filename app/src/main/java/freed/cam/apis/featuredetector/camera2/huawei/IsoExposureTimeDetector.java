package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.ExposureTimeDetector;
import freed.cam.apis.featuredetector.camera2.IsoDetector;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;

public class IsoExposureTimeDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics characteristics) {
        if (Objects.equals(characteristics.get(CameraCharacteristicsHuawei.HUAWEI_PROFESSIONAL_MODE_SUPPORTED), Byte.valueOf((byte) 1)))
        {
            int[] shutterminmax = characteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENSOR_EXPOSURETIME_RANGE);

            int min = shutterminmax[0];
            int max = shutterminmax[1];
            long maxs = settingsManager.getCamera2MaxExposureTime();
            if (settingsManager.getCamera2MaxExposureTime() > 0)
                max = (int) settingsManager.getCamera2MaxExposureTime();
            if (settingsManager.getCamera2MinExposureTime() >0)
                min = (int) settingsManager.getCamera2MinExposureTime();
            ArrayList<String> tmp = ExposureTimeDetector.getShutterStrings(max,min,true);
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(tmp.size() > 0);
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(tmp.toArray(new String[tmp.size()]));

            int[] isominmax = characteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENSOR_ISO_RANGE);
            min = isominmax[0];
            max = isominmax[1];
            int maxiso = settingsManager.getCamera2MaxIso();
            if (maxiso > 0)
                max = settingsManager.getCamera2MaxIso();
            ArrayList<String> ar = IsoDetector.getIsoStrings(max, min);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setIsSupported(ar.size() > 0);
            settingsManager.get(SettingKeys.M_MANUAL_ISO).setValues(ar.toArray(new String[ar.size()]));

            settingsManager.get(SettingKeys.EXPOSURE_MODE).setIsSupported(false);
            settingsManager.setFramework(Frameworks.HuaweiCamera2Ex);
        }
    }
}
