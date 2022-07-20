package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;
import android.text.TextUtils;

import freed.settings.Frameworks;
import freed.settings.SettingKeys;
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
                        settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setValues("off,60,120".split(","));
                        settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setCamera1ParameterKEY("video-hfr");
                        settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setIsSupported(true);
                        settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).set(parameters.get("video-hfr"));
                    }
                    catch(ArrayIndexOutOfBoundsException ex)
                    {
                        Log.WriteEx(ex);
                        settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setIsSupported(false);
                    }
                }
                else
                    settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setIsSupported(false);
            }
        }
        else if (settingsManager.getFrameWork() == Frameworks.MTK)
        {
            if (parameters.get("hsvr-prv-fps-values") != null)
            {
                settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setValues(parameters.get("hsvr-prv-fps-values").split(","));
                settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setCamera1ParameterKEY("hsvr-prv-fps");
                settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setIsSupported(true);
                settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).set(parameters.get("hsvr-prv-fps"));
            }
            else
                settingsManager.get(SettingKeys.VIDEO_HIGH_FRAMERATE).setIsSupported(false);
        }
    }
}
