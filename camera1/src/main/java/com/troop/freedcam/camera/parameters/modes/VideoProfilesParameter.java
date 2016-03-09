package com.troop.freedcam.camera.parameters.modes;

import android.media.CamcorderProfile;
import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.modules.VideoMediaProfile;
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
    final String TAG = VideoProfilesParameter.class.getSimpleName();
    HashMap<String, VideoMediaProfile> supportedProfiles;
    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;
    String profile;
    public static final String _720phfr = "720HFR";
    public static final String _4kUHD = "4kUHD";

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
            Logger.d(TAG, "Load supportedProfiles");
            String current;

            supportedProfiles = new HashMap<String, VideoMediaProfile>();
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
                supportedProfiles.put("480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_480P), "480p", VideoMediaProfile.VideoMode.Normal, true));
                Logger.d(TAG,"found 480p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P))
            {
                supportedProfiles.put("720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_720P), "720p", VideoMediaProfile.VideoMode.Normal,true));
                Logger.d(TAG, "found 720p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P)) {
                supportedProfiles.put("1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_1080P), "1080p", VideoMediaProfile.VideoMode.Normal, true));
                Logger.d(TAG,"found 1080p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P)) {
                supportedProfiles.put("Timelapse480p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_480P), "Timelapse480p", VideoMediaProfile.VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timnelapse480p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P)) {
                supportedProfiles.put("Timelapse720p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_720P), "Timelapse720p", VideoMediaProfile.VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse720p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P)) {
                supportedProfiles.put("Timelapse1080p", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_TIME_LAPSE_1080P), "Timelapse1080p", VideoMediaProfile.VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse1080p");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI)
                    || (DeviceUtils.IS(DeviceUtils.Devices.Htc_M9) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W )|| DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W))) //<--that will kill it when profile is not supported
            {

                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kDCI);

                supportedProfiles.put("4kDCI",new VideoMediaProfile(fourk, "4kDCI", VideoMediaProfile.VideoMode.Normal,true));
                Logger.d(TAG, "found 4kDCI");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
            {
                CamcorderProfile fourk = CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_4kUHD);

                supportedProfiles.put("4kUHD",new VideoMediaProfile(fourk, "4kUHD", VideoMediaProfile.VideoMode.Normal,true));
                Logger.d(TAG, "found 4kUHD");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD)) {
                supportedProfiles.put("Timelapse4kUHD", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD), "Timelapse4kUHD", VideoMediaProfile.VideoMode.Timelapse, false));
                Logger.d(TAG, "found Timelapse4kUHD");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P))
            {
                supportedProfiles.put("1080pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_1080P), "1080pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Logger.d(TAG, "found 1080pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P)) {
                supportedProfiles.put("2016pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_2160P), "2016HFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Logger.d(TAG, "found 2016pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P)) {
                supportedProfiles.put("720pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_720P), "720pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Logger.d(TAG, "found 720pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }
        try {
            if (CamcorderProfile.hasProfile(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P)) {
                supportedProfiles.put("480pHFR", new VideoMediaProfile(CamcorderProfile.get(cameraHolder.CurrentCamera, CamcorderProfile.QUALITY_HIGH_SPEED_480P), "480pHFR", VideoMediaProfile.VideoMode.Highspeed, true));
                Logger.d(TAG, "found 480pHFR");
            }
        } catch (Exception e) {
            Logger.exception(e);
        }

        if (supportedProfiles.get(_720phfr) == null && parameters.containsKey("video-hfr-values") && parameters.get("video-hfr-values").contains("120"))
        {
            Logger.d(TAG, "no 720phfr profile found, but hfr supported, try to add custom 720phfr");
            VideoMediaProfile t = supportedProfiles.get("720p").clone();
            t.videoFrameRate = 120;
            t.Mode = VideoMediaProfile.VideoMode.Highspeed;
            t.ProfileName = "720pHFR";
            supportedProfiles.put("720pHFR",t);
        }

        if (supportedProfiles.get(_4kUHD) == null && parameters.containsKey("video-size-values") && parameters.get("video-size-values").contains("3840x2160")
                || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) || DeviceUtils.IS(DeviceUtils.Devices.LenovoK920))
        {
            if (supportedProfiles.containsKey("1080p"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("1080p").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.videoBitRate = 30000000;
                uhd.Mode = VideoMediaProfile.VideoMode.Normal;
                uhd.ProfileName = _4kUHD;
                supportedProfiles.put(_4kUHD, uhd);
                Logger.d(TAG, "added custom 4kuhd");
            }
        }


        if (parameters.containsKey("video-size-values") && parameters.get("video-size-values").contains("1920x1080")
                && (parameters.containsKey("video-hfr-values")&& parameters.get("video-hfr-values").contains("60"))
                || DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV) || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)) //<--- that line is not needed. when parameters contains empty hfr it gets filled!
        {
            if (supportedProfiles.containsKey("1080p")) {
                VideoMediaProfile t = supportedProfiles.get("1080p").clone();
                t.videoFrameRate = 60;
                t.Mode = VideoMediaProfile.VideoMode.Highspeed;
                t.ProfileName = "1080pHFR";
                supportedProfiles.put("1080pHFR", t);
                Logger.d(TAG, "added custom 1080pHFR");
            }

        }

        if (DeviceUtils.IS(DeviceUtils.Devices.ZTE_ADV) || DeviceUtils.IS(DeviceUtils.Devices.ZTEADVIMX214) || DeviceUtils.IS(DeviceUtils.Devices.ZTEADV234))
        {
            if (supportedProfiles.containsKey("4kUHD"))
            {
                VideoMediaProfile uhd = supportedProfiles.get("4kUHD").clone();
                uhd.videoFrameWidth = 3840;
                uhd.videoFrameHeight = 2160;
                uhd.Mode = VideoMediaProfile.VideoMode.Timelapse;
                //profile must contain 4kUHD else it gets not detected!
                uhd.ProfileName = "4kUHDTimeLapse";
                supportedProfiles.put(uhd.ProfileName, uhd);
                Logger.d(TAG, "added custom 4kUHDTimelapse");
            }
        }

    }
}
