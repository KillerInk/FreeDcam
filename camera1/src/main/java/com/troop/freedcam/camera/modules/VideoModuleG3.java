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

/**
 * Created by troop on 18.11.2014.
 */
public class VideoModuleG3 extends VideoModule
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
            float frame = 30;
            if(!Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
            else
                Settings.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, ""+frame);
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
        if (camParametersHandler.PreviewFormat == null && ParameterHandler.VideoSize == null)
            return;
        if (Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE).equals("4kUHD"))
        {
            camParametersHandler.MemoryColorEnhancement.SetValue("disable",true);
            camParametersHandler.DigitalImageStabilization.SetValue("disable", true);
            camParametersHandler.Denoise.SetValue("denoise-off", true);

            camParametersHandler.setString("dual-recorder", "0");
            //camParametersHandler.PreviewFormat.SetValue("nv12-venus", true);
            camParametersHandler.setString("preview-format", "nv12-venus");
            camParametersHandler.setString("lge-camera", "1");
        }
        else
        {
            //camParametersHandler.PreviewFormat.SetValue("yuv420sp", true);
            camParametersHandler.setString("lge-camera", "1");
            camParametersHandler.setString("dual-recorder", "0");
        }
        //baseCameraHolder.SetCameraParameters(camParametersHandler.getParameters());
        VideoProfilesG3Parameter videoProfilesG3Parameter = (VideoProfilesG3Parameter)ParameterHandler.VideoProfilesG3;
        String sprof = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        if (sprof.equals(""))
        {
            sprof = "HIGH";
            Settings.setString(AppSettingsManager.SETTING_VIDEPROFILE, sprof);
        }
        CamcorderProfileEx prof = videoProfilesG3Parameter.GetCameraProfile(sprof);
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


    }
}
