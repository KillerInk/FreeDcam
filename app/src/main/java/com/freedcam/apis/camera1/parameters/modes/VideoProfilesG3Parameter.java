/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.modules.VideoMediaProfileLG;
import com.freedcam.utils.Logger;
import com.lge.media.CamcorderProfileEx;

import java.io.File;
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
    final String TAG = VideoProfilesG3Parameter.class.getSimpleName();
    private HashMap<String, VideoMediaProfile> supportedProfiles;
    private String profile;
    private CameraHolder cameraHolder;

    public VideoProfilesG3Parameter(Parameters parameters,I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, cameraUiWrapper, "", "");
        cameraHolder = (CameraHolder) cameraUiWrapper.GetCameraHolder();
        isSupported =true;
        loadProfiles();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
        BackgroundValueHasChanged(valueToSet);
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null && cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
            cameraUiWrapper.GetModuleHandler().GetCurrentModule().InitModule();

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
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

    public VideoMediaProfile GetCameraProfile(String profile)
    {
        if (profile == null || profile.equals(""))
        {
            String[] t = supportedProfiles.keySet().toArray(new String[supportedProfiles.keySet().size()]);
            return supportedProfiles.get(t[0]);
        }
        return supportedProfiles.get(profile);
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
            if (f.exists())
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
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HIGH))
                    supportedProfiles.put("HIGH", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HIGH));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QCIF))
                    supportedProfiles.put("QCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QCIF));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_CIF))
                    supportedProfiles.put("CIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_CIF));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_480P))
                supportedProfiles.put("480p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_480P), "480p", VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_720P))
                supportedProfiles.put("720p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_720P),"720p", VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_1080P)) {
                supportedProfiles.put("1080p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_1080P), "1080p", VideoMode.Normal,true));
                VideoMediaProfile p108060fps = supportedProfiles.get("1080p").clone();
                p108060fps.videoFrameRate = 60;
                p108060fps.ProfileName = "1080p@60";
                supportedProfiles.put("1080p@60", p108060fps);
            }

        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QVGA))
                    supportedProfiles.put("QVGA", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_QVGA));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
/*
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_LOW))
                    supportedProfiles.put("TimelapseLOW", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_LOW));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_HIGH))
                    supportedProfiles.put("TimelapseHIGH", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_HIGH));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QCIF))
                    supportedProfiles.put("TimelapseQCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QCIF));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_CIF))
                    supportedProfiles.put("TimelapseCIF", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_CIF));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_720P),"Timelapse720p", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
/*            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QVGA))
                    supportedProfiles.put("TimelapseQVGA", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_TIME_LAPSE_QVGA));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI))
                supportedProfiles.put("4kDCI", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI),"4kDCI", VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfileEx fourk = CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);
                supportedProfiles.put("4kUHD", new VideoMediaProfileLG(fourk,"4kUHD", VideoMode.Normal,true));

             /*   if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV) || DeviceUtils.IS(DeviceUtils.Devices.ZTEADVIMX214) || DeviceUtils.IS(DeviceUtils.Devices.ZTEADV234))
                {

                    VideoMediaProfile uhd = supportedProfiles.get("Timelapse1080p").clone();
                    uhd.videoFrameWidth = 3840;
                    uhd.videoFrameHeight = 2160;
                    uhd.Mode = VideoMediaProfile.VideoMode.Timelapse;
                    uhd.ProfileName = "Time Lapse 4K";
                    supportedProfiles.put("Time Lapse 4K",uhd);
                }*/
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_4kUHD))
            {
                CamcorderProfileEx fourk = CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_4kUHD);
                supportedProfiles.put("4kUHD", new VideoMediaProfileLG(fourk,"4kUHD", VideoMode.Normal,true));
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI))
                    supportedProfiles.put("Timelapse4kDCI", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                    supportedProfiles.put("Timelapse4kUHD", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD));
            } catch (Exception e) {
                Logger.exception(e);
            }
            try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CamcorderProfileEx.QUALITY_HFR1080P))
                    supportedProfiles.put("1080pHFR", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_1080p_HFR));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_720p_HFR))
                supportedProfiles.put("720pHFR", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_720p_HFR),"720pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_HFR720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_HFR720P),"720pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
            /*try {
                if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P))
                    supportedProfiles.put("TimelapseHfr1080p", CamcorderProfileEx.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_HFR1080P));
            } catch (Exception e) {
                Logger.exception(e);
            }*/
        try {
            if (CamcorderProfileEx.hasProfile(cameraHolder.CurrentCamera, QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR", new VideoMediaProfileLG(CamcorderProfileEx.get(cameraHolder.CurrentCamera, QUALITY_HIGH_SPEED_1080P), "1080pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
    }


}
