package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;

import com.lge.media.CamcorderProfileEx;
import com.lge.media.MediaRecorderEx;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
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
    CamParametersHandler camParametersHandler;

    public VideoModuleG3(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        camParametersHandler = (CamParametersHandler) ParameterHandler;
    }

    protected MediaRecorder initRecorder()
    {
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        CamcorderProfileEx prof = videoProfilesG3Parameter.GetCameraProfile(profile);
        String size = prof.videoFrameWidth + "x"+prof.videoFrameHeight;

        recorder = new MediaRecorderEx();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        if (!profile.contains("Timelapse")) {
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        }

        recorder.setProfile(prof);

        if (profile.contains("Timelapse"))
        {
            float frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
            recorder.setCaptureRate(frame);
        }
        if (profile.contains("HFR"))
        {
            recorder.setCaptureRate(120);
        }

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

            camParametersHandler.setString("dual-recorder", "0");
            camParametersHandler.setString("preview-format", "nv12-venus");

            //ParameterHandler.setString("video-hfr", "off");
            //ParameterHandler.setString("video-hdr", "off");
            camParametersHandler.setString("lge-camera", "1");
        }
        else
        {
            camParametersHandler.setString("preview-format", "nv12-venus");
            camParametersHandler.setString("lge-camera", "1");
            camParametersHandler.setString("dual-recorder", "0");
        }
        baseCameraHolder.SetCameraParameters(camParametersHandler.getParameters());
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        CamcorderProfileEx prof = videoProfilesG3Parameter.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        String size = prof.videoFrameWidth + "x"+prof.videoFrameHeight;
        ParameterHandler.PreviewSize.SetValue(size, false);
        camParametersHandler.setString("video-size", size);

        baseCameraHolder.SetCameraParameters(camParametersHandler.getParameters());
    }
}
