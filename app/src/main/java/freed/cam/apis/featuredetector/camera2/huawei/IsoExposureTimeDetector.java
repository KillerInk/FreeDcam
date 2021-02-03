package freed.cam.apis.featuredetector.camera2.huawei;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Objects;

import camera2_hidden_keys.huawei.CameraCharacteristicsHuawei;
import freed.cam.apis.featuredetector.BaseParameterDetector;
import freed.cam.apis.featuredetector.camera2.BaseParameter2Detector;
import freed.cam.apis.featuredetector.camera2.ExposureTimeDetector;
import freed.cam.apis.featuredetector.camera2.IsoDetector;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

public class IsoExposureTimeDetector extends BaseParameter2Detector {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void findAndFillSettings(CameraCharacteristics characteristics) {
        if (Objects.equals(characteristics.get(CameraCharacteristicsHuawei.HUAWEI_PROFESSIONAL_MODE_SUPPORTED), Byte.valueOf((byte) 1)))
        {
            int[] shutterminmax = characteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENSOR_EXPOSURETIME_RANGE);

            int min = shutterminmax[0];
            int max = shutterminmax[1];
            long maxs = SettingsManager.getInstance().getCamera2MaxExposureTime();
            if (SettingsManager.getInstance().getCamera2MaxExposureTime() > 0)
                max = (int) SettingsManager.getInstance().getCamera2MaxExposureTime();
            if (SettingsManager.getInstance().getCamera2MinExposureTime() >0)
                min = (int) SettingsManager.getInstance().getCamera2MinExposureTime();
            ArrayList<String> tmp = ExposureTimeDetector.getShutterStrings(max,min,true);
            SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(tmp.size() > 0);
            SettingsManager.get(SettingKeys.M_ExposureTime).setValues(tmp.toArray(new String[tmp.size()]));

            int[] isominmax = characteristics.get(CameraCharacteristicsHuawei.HUAWEI_SENSOR_ISO_RANGE);
            min = isominmax[0];
            max = isominmax[1];
            int maxiso = SettingsManager.getInstance().getCamera2MaxIso();
            if (maxiso > 0)
                max = SettingsManager.getInstance().getCamera2MaxIso();
            ArrayList<String> ar = IsoDetector.getIsoStrings(max, min);
            SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(ar.size() > 0);
            SettingsManager.get(SettingKeys.M_ManualIso).setValues(ar.toArray(new String[ar.size()]));

            SettingsManager.get(SettingKeys.ExposureMode).setIsSupported(false);
            SettingsManager.getInstance().setFramework(Frameworks.HuaweiCamera2Ex);
        }
    }
}
