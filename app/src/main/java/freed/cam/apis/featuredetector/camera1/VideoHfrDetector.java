package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class VideoHfrDetector extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectVideoHFR(cameraCharacteristics);
    }

    private void detectVideoHFR(Camera.Parameters parameters)
    {
        if (parameters.get("video-hfr") != null)
        {
            String hfrvals = parameters.get("video-hfr-values");
            if (!hfrvals.equals("off"))
            {
                if (TextUtils.isEmpty(hfrvals)) {
                    try {
                        SettingsManager.get(SettingKeys.VideoHighFramerate).setValues("off,60,120".split(","));
                        SettingsManager.get(SettingKeys.VideoHighFramerate).setCamera1ParameterKEY("video-hfr");
                        SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(true);
                        SettingsManager.get(SettingKeys.VideoHighFramerate).set(parameters.get("video-hfr"));
                    }
                    catch(ArrayIndexOutOfBoundsException ex)
                    {
                        Log.WriteEx(ex);
                        SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(false);
                    }
                }
                else
                    SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(false);
            }
        }
        else if (SettingsManager.getInstance().getFrameWork() == Frameworks.MTK)
        {
            if (parameters.get("hsvr-prv-fps-values") != null)
            {
                SettingsManager.get(SettingKeys.VideoHighFramerate).setValues(parameters.get("hsvr-prv-fps-values").split(","));
                SettingsManager.get(SettingKeys.VideoHighFramerate).setCamera1ParameterKEY("hsvr-prv-fps");
                SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(true);
                SettingsManager.get(SettingKeys.VideoHighFramerate).set(parameters.get("hsvr-prv-fps"));
            }
            else
                SettingsManager.get(SettingKeys.VideoHighFramerate).setIsSupported(false);
        }
    }
}
