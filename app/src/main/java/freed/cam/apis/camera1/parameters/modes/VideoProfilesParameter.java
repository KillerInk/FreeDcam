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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
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
    public static final String _2160p = "2160p";
    public static final String _2160pDCI = "2160pDCI";

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
            supportedProfiles = new HashMap<>();
            if (cameraUiWrapper.GetAppSettingsManager().getMediaProfiles().size() == 0)
            {
                Logger.d(TAG, "new file,lookupDefaultProfiles");
                lookupDefaultProfiles(supportedProfiles);
                Logger.d(TAG,"Save found Profiles");
                cameraUiWrapper.GetAppSettingsManager().saveMediaProfiles(supportedProfiles);
            }
            else
                supportedProfiles = cameraUiWrapper.GetAppSettingsManager().getMediaProfiles();
        }
    }

    private void lookupDefaultProfiles(HashMap<String, VideoMediaProfile> supportedProfiles)
    {
        int CAMCORDER_QUALITY_2160p = 12;
        int CAMCORDER_QUALITY_2160pDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160p = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI = 1013;
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
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_2160pDCI)) //<--that will kill it when profile is not supported
            {

                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_2160pDCI);

                supportedProfiles.put("2160pDCI",new VideoMediaProfile(fourk, "2160pDCI", VideoMode.Normal,true));
                Logger.d(TAG, "found 2160pDCI");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_2160p))
            {
                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_2160p);

                supportedProfiles.put("2160p",new VideoMediaProfile(fourk, "2160p", VideoMode.Normal,true));
                Logger.d(TAG, "found 2160p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160p)) {
                supportedProfiles.put("2160p_TimeLapse", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160p), "Timelapse2160p", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse2160p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI)) {
                supportedProfiles.put("2160p_DCI_TimeLapse", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_2160pDCI), "Timelapse2160pDCI", VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse2160pDCI");
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

        if (parameters.get("video-size-values")!=null && parameters.get("video-size-values").contains("3840x2160")
                && parameters.get("video-hfr-values")!=null&& parameters.get("video-hfr-values").contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhdHFR = supportedProfiles.get("1080p").clone();
                uhdHFR.videoFrameWidth = 3840;
                uhdHFR.videoFrameHeight = 2160;
                uhdHFR.videoBitRate = 30000000;
                uhdHFR.Mode = VideoMode.Highspeed;
                uhdHFR.ProfileName = "UHD_2160p_60FPS";
                supportedProfiles.put("UHD_2160p_60FPS", uhdHFR);
                Logger.d(TAG, "added custom 2160pHFR");
            }


        }

        if (supportedProfiles.get(_2160p) == null && parameters.get("video-size-values") !=null&& parameters.get("video-size-values").contains("3840x2160"))
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("1080p").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.videoBitRate = 30000000;
                uhd.Mode = VideoMode.Normal;
                uhd.ProfileName = _2160p;
                supportedProfiles.put(_2160p, uhd);
                Logger.d(TAG, "added custom 2160p");
            }
        }

        if (parameters.get("video-size-values")!=null && parameters.get("video-size-values").contains("1920x1080")
                && parameters.get("video-hfr-values")!=null&& parameters.get("video-hfr-values").contains("60")) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
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
    }

    private boolean isKnownHFR_Qcom()
    {
        return cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADV234
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Nexus6p
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Nexus5x
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.OnePlusTwo
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI5
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI3W
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI4W;
    }
}
