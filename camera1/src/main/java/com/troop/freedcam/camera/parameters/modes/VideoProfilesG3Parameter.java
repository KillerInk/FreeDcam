package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.lge.media.CamcorderProfileEx;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.VideoMediaProfile;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoProfilesG3Parameter extends BaseModeParameter
{

    HashMap<String, VideoMediaProfile> supportedProfiles;
    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;
    String profile = "HIGH";

    public VideoProfilesG3Parameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper) {
        super(handler,parameters, parameterChanged, value, values);
        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;
        this.isSupported =true;
        loadProfiles();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
        BackgroundValueHasChanged(valueToSet);
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null && cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
            cameraUiWrapper.moduleHandler.GetCurrentModule().LoadNeededParameters();

    }

    @Override
    public boolean IsSupported() {
        return this.isSupported;
    }

    @Override
    public String GetValue() {
        return profile;
    }

    @Override
    public String[] GetValues()
    {
        List<String> keys = new ArrayList<String>(supportedProfiles.keySet());
        Collections.sort(keys);
        return keys.toArray(new String[keys.size()]);
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

    private void loadProfiles()
    {

        if (supportedProfiles == null)
        {

            String current;

            supportedProfiles = new HashMap<String, VideoMediaProfile>();

            File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
            if (!f.exists())
            {
                lookupDefaultProfiles(supportedProfiles);
                VideoMediaProfile.saveCustomProfiles(supportedProfiles);
            }
             VideoMediaProfile.loadCustomProfiles(supportedProfiles);
        }
    }

    private void lookupDefaultProfiles(HashMap<String, VideoMediaProfile> supportedProfiles)
    {

        int CAMCORDER_QUALITY_4kUHD = 12;
        int CAMCORDER_QUALITY_4kDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kUHD = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kDCI = 1013;
        int CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P = 1016;
        int CAMCORDER_QUALITY_1080p_HFR = 16;
        int CAMCORDER_QUALITY_720p_HFR = 17;
        //g3 new with lolipop
        int QUALITY_HEVC1080P = 15;
        int QUALITY_HEVC4kDCI = 17;
        int QUALITY_HEVC4kUHD = 16;
        int QUALITY_HEVC720P = 14;
        int QUALITY_HFR720P = 2003;
        int QUALITY_HIGH_SPEED_1080P = 2004;
        int QUALITY_HIGH_SPEED_480P = 2002;
        int QUALITY_HIGH_SPEED_720P = 2003;
        int QUALITY_HIGH_SPEED_HIGH = 2001;
        int QUALITY_4kDCI = 13;
        int QUALITY_4kUHD = 8;
        /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_LOW))
                    supportedProfiles.put("LOW", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HIGH))
                    supportedProfiles.put("HIGH", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QCIF))
                    supportedProfiles.put("QCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_CIF))
                    supportedProfiles.put("CIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_480P))
                supportedProfiles.put("480p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_480P), "480p", VideoMediaProfile.VideoMode.Normal));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_720P))
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_720P),"720p", VideoMediaProfile.VideoMode.Normal));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_1080P)) {
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_1080P), "1080p", VideoMediaProfile.VideoMode.Normal));
                VideoMediaProfile p108060fps = supportedProfiles.get("1080p").clone();
                p108060fps.videoFrameRate = 60;
                p108060fps.ProfileName = "1080p@60";
                supportedProfiles.put("1080p@60", p108060fps);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QVGA))
                    supportedProfiles.put("QVGA", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
/*
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_LOW))
                    supportedProfiles.put("TimelapseLOW", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_HIGH))
                    supportedProfiles.put("TimelapseHIGH", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QCIF))
                    supportedProfiles.put("TimelapseQCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_CIF))
                    supportedProfiles.put("TimelapseCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_720P),"Timelapse720p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
/*            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QVGA))
                    supportedProfiles.put("TimelapseQVGA", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI))
                supportedProfiles.put("4kDCI", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI),"4kDCI", VideoMediaProfile.VideoMode.Normal));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfileEx fourk = CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);
                supportedProfiles.put("4kUHD", new VideoMediaProfile(fourk,"4kUHD", VideoMediaProfile.VideoMode.Normal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_4kUHD))
            {
                CamcorderProfileEx fourk = CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_4kUHD);
                supportedProfiles.put("4kUHD", new VideoMediaProfile(fourk,"4kUHD", VideoMediaProfile.VideoMode.Normal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI))
                    supportedProfiles.put("Timelapse4kDCI", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                    supportedProfiles.put("Timelapse4kUHD", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HFR1080P))
                    supportedProfiles.put("1080pHFR", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_1080p_HFR));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_720p_HFR))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_720p_HFR),"720pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_HFR720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_HFR720P),"720pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P))
                    supportedProfiles.put("TimelapseHfr1080p", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR", new VideoMediaProfile(CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_HIGH_SPEED_1080P), "1080pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
