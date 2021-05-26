package freed.cam.apis.featuredetector.camera2;

import android.hardware.camera2.CameraCharacteristics;

import java.util.HashMap;

import freed.cam.apis.featuredetector.SupportedVideoProfilesDetector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.VideoMediaProfile;

public class VideoMediaProfilesDetector extends BaseParameter2Detector {
    @Override
    protected void findAndFillSettings(CameraCharacteristics cameraCharacteristics) {
        int camid = settingsManager.getCameraIds()[settingsManager.GetCurrentCamera()];
        detectVideoMediaProfiles(camid);
    }

    private void detectVideoMediaProfiles(int cameraid)
    {
        HashMap<String, VideoMediaProfile> supportedProfiles = new SupportedVideoProfilesDetector().getDefaultVideoMediaProfiles(cameraid);

        if (supportedProfiles.get("2160p") == null && has2160pSize()) {
            supportedProfiles.put("2160p", new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p Normal true"));
            supportedProfiles.put("2160p_Timelapse",new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p_TimeLapse Timelapse true"));
        }
        settingsManager.saveMediaProfiles(supportedProfiles);

        ////publishProgress("VideoMediaProfiles:" + getStringFromArray(supportedProfiles.keySet().toArray(new String[supportedProfiles.size()])));
    }

    private boolean has2160pSize()
    {
        String[] size = settingsManager.get(SettingKeys.PictureSize).getValues();
        for (String s: size) {
            if (s.matches("3840x2160"))
                return true;
        }
        return false;
    }
}
