package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import com.lge.media.CamcorderProfileEx;
import com.lge.media.MediaRecorderEx;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends VideoModule
{
    protected MediaRecorderEx recorder;

    public VideoModuleG3(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }

    protected MediaRecorder initRecorder()
    {
        CamcorderProfileEx prof = ParameterHandler.VideoProfilesG3.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        String size = prof.videoFrameWidth + "x"+prof.videoFrameHeight;

        recorder = new MediaRecorderEx();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setProfile(prof);
        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD")) {
            recorder.setMaxFileSize(3037822976L);
            recorder.setMaxDuration(7200000);
        }
        return recorder;
    }

    @Override
    protected void setRecorderOutPutFile(String s)
    {
        super.setRecorderOutPutFile(s);
        /*FileOutputStream out;
        try {
            out = new FileOutputStream(s);
            recorder.setOutputFileFD(out.getFD());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void LoadNeededParameters()
    {
        loadProfileSpecificParameters();
    }

    public void UpdatePreview()
    {
        loadProfileSpecificParameters();
    }

    private void loadProfileSpecificParameters()
    {

        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD"))
        {
            ParameterHandler.MemoryColorEnhancement.SetValue("disable",true);
            ParameterHandler.DigitalImageStabilization.SetValue("disable", true);
            ParameterHandler.Denoise.SetValue("denoise-off", true);

            ParameterHandler.setString("dual-recorder", "0");
            ParameterHandler.setString("preview-format", "nv12-venus");

            //ParameterHandler.setString("video-hfr", "off");
            //ParameterHandler.setString("video-hdr", "off");
            ParameterHandler.setString("lge-camera", "1");
        }
        else
        {
            ParameterHandler.setString("preview-format", "nv12-venus");
            ParameterHandler.setString("lge-camera", "1");
            ParameterHandler.setString("dual-recorder", "0");
        }
        baseCameraHolder.SetCameraParameters(ParameterHandler.getParameters());
        CamcorderProfileEx prof = ParameterHandler.VideoProfilesG3.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        String size = prof.videoFrameWidth + "x"+prof.videoFrameHeight;
        ParameterHandler.PreviewSize.SetValue(size, false);
        ParameterHandler.setString("video-size", size);

        baseCameraHolder.SetCameraParameters(ParameterHandler.getParameters());
    }
}
