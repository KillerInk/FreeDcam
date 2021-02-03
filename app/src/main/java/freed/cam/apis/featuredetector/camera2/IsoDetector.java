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
public class IsoDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectManualIso(cameraCharacteristics);
    }


    private void detectManualIso(CameraCharacteristics characteristics)
    {
        int max  = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
        if (SettingsManager.getInstance().getCamera2MaxIso() >0)
            max = SettingsManager.getInstance().getCamera2MaxIso();

        int min = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower();
        ArrayList<String> ar = getIsoStrings(max, min);
        SettingsManager.get(SettingKeys.M_ManualIso).setIsSupported(ar.size() > 0);
        if (ar.size() > 0)
            SettingsManager.get(SettingKeys.M_ManualIso).setValues(ar.toArray(new String[ar.size()]));
    }

    public static ArrayList<String> getIsoStrings(int max, int min) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(FreedApplication.getStringFromRessources(R.string.auto_));
        for (int i = min; i <= max; i += 50) {
            //double isostep when its bigger then 3200
            if(i > 3200)
            {
                int next = (i-50) *2;
                if (next > max)
                    next = max;
                i =next;
            }
            ar.add(i + "");
        }
        return ar;
    }
}
