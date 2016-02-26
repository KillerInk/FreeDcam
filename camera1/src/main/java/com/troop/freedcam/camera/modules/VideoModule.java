package com.troop.freedcam.camera.modules;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.i_camera.modules.VideoMediaProfile;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private static String TAG = VideoModule.class.getSimpleName();
    private VideoMediaProfile currentProfile;

    public VideoModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }



    protected MediaRecorder initRecorder()
    {
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());

        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        switch (currentProfile.Mode)
        {
            case Normal:
                recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                break;
            case Highspeed:
                break;
            case Timelapse:
                break;
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoFrameRate(currentProfile.videoFrameRate);
        recorder.setVideoSize(currentProfile.videoFrameWidth, currentProfile.videoFrameHeight);
        recorder.setVideoEncodingBitRate(currentProfile.videoBitRate);
        recorder.setVideoEncoder(currentProfile.videoCodec);

        switch (currentProfile.Mode)
        {
            case Normal:
                recorder.setAudioSamplingRate(currentProfile.audioSampleRate);
                recorder.setAudioEncodingBitRate(currentProfile.audioBitRate);
                recorder.setAudioChannels(currentProfile.audioChannels);
                recorder.setAudioEncoder(currentProfile.audioCodec);
                break;
            case Highspeed:
                break;
            case Timelapse:
                float frame = 30;
                if(!Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                    frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                else
                    Settings.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, ""+frame);
                recorder.setCaptureRate(frame);
                break;
        }
        return recorder;
    }



    @Override
    public void LoadNeededParameters()
    {

        if (ParameterHandler.VideoHDR != null)
            if(Settings.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && ParameterHandler.VideoHDR.IsSupported())
                ParameterHandler.VideoHDR.SetValue("on", true);
        loadProfileSpecificParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported())
            ParameterHandler.VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMediaProfile.VideoMode.Highspeed)
        {
            if (camParametersHandler.MemoryColorEnhancement != null && camParametersHandler.MemoryColorEnhancement.IsSupported())
                camParametersHandler.MemoryColorEnhancement.SetValue("disable", true);
            if (camParametersHandler.DigitalImageStabilization != null && camParametersHandler.DigitalImageStabilization.IsSupported())
                camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            if (camParametersHandler.VideoStabilization != null && camParametersHandler.VideoStabilization.IsSupported())
                camParametersHandler.VideoStabilization.SetValue("false", true);
            if (camParametersHandler.Denoise != null && camParametersHandler.Denoise.IsSupported())
                camParametersHandler.Denoise.SetValue("denoise-off", true);
            camParametersHandler.setString("preview-format", "yuv420sp");
            if (camParametersHandler.VideoHighFramerateVideo != null && camParametersHandler.VideoHighFramerateVideo.IsSupported())
            {
                camParametersHandler.VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", true);
            }
        }
        else
        {
            if (currentProfile.ProfileName.equals(VideoProfilesParameter._4kUHD))
            {
                if (camParametersHandler.MemoryColorEnhancement != null && camParametersHandler.MemoryColorEnhancement.IsSupported())
                    camParametersHandler.MemoryColorEnhancement.SetValue("disable", true);
                if (camParametersHandler.DigitalImageStabilization != null && camParametersHandler.DigitalImageStabilization.IsSupported())
                    camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
                if (camParametersHandler.VideoStabilization != null && camParametersHandler.VideoStabilization.IsSupported())
                    camParametersHandler.VideoStabilization.SetValue("false", true);
                if (camParametersHandler.Denoise != null && camParametersHandler.Denoise.IsSupported())
                    camParametersHandler.Denoise.SetValue("denoise-off", true);
                camParametersHandler.setString("preview-format", "nv12-venus");
            }
            else
                camParametersHandler.setString("preview-format", "yuv420sp");
            if (camParametersHandler.VideoHighFramerateVideo != null && camParametersHandler.VideoHighFramerateVideo.IsSupported())
            {
                camParametersHandler.VideoHighFramerateVideo.SetValue("off", true);
            }
        }

        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        camParametersHandler.setString("preview-size", size);
        camParametersHandler.setString("video-size", size);
        camParametersHandler.SetParametersToCamera();
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();

    }

    private void videoTime(int VB, int AB)
    {
        int i = VB / 2;
        int j = AB;

        long l2 = (i + j >> 3) / 1000;
        // long l3 = Environment.getExternalStorageDirectory().getUsableSpace() / l2;
        Log.d("VideoCamera Remaing", getTimeString(Environment.getExternalStorageDirectory().getUsableSpace() / l2)) ;

    }

    private String getTimeString(long paramLong)
    {
        long l1 = paramLong / 1000L;
        long l2 = l1 / 60L;
        long l3 = l2 / 60L;
        long l4 = l2 - 60L * l3;
        String str1 = Long.toString(l1 - 60L * l2);
        if (str1.length() < 2) {
            str1 = "0" + str1;
        }
        String str2 = Long.toString(l4);
        if (str2.length() < 2) {
            str2 = "0" + str2;
        }
        String str3 = str2 + ":" + str1;
        if (l3 > 0L)
        {
            String str4 = Long.toString(l3);
            if (str4.length() < 2) {
                str4 = "0" + str4;
            }
            str3 = str4 + ":" + str3;
        }
        return str3;
    }
}