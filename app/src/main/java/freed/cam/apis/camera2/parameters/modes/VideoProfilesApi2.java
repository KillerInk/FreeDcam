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

package freed.cam.apis.camera2.parameters.modes;

import android.media.CamcorderProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.camera2.CameraHolderApi2;
import android.util.Log;

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
            if (cameraUiWrapper.GetAppSettingsManager().getMediaProfiles().size() == 0)
            {
                lookupDefaultProfiles(supportedProfiles);
                if (has2160pSize()) {
                    supportedProfiles.put("2160p", new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p Normal true"));
                    supportedProfiles.put("2160p_Timelapse",new VideoMediaProfile("156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p_TimeLapse Timelapse true"));
                }
                cameraUiWrapper.GetAppSettingsManager().saveMediaProfiles(supportedProfiles);
            }
            else
                supportedProfiles = cameraUiWrapper.GetAppSettingsManager().getMediaProfiles();
        }
    }

    private boolean has2160pSize()
    {
        String[] size = cameraUiWrapper.GetParameterHandler().PictureSize.GetValues();
        for (String s: size) {
            if (s.matches("3840x2160"))
                return true;
        }
        return false;
    }

    //156000 2 3 48000 30 2 10007 48000000 2 30 2160 3840 2160p Normal true
    private void lookupDefaultProfiles(HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        int CAMCORDER_QUALITY_2160p = 12;
        int CAMCORDER_QUALITY_2160pDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160p = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI = 1013;

        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_2160P))
                supportedProfiles.put("2160p",new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_2160P),"2160p", VideoMode.Normal,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_480P))
                supportedProfiles.put("480p",new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_480P),"480p", VideoMode.Normal,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMode.Normal,true));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_1080P))
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMode.Normal,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P))
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMode.Timelapse,false));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P))
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMode.Timelapse,false));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P))
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P),"Timelapse1080p", VideoMode.Timelapse,false));
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_2160p))
            {
                CamcorderProfile fourk = CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_2160p);

                supportedProfiles.put("2160p",new VideoMediaProfile(fourk, "2160p", VideoMode.Normal,true));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160p))
                supportedProfiles.put("Timelapse2160p", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160p),"Timelapse2160p", VideoMode.Timelapse,false));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
                supportedProfiles.put("1080pHFR",new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P),"1080pHFR", VideoMode.Highspeed,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P))
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P),"2160pHFR", VideoMode.Highspeed,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P))
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P),"720pHFR", VideoMode.Highspeed,true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            if (CamcorderProfile.hasProfile(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P))
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(((CameraHolderApi2) cameraUiWrapper.GetCameraHolder()).CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P),"480pHFR", VideoMode.Highspeed,true));
        } catch (Exception ex) {
            ex.printStackTrace();
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
