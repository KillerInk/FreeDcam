package com.troop.freecam.manager.camera_parameters;

import android.hardware.Camera;
import android.media.CamcorderProfile;

import com.troop.freecam.camera.old.CameraManager;
import com.troop.freecam.manager.AppSettingsManager;

import java.util.HashMap;
import java.util.List;

/**
 * Created by troop on 14.08.2014.
 */
public class VideoParameters extends PictureParameters
{
    public VideoSizes videoSizes;
    public VideoProfiles VideoProfileClass;

    public VideoParameters(CameraManager cameraManager, AppSettingsManager preferences) {
        super(cameraManager, preferences);
    }

    @Override
    public void SetCameraParameters(Camera.Parameters parameters) {
        super.SetCameraParameters(parameters);
        videoSizes = new VideoSizes();
        VideoProfileClass = new VideoProfiles();
    }

    public class VideoSizes
    {
        List<Camera.Size> sizes;
        public int Width;
        public int Height;

        public VideoSizes()
        {
            sizes = parameters.getSupportedVideoSizes();
            if (sizes == null || sizes.size() == 0)
                sizes = parameters.getSupportedPreviewSizes();
            if (preferences != null)
                SetSize(preferences.VideoSize.Get());

        }

        public String[] getStringValues()
        {
            String[] ar = new String[sizes.size()];
            for (int i = 0; i< sizes.size(); i++)
            {
                ar[i] = (sizes.get(i).width + "x" + sizes.get(i).height);
            }
            return ar;
        }

        public void SetSize(String tmp)
        {
            String[] widthHeight = tmp.split("x");
            Width = Integer.parseInt(widthHeight[0]);
            Height = Integer.parseInt(widthHeight[1]);
            preferences.VideoSize.Set(Width + "x" + Height);
            onParametersCHanged(BaseParametersManager.enumParameters.VideoModes);
        }
    }

    public class VideoProfiles
    {
        HashMap<String, CamcorderProfile> supportedProfiles;
        int CAMCORDER_QUALITY_4kUHD = 12;
        int CAMCORDER_QUALITY_4kDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kUHD = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kDCI = 1013;
        String current;

        public VideoProfiles()
        {
            supportedProfiles = new HashMap<String, CamcorderProfile>();
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera, CamcorderProfile.QUALITY_LOW))
                    supportedProfiles.put("LOW", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_HIGH))
                    supportedProfiles.put("HIGH", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_QCIF))
                    supportedProfiles.put("QCIF", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_CIF))
                    supportedProfiles.put("CIF", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_480P))
                    supportedProfiles.put("480p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_480P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_720P))
                    supportedProfiles.put("720p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_720P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_1080P))
                    supportedProfiles.put("1080p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_1080P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_QVGA))
                    supportedProfiles.put("QVGA", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_LOW))
                    supportedProfiles.put("TimelapseLOW", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_LOW));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_HIGH))
                    supportedProfiles.put("TimelapseHIGH", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_HIGH));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_QCIF))
                    supportedProfiles.put("TimelapseQCIF", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_QCIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_CIF))
                    supportedProfiles.put("TimelapseCIF", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_CIF));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_480P))
                    supportedProfiles.put("Timelapse480p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_480P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_720P))
                    supportedProfiles.put("Timelapse720p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_720P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_1080P))
                    supportedProfiles.put("Timelapse1080p", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_1080P));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_QVGA))
                    supportedProfiles.put("TimelapseQVGA", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CamcorderProfile.QUALITY_TIME_LAPSE_QVGA));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera, CAMCORDER_QUALITY_4kDCI))
                    supportedProfiles.put("4kDCI", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CAMCORDER_QUALITY_4kDCI));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera, CAMCORDER_QUALITY_4kUHD))
                    supportedProfiles.put("4kUHD", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CAMCORDER_QUALITY_4kUHD));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kDCI))
                    supportedProfiles.put("Timelapse4kDCI", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CAMCORDER_QUALITY_TIME_LAPSE_4kDCI));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (CamcorderProfile.hasProfile(cameraManager.Settings.CurrentCamera, CAMCORDER_QUALITY_TIME_LAPSE_4kUHD))
                    supportedProfiles.put("Timelapse4kUHD", CamcorderProfile.get(cameraManager.Settings.CurrentCamera,CAMCORDER_QUALITY_TIME_LAPSE_4kUHD));
            } catch (Exception e) {
                e.printStackTrace();
            }

            current = cameraManager.Settings.VideoProfileSETTINGS.get();
        }

        public String[] getProfiles()
        {
            return supportedProfiles.keySet().toArray(new String[supportedProfiles.keySet().size()]);
        }

        public CamcorderProfile getProfile()
        {
            return supportedProfiles.get(current);
        }

        public String getProfileString()
        {
            return current;
        }

        public void SetProfile(String value)
        {
            current = value;
            cameraManager.Settings.VideoProfileSETTINGS.set(value);
        }
    }
}
