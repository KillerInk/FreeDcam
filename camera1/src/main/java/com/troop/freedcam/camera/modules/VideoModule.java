package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private static String TAG = VideoModule.class.getSimpleName();

    public VideoModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }



    protected MediaRecorder initRecorder()
    {
        String hfr = "",hsr = "";
        if (ParameterHandler.VideoHighFramerateVideo != null && ParameterHandler.VideoHighFramerateVideo.IsSupported())
            hfr = ParameterHandler.VideoHighFramerateVideo.GetValue();
        if (ParameterHandler.VideoHighSpeedVideo != null && ParameterHandler.VideoHighSpeedVideo.IsSupported())
            hsr = ParameterHandler.VideoHighSpeedVideo.GetValue();
        String mBitare = Settings.getString(AppSettingsManager.SETTING_VideoBitrate);
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesParameter videoProfilesParameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        VideoMediaProfile prof = videoProfilesParameter.GetCameraProfile(profile);

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

        Log.e(TAG, "Index :" + hfr);
        if (!hfr.equals("Default")) {
            int frame = Integer.parseInt(hfr.split("@")[1]);
            Log.e(TAG, "Index :" + frame);
            //camParametersHandler.FPSRangeLock(frame,frame);
            recorder.setVideoFrameRate(frame);
        }
        else
        {
            recorder.setVideoFrameRate(prof.videoFrameRate);
        }
        recorder.setVideoSize(prof.videoFrameWidth, prof.videoFrameHeight);

        if (mBitare.equals("Default") || mBitare.equals(""))
        {
            recorder.setVideoEncodingBitRate(prof.videoBitRate);
        }
        else {

            recorder.setVideoEncodingBitRate(Integer.parseInt(mBitare.split("M")[0]) * 1000000);

        }

        recorder.setVideoEncoder(prof.videoCodec);

        switch (prof.Mode)
        {
            case Normal:
                recorder.setAudioSamplingRate(prof.audioSampleRate);
                recorder.setAudioEncodingBitRate(prof.audioBitRate);
                recorder.setAudioChannels(prof.audioChannels);
                recorder.setAudioEncoder(prof.audioCodec);
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
        String hfr = "";
        String hsr = "";
        //TODO fixup that stuff, it will fc on devices wich doesnt support that parameters
        if (ParameterHandler.VideoHighFramerateVideo != null && ParameterHandler.VideoHighFramerateVideo.IsSupported())
            hfr = ParameterHandler.VideoHighFramerateVideo.GetValue();
        if (ParameterHandler.VideoHighSpeedVideo != null && ParameterHandler.VideoHighSpeedVideo.IsSupported())
            hsr = ParameterHandler.VideoHighSpeedVideo.GetValue();
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);


        if (profile.equals("4kUHD") || (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) && profile.contains("HIGH")))
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
        else if ((!hfr.equals("") && !hfr.equals("off")) || (!hsr.equals("") && !hsr.equals("off")) || profile.contains("HFR"))
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
        }
        else
            camParametersHandler.setString("preview-format", "yuv420sp");


        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
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