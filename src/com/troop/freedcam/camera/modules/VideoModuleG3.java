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

    protected MediaRecorder initRecorder() {
        recorder = new MediaRecorderEx();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfileEx prof = baseCameraHolder.ParameterHandler.VideoProfilesG3.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        recorder.setProfile(prof);
        recorder.setMaxFileSize(3037822976L);
        recorder.setMaxDuration(7200000);
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
        /*if (baseCameraHolder != null && baseCameraHolder.ParameterHandler.PreviewFormat != null)
        {

            if (baseCameraHolder.IsPreviewRunning())
                baseCameraHolder.StopPreview();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            baseCameraHolder.ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);
            baseCameraHolder.ParameterHandler.setString("video-hfr", "off");
            baseCameraHolder.ParameterHandler.PreviewSize.SetValue("3840x2160", true);

            //baseCameraHolder.ParameterHandler.PreviewSize.SetValue(baseCameraHolder.ParameterHandler.VideoSize.GetValue(), true);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            baseCameraHolder.StartPreview();

        }*/

        baseCameraHolder.ParameterHandler.setString("dual-recorder", "0");
        baseCameraHolder.ParameterHandler.setString("preview-format", "nv12-venus");
        baseCameraHolder.ParameterHandler.setString("video-hfr", "off");
        baseCameraHolder.ParameterHandler.setString("video-hdr", "off");
        baseCameraHolder.ParameterHandler.setString("lge-camera", "1");
        baseCameraHolder.ParameterHandler.setString("preview-size","3840x2160");

        baseCameraHolder.SetCameraParameters(baseCameraHolder.ParameterHandler.getParameters());


        //baseCameraHolder.ParameterHandler.PreviewSize.SetValue("3840x2160", true);
        baseCameraHolder.ParameterHandler.setString("video-size","3840x2160");
        baseCameraHolder.SetCameraParameters(baseCameraHolder.ParameterHandler.getParameters());
    }
}
