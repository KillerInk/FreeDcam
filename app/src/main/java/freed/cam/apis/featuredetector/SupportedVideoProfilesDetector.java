package freed.cam.apis.featuredetector;

import android.media.CamcorderProfile;
import android.os.Build;

import com.lge.media.CamcorderProfileExRef;

import java.util.HashMap;

import freed.utils.Log;
import freed.utils.VideoMediaProfile;

public class SupportedVideoProfilesDetector {

    private final String TAG = SupportedVideoProfilesDetector.class.getSimpleName();

    public HashMap<String, VideoMediaProfile> getDefaultVideoMediaProfiles(int camera_id)
    {

        int CAMCORDER_QUALITY_2160p = 12;
        int CAMCORDER_QUALITY_2160pDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160p = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI = 1013;

        int CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P = 1016;
        int CAMCORDER_QUALITY_1080p_HFR = 16;
        int CAMCORDER_QUALITY_720p_HFR = 17;
        //g3 new with lolipop
        int QUALITY_HEVC1080P = 15;
        int QUALITY_HEVC2160pDCI = 17;
        int QUALITY_HEVC2160p = 16;
        int QUALITY_HEVC720P = 14;
        int QUALITY_HFR720P = 2003;
        int QUALITY_HIGH_SPEED_1080P = 2004;
        int QUALITY_HIGH_SPEED_480P = 2002;
        int QUALITY_HIGH_SPEED_720P = 2003;
        int QUALITY_HIGH_SPEED_HIGH = 2001;
        int QUALITY_2160pDCI = 13;
        int QUALITY_2160p = 8;

        HashMap<String, VideoMediaProfile> supportedProfiles = new HashMap<>();

        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_480P, "480p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_720P, "720p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_1080P, "1080p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfProfileSupported(camera_id, CAMCORDER_QUALITY_2160pDCI, "2160pDCI",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfProfileSupported(camera_id, CAMCORDER_QUALITY_2160p, "2160p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_480P, "Timelapse480p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_720P, "Timelapse720p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_1080P, "Timelapse1080p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfProfileSupported(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160p, "Timelapse2160p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfProfileSupported(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI, "Timelapse2160pDCI",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_1080P, "1080pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
            checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_2160P, "2160pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
            checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_480P, "480pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
            checkIfProfileSupported(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_720P, "720pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
        }


        return supportedProfiles;
    }

    private void checkIfProfileSupported(int camera_id, int camcorderProfile, String name ,boolean isAudioAcitve,VideoMediaProfile.VideoMode videoMode,HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        try {
            if (CamcorderProfile.hasProfile(camera_id, camcorderProfile)) {
                supportedProfiles.put(name, new VideoMediaProfile(CamcorderProfile.get(camera_id, camcorderProfile), name, videoMode, isAudioAcitve));
                Log.d(TAG, "found " +name);
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
    }

    public HashMap<String, VideoMediaProfile> getLGVideoMediaProfiles(int camera_id)
    {
        int CAMCORDER_QUALITY_2160p = 12;
        int CAMCORDER_QUALITY_2160pDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160p = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI = 1013;
        int CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P = 1016;
        int CAMCORDER_QUALITY_1080p_HFR = 16;
        int CAMCORDER_QUALITY_720p_HFR = 17;
        //g3 new with lolipop
        int QUALITY_HEVC1080P = 15;
        int QUALITY_HEVC2160pDCI = 17;
        int QUALITY_HEVC2160p = 16;
        int QUALITY_HEVC720P = 14;
        int QUALITY_HFR720P = 2003;
        int QUALITY_HIGH_SPEED_1080P = 2004;
        int QUALITY_HIGH_SPEED_480P = 2002;
        int QUALITY_HIGH_SPEED_720P = 2003;
        int QUALITY_HIGH_SPEED_HIGH = 2001;
        int QUALITY_2160pDCI = 13;
        int QUALITY_2160p = 8;

        HashMap<String, VideoMediaProfile> supportedProfiles = new HashMap<>();

        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_480P, "480p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_720P, "720p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_1080P, "1080p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CAMCORDER_QUALITY_2160pDCI, "2160pDCI",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CAMCORDER_QUALITY_2160p, "2160p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, QUALITY_2160p, "2160p",true,VideoMediaProfile.VideoMode.Normal, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CAMCORDER_QUALITY_720p_HFR, "720pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
        checkIfLGProfileSupported(camera_id, QUALITY_HFR720P, "720pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
        checkIfLGProfileSupported(camera_id, QUALITY_HIGH_SPEED_1080P, "1080pHFR",true,VideoMediaProfile.VideoMode.Highspeed, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_480P, "Timelapse480p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_720P, "Timelapse720p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);
        checkIfLGProfileSupported(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_1080P, "Timelapse1080p",false,VideoMediaProfile.VideoMode.Timelapse, supportedProfiles);


        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_1080P)) {
                VideoMediaProfile p108060fps = supportedProfiles.get("1080p").clone();
                p108060fps.videoFrameRate = 60;
                p108060fps.ProfileName = "1080p@60";
                supportedProfiles.put("1080p@60", p108060fps);
            }

        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        return supportedProfiles;
    }

    private void checkIfLGProfileSupported(int camera_id, int camcorderProfile, String name ,boolean isAudioAcitve,VideoMediaProfile.VideoMode videoMode,HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, camcorderProfile)) {
                supportedProfiles.put(name, new VideoMediaProfile(CamcorderProfile.get(camera_id, camcorderProfile), name, videoMode, isAudioAcitve));
                Log.d(TAG, "found " +name);
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
    }
}
