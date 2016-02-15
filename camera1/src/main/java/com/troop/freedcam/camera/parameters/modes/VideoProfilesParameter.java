package com.troop.freedcam.camera.parameters.modes;

import android.media.CamcorderProfile;
import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.camera.modules.VideoMediaProfile;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoProfilesParameter extends BaseModeParameter
{

    HashMap<String, VideoMediaProfile> supportedProfiles;
    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;
    String profile = "HIGH";

    public VideoProfilesParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper) {
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
        /* try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_LOW))
                    supportedProfiles.put("LOW", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH))
                    supportedProfiles.put("HIGH", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QCIF))
                    supportedProfiles.put("QCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_CIF))
                    supportedProfiles.put("CIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P))
                supportedProfiles.put("480p",new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P),"480p", VideoMediaProfile.VideoMode.Normal));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMediaProfile.VideoMode.Normal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P))
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMediaProfile.VideoMode.Normal));
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QVGA))
                    supportedProfiles.put("QVGA", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_LOW))
                    supportedProfiles.put("TimelapseLOW", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_HIGH))
                    supportedProfiles.put("TimelapseHIGH", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QCIF))
                    supportedProfiles.put("TimelapseQCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_CIF))
                    supportedProfiles.put("TimelapseCIF", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QVGA))
                    supportedProfiles.put("TimelapseQVGA", CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI))
                    supportedProfiles.put("4kDCI", CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);

                supportedProfiles.put("4kUHD",new VideoMediaProfile(fourk, "4kUHD", VideoMediaProfile.VideoMode.Normal));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
            /*try {
                if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI))
                    supportedProfiles.put("Timelapse4kDCI", CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI));
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                supportedProfiles.put("Timelapse4kUHD", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD),"Timelapse4kUHD", VideoMediaProfile.VideoMode.Timelapse));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR",new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P),"1080pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P))
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P),"2016HFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P),"720pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P))
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P),"480pHFR", VideoMediaProfile.VideoMode.Highspeed));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DeviceUtils.IS(DeviceUtils.Devices.LenovoK920) || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            VideoMediaProfile t = supportedProfiles.get("720p").clone();
            t.videoFrameRate = 120;
            t.Mode = VideoMediaProfile.VideoMode.Highspeed;
            t.ProfileName = "720pHFR";
            supportedProfiles.put("720pHFR",t);

            VideoMediaProfile uhd = supportedProfiles.get("1080p").clone();
            uhd.videoFrameWidth = 3840;
            uhd.videoFrameHeight = 2160;
            uhd.Mode = VideoMediaProfile.VideoMode.Normal;
            uhd.ProfileName = "4kUHD";
            supportedProfiles.put("4kUHD",uhd);
        }
    }
}
