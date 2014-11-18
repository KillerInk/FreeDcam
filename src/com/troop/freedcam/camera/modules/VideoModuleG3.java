package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import com.lge.media.CamcorderProfileEx;
import com.lge.media.MediaRecorderEx;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends VideoModule
{
    protected MediaRecorderEx recorder;

    public VideoModuleG3(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }

    protected MediaRecorder initRecorder() {
        recorder = new MediaRecorderEx();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfileEx prof = baseCameraHolder.ParameterHandler.VideoProfilesG3.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        recorder.setProfile(prof);
        return recorder;
    }
}
