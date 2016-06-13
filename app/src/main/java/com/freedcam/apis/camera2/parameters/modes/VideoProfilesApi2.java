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

package com.freedcam.apis.camera2.parameters.modes;

import android.media.CamcorderProfile;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import com.freedcam.apis.camera2.CameraHolderApi2;
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

    public VideoProfilesApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        loadProfiles();
        isSupported = true;
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
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_480P))
                supportedProfiles.put("480p",new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_480P),"480p", VideoMode.Normal,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMode.Normal,true));
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_1080P))
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMode.Normal,true));
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
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMode.Timelapse,false));
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
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfile fourk = CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_4kUHD);

                supportedProfiles.put("4kUHD",new VideoMediaProfile(fourk, "4kUHD", VideoMode.Normal,true));
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
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                supportedProfiles.put("Timelapse4kUHD", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD),"Timelapse4kUHD", VideoMode.Timelapse,false));
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR",new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P),"1080pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P))
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P),"2016HFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P),"720pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P))
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P),"480pHFR", VideoMode.Highspeed,true));
        } catch (Exception e) {
            Logger.exception(e);
        }
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
    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
        if (cameraUiWrapper.GetModuleHandler().GetCurrentModule() != null && cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
        {
            cameraUiWrapper.GetModuleHandler().GetCurrentModule().DestroyModule();
            cameraUiWrapper.GetModuleHandler().GetCurrentModule().InitModule();
        }

    }


}
