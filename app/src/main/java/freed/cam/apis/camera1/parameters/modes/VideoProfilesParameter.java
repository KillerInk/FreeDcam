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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.interfaces.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.basecamera.modules.VideoMediaProfile.VideoMode;
import freed.cam.apis.camera1.CameraHolder;
import freed.utils.DeviceUtils.Devices;
import freed.utils.Logger;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoProfilesParameter extends BaseModeParameter
{
    private final String TAG = VideoProfilesParameter.class.getSimpleName();
    private HashMap<String, VideoMediaProfile> supportedProfiles;
    private final CameraHolder cameraHolder;
    private String profile;
    private static final String _720phfr = "720HFR";
    public static final String _4kUHD = "4kUHD";

    public VideoProfilesParameter(Parameters parameters,CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();
        this.cameraUiWrapper = cameraUiWrapper;
        isSupported =true;
        loadProfiles();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        profile = valueToSet;
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
            Logger.d(TAG, "Load supportedProfiles");
            String current;

            supportedProfiles = new HashMap<>();
            File f = new File(VideoMediaProfile.MEDIAPROFILESPATH);
            if (!f.exists())
            {
                Logger.d(TAG, "new file,lookupDefaultProfiles");
                lookupDefaultProfiles(supportedProfiles);
                Logger.d(TAG,"Save found Profiles");
                VideoMediaProfile.saveCustomProfiles(supportedProfiles);
            }
            if (f.exists())
            {
                Logger.d(TAG, "file exists load from txt");
                try {
                    VideoMediaProfile.loadCustomProfiles(supportedProfiles);
                }
                catch (Exception ex)
                {
                    Logger.exception(ex);
                    Logger.d(TAG, "Failed to load CustomProfiles.txt");
                    f.delete();
                    lookupDefaultProfiles(supportedProfiles);
                    VideoMediaProfile.saveCustomProfiles(supportedProfiles);
                }

            }

        }
    }

    private void lookupDefaultProfiles(HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        int CAMCORDER_QUALITY_4kUHD = 12;
        int CAMCORDER_QUALITY_4kDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kUHD = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kDCI = 1013;
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P)) {
                supportedProfiles.put("480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P), "480p", VideoMode.Normal, true));
                Logger.d(TAG,"found 480p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMode.Normal,true));
                Logger.d(TAG, "found 720p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P)) {
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMode.Normal, true));
                Logger.d(TAG,"found 1080p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P)) {
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timnelapse480p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P)) {
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse720p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P)) {
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P), "Timelapse1080p", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse1080p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI)
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Htc_M9 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.OnePlusOne || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W) //<--that will kill it when profile is not supported
            {

                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI);

                supportedProfiles.put("4kDCI",new VideoMediaProfile(fourk, "4kDCI", VideoMode.Normal,true));
                Logger.d(TAG, "found 4kDCI");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);

                supportedProfiles.put("4kUHD",new VideoMediaProfile(fourk, "4kUHD", VideoMode.Normal,true));
                Logger.d(TAG, "found 4kUHD");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD)) {
                supportedProfiles.put("Timelapse4kUHD", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD), "Timelapse4kUHD", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse4kUHD");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
            {
                supportedProfiles.put("1080pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P), "1080pHFR", VideoMode.Highspeed, true));
                Logger.d(TAG, "found 1080pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P)) {
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P), "2016HFR", VideoMode.Highspeed, true));
                Logger.d(TAG, "found 2016pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P)) {
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P), "720pHFR", VideoMode.Highspeed, true));
                Logger.d(TAG, "found 720pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P)) {
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P), "480pHFR", VideoMode.Highspeed, true));
                Logger.d(TAG, "found 480pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        if (supportedProfiles.get(_720phfr) == null && parameters.get("video-hfr-values")!=null && parameters.get("video-hfr-values").contains("120"))
        {
            Logger.d(TAG, "no 720phfr profile found, but hfr supported, try to add custom 720phfr");
            VideoMediaProfile t = supportedProfiles.get("720p").clone();
            t.videoFrameRate = 120;
            t.Mode = VideoMode.Highspeed;
            t.ProfileName = "720pHFR";
            supportedProfiles.put("720pHFR",t);
        }

        if (supportedProfiles.get(_4kUHD) == null && parameters.get("video-size-values") !=null&& parameters.get("video-size-values").contains("3840x2160")
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LenovoK920)
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("1080p").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.videoBitRate = 30000000;
                uhd.Mode = VideoMode.Normal;
                uhd.ProfileName = _4kUHD;
                supportedProfiles.put(_4kUHD, uhd);
                Logger.d(TAG, "added custom 4kuhd");
            }
        }


        if (parameters.get("video-size-values")!=null && parameters.get("video-size-values").contains("1920x1080")
                && parameters.get("video-hfr-values")!=null&& parameters.get("video-hfr-values").contains("60")
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
        {
            if (supportedProfiles.containsKey("1080p")) {
                VideoMediaProfile t = supportedProfiles.get("1080p").clone();
                t.videoFrameRate = 60;
                t.Mode = VideoMode.Highspeed;
                t.ProfileName = "1080pHFR";
                supportedProfiles.put("1080pHFR", t);
                Logger.d(TAG, "added custom 1080pHFR");
            }

        }

        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADV234)
        {
            if (supportedProfiles.containsKey("4kUHD"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("4kUHD").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.Mode = VideoMode.Timelapse;
                //profile must contain 4kUHD else it gets not detected!
                uhd.ProfileName = "4kUHDTimeLapse";
                supportedProfiles.put(uhd.ProfileName, uhd);
                Logger.d(TAG, "added custom 4kUHDTimelapse");
            }
        }

    }
}
