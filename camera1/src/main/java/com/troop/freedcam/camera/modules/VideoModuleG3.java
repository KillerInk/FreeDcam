package com.troop.freedcam.camera.modules;

import android.media.MediaRecorder;
import android.util.Log;

import com.lge.media.CamcorderProfileEx;
import com.lge.media.MediaRecorderEx;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    protected MediaRecorderEx recorder;
    CamParametersHandler camParametersHandler;

    final static String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        camParametersHandler = (CamParametersHandler) ParameterHandler;

    }

    protected MediaRecorder initRecorder()
    {
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        VideoMediaProfile prof = videoProfilesG3Parameter.GetCameraProfile(profile);

        try {
            recorder = new MediaRecorderEx();
            recorder.reset();
            recorder.setCamera(baseCameraHolder.GetCamera());
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            switch (prof.Mode)
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
            recorder.setVideoFrameRate(prof.videoFrameRate);
            recorder.setVideoSize(prof.videoFrameWidth, prof.videoFrameHeight);
            recorder.setVideoEncodingBitRate(prof.videoBitRate);
            recorder.setVideoEncoder(prof.videoCodec);

            switch (prof.Mode)
            {
                case Normal:
                    setAudioStuff(prof);
                    recorder.setCaptureRate(prof.videoFrameRate);
                    break;
                case Highspeed:
                    recorder.setCaptureRate(prof.videoFrameRate);
                    break;
                case Timelapse:
                    float frame = 30;
                    if (!Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                        frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                    else
                        Settings.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, "" + frame);
                    recorder.setCaptureRate(frame);
                    break;
            }
            if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD")) {

                recorder.setMaxFileSize(3037822976L);
                recorder.setMaxDuration(7200000);
            }
        }
        catch (IllegalStateException ex)
        {
            recorder.reset();
        }
        return recorder;
    }

    private void setAudioStuff(VideoMediaProfile prof) {
        recorder.setAudioSamplingRate(prof.audioSampleRate);
        recorder.setAudioEncodingBitRate(prof.audioBitRate);
        recorder.setAudioChannels(prof.audioChannels);
        recorder.setAudioEncoder(prof.audioCodec);
    }

    @Override
    protected void setRecorderOutPutFile(String s)
    {
        super.setRecorderOutPutFile(s);
    }

    @Override
    public void LoadNeededParameters()
    {
        loadProfileSpecificParameters();
    }

    @Override
    public void UnloadNeededParameters() {

    }

    public void UpdatePreview()
    {
        loadProfileSpecificParameters();
    }

    private void loadProfileSpecificParameters()
    {
        if (camParametersHandler.PreviewFormat == null && ParameterHandler.VideoSize == null)
            return;
        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD") || Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).contains("HFR"))
        {
            camParametersHandler.MemoryColorEnhancement.SetValue("disable",true);
            camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            camParametersHandler.Denoise.SetValue("denoise-off", true);

            camParametersHandler.setString("dual-recorder", "0");
            //camParametersHandler.PreviewFormat.SetValue("nv12-venus", true);
            if(!DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                camParametersHandler.setString("preview-format", "nv12-venus");
            camParametersHandler.setString("lge-camera", "1");
        }
        else
        {
            camParametersHandler.setString("preview-format", "yuv420sp");
            camParametersHandler.setString("lge-camera", "1");
            camParametersHandler.setString("dual-recorder", "0");
        }
        //baseCameraHolder.SetCameraParameters(camParametersHandler.getParameters());
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        String sprof = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);

        VideoMediaProfile prof = videoProfilesG3Parameter.GetCameraProfile(sprof);
        String size;
        if (prof == null)
        {
            Log.e(TAG , "Error: CamcorderProfileEx is NULL!!!!!!!!!");
            size = camParametersHandler.VideoSize.GetValue();
        }
        else {
            size = prof.videoFrameWidth + "x" + prof.videoFrameHeight;
        }
        camParametersHandler.setString("preview-size", size);
        camParametersHandler.setString("video-size", size);
        camParametersHandler.SetParametersToCamera();
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();


    }
}
