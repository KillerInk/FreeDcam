package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractVideoModule
{
    private static String TAG = VideoModule.class.getSimpleName();



    public VideoModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }



    protected MediaRecorder initRecorder() {
        String hfr = ParameterHandler.VideoHighFramerateVideo.GetValue();
        String hsr = ParameterHandler.VideoHighSpeedVideo.GetValue();
        recorder = new MediaRecorder();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetCamera());
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesParameter videoProfilesParameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        CamcorderProfile prof = videoProfilesParameter.GetCameraProfile(profile);


        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        if (!profile.contains("Timelapse")) {
            recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        }

        /*if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).contains("4kUHD")||Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kDCI")||Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("TimelapseHIGH"))
        {

            //camParametersHandler.UHDDO();
          //  ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
            //recorder.setMaxFileSize(3037822976L);
            //recorder.setMaxDuration(7200000);
            //recorder.setCaptureRate(30);
            //recorder.setVideoFrameRate(30);

            //ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);
            //  camParametersHandler.setString("preview-size", "3840x2160");
            // camParametersHandler.setString("video-size", "3840x2160");

           // ParameterHandler.MemoryColorEnhancement.SetValue("disable",true);
           // ParameterHandler.DigitalImageStabilization.SetValue("disable", true);
           // ParameterHandler.Denoise.SetValue("denoise-off", true);
            //  baseCameraHolder.StopPreview();
            // baseCameraHolder.StartPreview();
        }



        /*recorder.setOutputFormat(prof.fileFormat);

        if (!profile.contains("Timelapse")) {
            recorder.setAudioChannels(prof.audioChannels);
            recorder.setAudioEncoder(prof.audioCodec);
            recorder.setAudioEncodingBitRate(prof.audioBitRate);
            recorder.setAudioSamplingRate(prof.audioSampleRate);
        }


        recorder.setVideoEncoder(prof.videoCodec);
        recorder.setVideoEncodingBitRate(prof.videoBitRate);
        recorder.setVideoSize(prof.videoFrameWidth ,prof.videoFrameHeight);
        if (!profile.contains("Timelapse"))
            recorder.setVideoFrameRate(prof.videoFrameRate);*/



        recorder.setProfile(prof);

        if (profile.contains("Timelapse"))
        {
            float frame = 30;
            if(!Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
            else
                Settings.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, ""+frame);
            recorder.setCaptureRate(frame);
        }


           if (!hfr.equals("off")) {
               recorder.setCaptureRate(Integer.parseInt(hfr));
            }

           if (!hsr.equals("off")) {
               recorder.setCaptureRate(Integer.parseInt(hsr));
               recorder.setVideoFrameRate(Integer.parseInt(hsr));
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
        String hfr = ParameterHandler.VideoHighFramerateVideo.GetValue();
        String hsr = ParameterHandler.VideoHighSpeedVideo.GetValue();
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);

        if (profile.equals("4kUHD") || DeviceUtils.isXiaomiMI3W() && profile.contains("HIGH") || DeviceUtils.isXiaomiMI4W() && profile.contains("HIGH")) {
            camParametersHandler.MemoryColorEnhancement.SetValue("disable", true);
            camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            camParametersHandler.VideoStabilization.SetValue("false", true);
            camParametersHandler.Denoise.SetValue("denoise-off", true);
            camParametersHandler.setString("dual-recorder", "0");
            camParametersHandler.setString("preview-format", "nv12-venus");
        }
        else if (!hfr.equals("off") || !hsr.equals("off") || profile.contains("HFR")) {
            camParametersHandler.MemoryColorEnhancement.SetValue("disable", true);
            camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            camParametersHandler.VideoStabilization.SetValue("false", true);
            camParametersHandler.Denoise.SetValue("denoise-off", true);
            camParametersHandler.setString("dual-recorder", "0");
            camParametersHandler.setString("preview-format", "yuv420sp");
        }
        else
            camParametersHandler.setString("preview-format", "yuv420sp");


        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        if (videoProfilesG3Parameter != null) {
            String sprof = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
            if (sprof.equals(""))
            {
                sprof = "HIGH";
                Settings.setString(AppSettingsManager.SETTING_VIDEPROFILE, sprof);
            }
            CamcorderProfile prof = videoProfilesG3Parameter.GetCameraProfile(sprof);
            if (prof == null)
                return;
            String size = prof.videoFrameWidth + "x" + prof.videoFrameHeight;
            camParametersHandler.setString("preview-size", size);
            camParametersHandler.setString("video-size", size);
            camParametersHandler.SetParametersToCamera();
            baseCameraHolder.StopPreview();
            baseCameraHolder.StartPreview();
        }
       // camParametersHandler.UHDDO();
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
