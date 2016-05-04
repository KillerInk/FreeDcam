package com.freedcam.apis.camera1.camera.modules;

import android.media.MediaRecorder;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.camera1.camera.parameters.modes.VideoProfilesG3Parameter;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.apis.i_camera.modules.VideoMediaProfile;
import com.freedcam.ui.AppSettingsManager;
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

    public VideoModuleG3(BaseCameraHolder cameraHandler, ModuleEventHandler eventHandler) {
        super(cameraHandler, eventHandler);
    }

    protected MediaRecorder initRecorder()
    {

        try {
            recorder = new MediaRecorderEx();
            recorder.reset();
            recorder.setCamera(baseCameraHolder.GetCamera());
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
                    if (!AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                        frame = Float.parseFloat(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                    else
                        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, "" + frame);
                    recorder.setCaptureRate(frame);
                    break;
            }
            if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD")) {

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
        currentProfile = videoProfilesG3Parameter.GetCameraProfile(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_VIDEPROFILE));
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
        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();
    }
}
