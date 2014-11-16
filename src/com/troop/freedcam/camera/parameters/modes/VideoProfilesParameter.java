package com.troop.freedcam.camera.parameters.modes;

import android.media.CamcorderProfile;

import java.util.HashMap;

/**
 * Created by troop on 13.11.2014.
 */
public class VideoProfilesParameter
{
    public void getProfiles()
    {

        /*HashMap<String, CamcorderProfile> supportedProfiles;
        int CAMCORDER_QUALITY_4kUHD = 12;
        int CAMCORDER_QUALITY_4kDCI = 13;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kUHD = 1012;
        int CAMCORDER_QUALITY_TIME_LAPSE_4kDCI = 1013;
        String current;

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
        }*/
    }
}
