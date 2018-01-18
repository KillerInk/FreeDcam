package freed.cam.apis.featuredetector;

import android.media.CamcorderProfile;

import com.lge.media.CamcorderProfileExRef;

import java.util.HashMap;

import freed.settings.SettingsManager;
import freed.settings.mode.SettingInterface;
import freed.settings.mode.SettingMode;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 23.01.2017.
 */

abstract class AbstractFeatureDetectorTask {

    private final  String TAG = AbstractFeatureDetectorTask.class.getSimpleName();
    private ProgressUpdate progressUpdateListner;
    AbstractFeatureDetectorTask(ProgressUpdate progressUpdate)
    {
        this.progressUpdateListner = progressUpdate;
    }

    public interface ProgressUpdate
    {
        void onProgessUpdate(String msg);
        void onTaskEnd(String msg);
    }

    void sendProgress(SettingInterface settingMode, String name)
    {
        if (settingMode instanceof SettingMode) {
            SettingMode ts = (SettingMode) settingMode;
            if (ts.isSupported()) {
                String[]ar = ts.getValues();
                String t = getStringFromArray(ar);
                publishProgress(name+" Values:" +t);
                publishProgress(name+":" + ts.get());
            }
            else
                publishProgress(name+" not supported");
        }

    }

    public abstract void detect();

    void publishProgress(String value) {
        if (progressUpdateListner != null)
            progressUpdateListner.onProgessUpdate(value);
    }

    public void onPostExecute(String s) {
        if (progressUpdateListner != null)
            progressUpdateListner.onTaskEnd(s);
    }

    String getStringFromArray(String[] arr)
    {
        String t = "";
        for (int i =0; i<arr.length;i++)
            t+=arr[i]+ SettingsManager.SPLITTCHAR;
        return t;
    }

    HashMap<String, VideoMediaProfile> getDefaultVideoMediaProfiles(int camera_id)
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

        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_480P)) {
                supportedProfiles.put("480p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_480P), "480p", VideoMediaProfile.VideoMode.Normal, true));
                Log.d(TAG,"found 480p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_720P), "720p", VideoMediaProfile.VideoMode.Normal,true));
                Log.d(TAG, "found 720p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_1080P)) {
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_1080P), "1080p", VideoMediaProfile.VideoMode.Normal, true));
                Log.d(TAG,"found 1080p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_480P)) {
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse, false));
                Log.d(TAG, "found Timnelapse480p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_720P)) {
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMediaProfile.VideoMode.Timelapse, false));
                Log.d(TAG, "found Timelapse720p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_1080P)) {
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_TIME_LAPSE_1080P), "Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse, false));
                Log.d(TAG, "found Timelapse1080p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }

        try {
            if (CamcorderProfile.hasProfile(camera_id, CAMCORDER_QUALITY_2160pDCI))
            {

                CamcorderProfile fourk = CamcorderProfile.get(camera_id, CAMCORDER_QUALITY_2160pDCI);

                supportedProfiles.put("2160pDCI",new VideoMediaProfile(fourk, "2160pDCI", VideoMediaProfile.VideoMode.Normal,true));
                Log.d(TAG, "found 2160pDCI");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CAMCORDER_QUALITY_2160p))
            {
                CamcorderProfile fourk = CamcorderProfile.get(camera_id, CAMCORDER_QUALITY_2160p);

                supportedProfiles.put("2160p",new VideoMediaProfile(fourk, "2160p", VideoMediaProfile.VideoMode.Normal,true));
                Log.d(TAG, "found 2160p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160p)) {
                supportedProfiles.put("2160p_TimeLapse", new VideoMediaProfile(CamcorderProfile.get(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160p), "Timelapse2160p", VideoMediaProfile.VideoMode.Timelapse, false));
                Log.d(TAG, "found Timelapse2160p");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI)) {
                supportedProfiles.put("2160p_DCI_TimeLapse", new VideoMediaProfile(CamcorderProfile.get(camera_id, CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI), "Timelapse2160pDCI", VideoMediaProfile.VideoMode.Timelapse, false));
                Log.d(TAG, "found Timelapse2160pDCI");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
            {
                supportedProfiles.put("1080pHFR", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_1080P), "1080pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Log.d(TAG, "found 1080pHFR");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_2160P)) {
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_2160P), "2016HFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Log.d(TAG, "found 2016pHFR");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_720P)) {
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_720P), "720pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Log.d(TAG, "found 720pHFR");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfile.hasProfile(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_480P)) {
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(camera_id, CamcorderProfile.QUALITY_HIGH_SPEED_480P), "480pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Log.d(TAG, "found 480pHFR");
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        return supportedProfiles;
    }

    HashMap<String, VideoMediaProfile> getLGVideoMediaProfiles(int camera_id)
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

        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_480P))
                supportedProfiles.put("480p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_480P, "480p", VideoMediaProfile.VideoMode.Normal,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_720P))
                supportedProfiles.put("720p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_720P,"720p", VideoMediaProfile.VideoMode.Normal,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_1080P)) {
                supportedProfiles.put("1080p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_1080P, "1080p", VideoMediaProfile.VideoMode.Normal,true));
                VideoMediaProfile p108060fps = supportedProfiles.get("1080p").clone();
                p108060fps.videoFrameRate = 60;
                p108060fps.ProfileName = "1080p@60";
                supportedProfiles.put("1080p@60", p108060fps);
            }

        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_480P, "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_720P,"Timelapse720p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", CamcorderProfileExRef.getProfile(camera_id, CamcorderProfileExRef.QUALITY_TIME_LAPSE_1080P,"Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CAMCORDER_QUALITY_2160pDCI))
                supportedProfiles.put("2160pDCI", CamcorderProfileExRef.getProfile(camera_id, CAMCORDER_QUALITY_2160pDCI,"2160pDCI", VideoMediaProfile.VideoMode.Normal,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CAMCORDER_QUALITY_2160p))
            {
                supportedProfiles.put("2160p", CamcorderProfileExRef.getProfile(camera_id, CAMCORDER_QUALITY_2160p,"2160p", VideoMediaProfile.VideoMode.Normal,true));
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, QUALITY_2160p))
            {
                supportedProfiles.put("2160p", CamcorderProfileExRef.getProfile(camera_id, QUALITY_2160p,"2160p", VideoMediaProfile.VideoMode.Normal,true));
            }
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, CAMCORDER_QUALITY_720p_HFR))
                supportedProfiles.put("720pHFR", CamcorderProfileExRef.getProfile(camera_id, CAMCORDER_QUALITY_720p_HFR,"720pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, QUALITY_HFR720P))
                supportedProfiles.put("720pHFR", CamcorderProfileExRef.getProfile(camera_id, QUALITY_HFR720P,"720pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }

        try {
            if (CamcorderProfileExRef.hasProfile(camera_id, QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR", CamcorderProfileExRef.getProfile(camera_id, QUALITY_HIGH_SPEED_1080P, "1080pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception ex) {
            Log.WriteEx(ex);
        }
        return supportedProfiles;
    }
}
