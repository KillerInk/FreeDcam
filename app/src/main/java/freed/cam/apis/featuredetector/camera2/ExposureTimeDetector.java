package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

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

        if (SettingsManager.getInstance().getCamera2MaxExposureTime() >0)
            max = SettingsManager.getInstance().getCamera2MaxExposureTime();
        if (SettingsManager.getInstance().getCamera2MinExposureTime() >0)
            min = SettingsManager.getInstance().getCamera2MinExposureTime();

        ArrayList<String> tmp = getShutterStrings(max, min,false);
        SettingsManager.get(SettingKeys.M_ExposureTime).setIsSupported(tmp.size() > 0);
        if (tmp.size() > 0)
            SettingsManager.get(SettingKeys.M_ExposureTime).setValues(tmp.toArray(new String[tmp.size()]));

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
