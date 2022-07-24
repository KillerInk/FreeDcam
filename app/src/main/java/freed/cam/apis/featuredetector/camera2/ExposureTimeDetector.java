package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.FreedApplication;
import freed.settings.SettingKeys;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ExposureTimeDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectManualexposureTime(cameraCharacteristics);
    }


    private void detectManualexposureTime(CameraCharacteristics characteristics)
    {
        long max = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper() / 1000;
        long min = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower() / 1000;

        if (settingsManager.getCamera2MaxExposureTime() >0)
            max = settingsManager.getCamera2MaxExposureTime();
        if (settingsManager.getCamera2MinExposureTime() >0)
            min = settingsManager.getCamera2MinExposureTime();
        ArrayList<String>  tmp = getShutterStrings(max, min,settingsManager.get(SettingKeys.USE_QCOM_AE).get());
        settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setIsSupported(tmp.size() > 0);
        if (tmp.size() > 0) {
            String[] ext = tmp.toArray(new String[tmp.size()]);
            settingsManager.get(SettingKeys.M_EXPOSURE_TIME).setValues(ext);
            
            if (settingsManager.get(SettingKeys.USE_QCOM_AE).get())
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).set("Auto");
            else
                settingsManager.get(SettingKeys.M_EXPOSURE_TIME).set("1/30");

            tmp = getShutterStrings(max, min,true);
            String[] ext2 = tmp.toArray(new String[tmp.size()]);
            settingsManager.get(SettingKeys.MIN_EXPOSURE).setValues(ext2);
            settingsManager.get(SettingKeys.MIN_EXPOSURE).set("auto");
            settingsManager.get(SettingKeys.MIN_EXPOSURE).setIsSupported(true);
            settingsManager.get(SettingKeys.MAX_EXPOSURE).setValues(ext2);
            settingsManager.get(SettingKeys.MAX_EXPOSURE).set("auto");
            settingsManager.get(SettingKeys.MAX_EXPOSURE).setIsSupported(true);
        }



    }

    public static ArrayList<String> getShutterStrings(long max, long min,boolean withAutoMode) {
        String[] allvalues = FreedApplication.getContext().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;

        ArrayList<String> tmp = new ArrayList<>();
        if (withAutoMode)
            tmp.add(FreedApplication.getStringFromRessources(R.string.auto_));
        for (int i = 1; i< allvalues.length; i++ )
        {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String[] split = s.split("/");
                a = Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f;
            }
            else
                a = Float.parseFloat(s)*1000000f;

            if (a>= min && a <= max)
                tmp.add(s);
            if (a >= min && !foundmin)
            {
                foundmin = true;
            }
            if (a > max && !foundmax)
            {
                foundmax = true;
            }
            if (foundmax && foundmin)
                break;
        }
        return tmp;
    }
}
