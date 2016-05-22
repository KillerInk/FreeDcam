package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.media.MediaRecorder;

import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesG3Parameter;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.lge.media.MediaRecorderEx;


/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends AbstractVideoModule
{
    private MediaRecorderEx recorder;
    private VideoMediaProfile currentProfile;

    final static String TAG = VideoModuleG3.class.getSimpleName();

    public VideoModuleG3(CameraHolderApi1 cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
    }

    protected MediaRecorder initRecorder()
    {

        try {
            recorder = new MediaRecorderEx();
            recorder.reset();
            recorder.setCamera(cameraHolderApi1.GetCamera());
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            switch (currentProfile.Mode)
            {

                case Normal:
                case Highspeed:
                    if (currentProfile.isAudioActive)
                        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
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
                case Highspeed:
                    if (currentProfile.isAudioActive)
                        setAudioStuff(currentProfile);
                    break;
                case Timelapse:
                    float frame = 30;
                    if (!appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                        frame = Float.parseFloat(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                    else
                        appSettingsManager.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, "" + frame);
                    recorder.setCaptureRate(frame);
                    break;
            }
            if (appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD")) {

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

    private void loadProfileSpecificParameters()
    {
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        if (currentProfile.Mode == VideoMediaProfile.VideoMode.Highspeed || currentProfile.ProfileName.contains("4kUHD"))
        {
            ParameterHandler.MemoryColorEnhancement.SetValue("disable",true);
            ParameterHandler.DigitalImageStabilization.SetValue("disable", true);
            ParameterHandler.Denoise.SetValue("denoise-off", true);

            ((CamParametersHandler)ParameterHandler).SetDualRecorder();
            //camParametersHandler.PreviewFormat.SetValue("nv12-venus", true);
            if(!DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                ParameterHandler.PreviewFormat.SetValue("nv12-venus",true);
            ((CamParametersHandler)ParameterHandler).SetLGCamera();
            if (currentProfile.Mode == VideoMediaProfile.VideoMode.Highspeed)
            {
                if (ParameterHandler.VideoHighFramerateVideo != null && ParameterHandler.VideoHighFramerateVideo.IsSupported())
                {
                    ParameterHandler.VideoHighFramerateVideo.SetValue(currentProfile.videoFrameRate+"", true);
                }
            }
        }
        else
        {
            ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
            ((CamParametersHandler)ParameterHandler).SetLGCamera();
            ((CamParametersHandler)ParameterHandler).SetDualRecorder();
        }
        String size = currentProfile.videoFrameWidth + "x" + currentProfile.videoFrameHeight;
        ParameterHandler.PreviewSize.SetValue(size,true);
        ParameterHandler.VideoSize.SetValue(size,true);
        cameraHolderApi1.StopPreview();
        cameraHolderApi1.StartPreview();
    }
}
