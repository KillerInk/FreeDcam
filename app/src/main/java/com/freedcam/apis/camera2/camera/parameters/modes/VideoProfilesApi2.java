package com.freedcam.apis.camera2.camera.parameters.modes;

import android.media.CamcorderProfile;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.CameraUiWrapperApi2;
import com.freedcam.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 24.02.2016.
 */
public class VideoProfilesApi2 extends BaseModeApi2
{
    final String TAG = VideoProfilesApi2.class.getSimpleName();
    private HashMap<String, VideoMediaProfile> supportedProfiles;
    private String profile;
    private CameraUiWrapperApi2 cameraUiWrapperApi2;

    public VideoProfilesApi2(Handler handler, CameraHolderApi2 cameraHolderApi2, CameraUiWrapperApi2 cameraUiWrapperApi2)
    {
        super(handler, cameraHolderApi2);
        loadProfiles();
        this.isSupported = true;
        this.cameraUiWrapperApi2 = cameraUiWrapperApi2;
    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }

    @Override
    public String GetValue()
    {
        if (profile == null && supportedProfiles != null)
        {
            List<String> keys = new ArrayList<>(supportedProfiles.keySet());
            profile = keys.get(0);
        }
        return profile;
    }

    @Override
    public String[] GetValues()
    {
        List<String> keys = new ArrayList<>(supportedProfiles.keySet());
        Collections.sort(keys);
        return keys.toArray(new String[keys.size()]);
    }

    private void loadProfiles()
    {
        if (supportedProfiles == null)
        {

            String current;

            supportedProfiles = new HashMap<>();
            File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
            if (!f.exists())
            {
                lookupDefaultProfiles(supportedProfiles);
                VideoMediaProfile.saveCustomProfiles(supportedProfiles);
            }

            try {
                VideoMediaProfile.loadCustomProfiles(supportedProfiles);
            } catch (IOException e) {
                Logger.exception(e);
            }

        }
    }
    private void lookupDefaultProfiles(HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        int CAMCORDER_QUALITY_4kUHD = 12;
        int CAMCORDER_QUALITY_4kDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kUHD = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kDCI = 1013;
        /* try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_LOW))
                    supportedProfiles.put("LOW", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_LOW));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH))
                    supportedProfiles.put("HIGH", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QCIF))
                    supportedProfiles.put("QCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QCIF));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_CIF))
                    supportedProfiles.put("CIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_CIF));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P))
                supportedProfiles.put("480p",new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P),"480p", VideoMediaProfile.VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMediaProfile.VideoMode.Normal,true));
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P))
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMediaProfile.VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QVGA))
                    supportedProfiles.put("QVGA", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QVGA));
            } catch (Exception e) {
                Logger.exception(e);
            }

            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_LOW))
                    supportedProfiles.put("TimelapseLOW", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_LOW));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_HIGH))
                    supportedProfiles.put("TimelapseHIGH", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QCIF))
                    supportedProfiles.put("TimelapseQCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QCIF));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_CIF))
                    supportedProfiles.put("TimelapseCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_CIF));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QVGA))
                    supportedProfiles.put("TimelapseQVGA", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QVGA));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI))
                    supportedProfiles.put("4kDCI", CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);

                supportedProfiles.put("4kUHD",new VideoMediaProfile(fourk, "4kUHD", VideoMediaProfile.VideoMode.Normal,true));
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI))
                    supportedProfiles.put("Timelapse4kDCI", CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                supportedProfiles.put("Timelapse4kUHD", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD),"Timelapse4kUHD", VideoMediaProfile.VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR",new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P),"1080pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P))
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P),"2016HFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P),"720pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P))
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P),"480pHFR", VideoMediaProfile.VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
    }

    public VideoMediaProfile GetCameraProfile(String profile)
    {
        if (profile == null || profile.equals(""))
        {
            String t[] = supportedProfiles.keySet().toArray(new String[supportedProfiles.keySet().size()]);
            return supportedProfiles.get(t[0]);
        }
        return supportedProfiles.get(profile);
    }
    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
        if (cameraUiWrapperApi2.moduleHandler.GetCurrentModule() != null && cameraUiWrapperApi2.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
        {
            cameraUiWrapperApi2.moduleHandler.GetCurrentModule().DestroyModule();
            cameraUiWrapperApi2.moduleHandler.GetCurrentModule().InitModule();
        }

    }


}
