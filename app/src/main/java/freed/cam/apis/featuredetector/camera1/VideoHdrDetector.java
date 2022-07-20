package freed.cam.apis.featuredetector.camera1;

import android.hardware.Camera;

import com.troop.freedcam.R;

import freed.settings.SettingKeys;

public class VideoHdrDetector  extends BaseParameter1Detector{
    @Override
    protected void findAndFillSettings(Camera.Parameters cameraCharacteristics) {
        detectVideoHdr(cameraCharacteristics);
    }

    private void detectVideoHdr(Camera.Parameters parameters)
    {
        if (parameters.get(camstring(R.string.video_hdr_values)) != null)
        {
            detectMode(parameters,R.string.video_hdr,R.string.video_hdr_values, settingsManager.get(SettingKeys.VIDEO_HDR));
        }
        else if (parameters.get(camstring(R.string.sony_video_hdr_values))!= null) {
            detectMode(parameters,R.string.sony_video_hdr,R.string.sony_video_hdr_values, settingsManager.get(SettingKeys.VIDEO_HDR));
        }
        else
            settingsManager.get(SettingKeys.VIDEO_HDR).setIsSupported(false);
    }
}
