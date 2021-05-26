package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Range;

import androidx.annotation.RequiresApi;

import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AeTargetFpsDetector extends BaseParameter2Detector {

    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        Range[] aetargetfps = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        if (aetargetfps != null && aetargetfps.length>1)
        {
            String[] t = new String[aetargetfps.length];
            int min = 30,max = 0;
            for (int i = 0;i < aetargetfps.length; i++)
            {

                if ((int)aetargetfps[i].getLower() <= min && (int)aetargetfps[i].getUpper() > max)
                {
                    min = (int)aetargetfps[i].getLower();
                    max = (int)aetargetfps[i].getUpper();
                }

                t[i] = aetargetfps[i].getLower()+","+aetargetfps[i].getUpper();
            }
            settingsManager.get(SettingKeys.Ae_TargetFPS).setValues(t);
            settingsManager.get(SettingKeys.Ae_TargetFPS).setIsSupported(true);
            settingsManager.get(SettingKeys.Ae_TargetFPS).set(min+","+max);
        }
    }
}
