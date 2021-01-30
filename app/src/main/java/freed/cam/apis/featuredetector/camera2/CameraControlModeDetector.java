package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.HashMap;

import freed.FreedApplication;
import freed.cam.apis.featuredetector.Camera2Util;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.StringUtils;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CameraControlModeDetector  extends BaseParameterDetector{
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        detectControlMode(cameraCharacteristics);
    }


    private void detectControlMode(CameraCharacteristics characteristics) {
        if (SettingsManager.getInstance().hasCamera2Features()) {
            //flash mode
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                Camera2Util.detectIntMode(characteristics,CameraCharacteristics.CONTROL_AVAILABLE_MODES, SettingsManager.get(SettingKeys.CONTROL_MODE), FreedApplication.getStringArrayFromRessource(R.array.controlModes));
                return;
            }
            else {
                int device = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                String[] lookupar = FreedApplication.getContext().getResources().getStringArray(R.array.controlModes);
                int[] full = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES) != null)
                    full = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_MODES);
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL || device==CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 || device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
                    full = new int[] {0,1,2,};
                }
                else if (device == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    full = new int[] {1,2,};
                SettingsManager.get(SettingKeys.CONTROL_MODE).setIsSupported(true);
                if (SettingsManager.get(SettingKeys.CONTROL_MODE).isSupported()) {
                    HashMap<String, Integer> map = new HashMap<>();
                    for (int i = 0; i < full.length; i++) {
                        map.put(lookupar[i], full[i]);
                    }
                    lookupar = StringUtils.IntHashmapToStringArray(map);
                    SettingsManager.get(SettingKeys.CONTROL_MODE).setValues(lookupar);
                    SettingsManager.get(SettingKeys.CONTROL_MODE).set(FreedApplication.getStringFromRessources(R.string.auto));
                }
            }
        }
    }
}
