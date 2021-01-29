package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import camera2_hidden_keys.xiaomi.CameraCharacteristicsXiaomi;
import freed.FreedApplication;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class OisDetector extends BaseParameterDetector {

    private final String TAG = OisDetector.class.getSimpleName();

    @Override
    protected void findAndFillSettings(CameraCharacteristics characteristics) {
        int[] oisvalues = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        boolean ois_supported= false;
        if (oisvalues.length > 1)
        {
            if (oisvalues[0] == 1)
                ois_supported =true;
            if (oisvalues[1] == 1)
                ois_supported = true;
        }
        else if (oisvalues.length == 1)
            if (oisvalues[0] == 1)
                ois_supported = true;

        if (!ois_supported)
        {
            try{
                byte ois = characteristics.get(CameraCharacteristicsXiaomi.teleois_supported);
                if (ois == (byte)1)
                    ois_supported = true;
            }
            catch (IllegalArgumentException | NullPointerException ex)
            {
                Log.d(TAG, "No Xiaomi ois");
            }
        }
        if (ois_supported)
        {
            String values[] = new String[2];
            values[0] = FreedApplication.getStringFromRessources(R.string.off) + ",0";
            values[1] = FreedApplication.getStringFromRessources(R.string.on) + ",1";
            SettingsManager.get(SettingKeys.OIS_MODE).setValues(values);
            SettingsManager.get(SettingKeys.OIS_MODE).setIsSupported(true);
            SettingsManager.get(SettingKeys.OIS_MODE).set(FreedApplication.getStringFromRessources(R.string.on));
        }
        else
            SettingsManager.get(SettingKeys.OIS_MODE).setIsSupported(false);
    }
}
