package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.lge.media.CamcorderProfileEx;
import com.lge.media.MediaRecorderEx;
import com.sec.android.secmediarecorder.SecMediaRecorder;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesG3Parameter;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 07.01.2015.
 */
public class VideoModuleSamsung extends VideoModule
{
    SecMediaRecorder recorder;

    public VideoModuleSamsung(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
    }

    protected void prepareRecorder()
    {
        try
        {
            Log.d(TAG, "InitMediaRecorder");

            baseCameraHolder.GetSamsungCamera().unlock();
            recorder =  initSecRecorder();


            File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
            if (!file.exists())
                file.mkdirs();
            Date date = new Date();
            String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);

            mediaSavePath = new StringBuilder(String.valueOf(file.getPath())).append(File.separator).append("VID_").append(s).append(".mp4").toString();
            setRecorderOutPutFile(mediaSavePath);

            try {
                Log.d(TAG,"Preparing Recorder");
                recorder.prepare();
                Log.d(TAG, "Recorder Prepared, Starting Recording");
                recorder.start();
                Log.d(TAG, "Recording started");
                isWorking = true;
            } catch (Exception e)
            {
                Log.e(TAG,"Recording failed");
                e.printStackTrace();
                recorder.reset();

                baseCameraHolder.GetCamera().lock();
                recorder.release();
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();

            recorder.reset();

            baseCameraHolder.GetCamera().lock();
            recorder.release();
        }
    }

    protected SecMediaRecorder initSecRecorder()
    {
        String profile = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
        VideoProfilesParameter videoProfilesParameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        CamcorderProfile prof = videoProfilesParameter.GetCameraProfile(profile);
        String size = prof.videoFrameWidth + "x"+prof.videoFrameHeight;

        recorder = new SecMediaRecorder();
        recorder.reset();
        recorder.setCamera(baseCameraHolder.GetSamsungCamera());
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

    protected void stopRecording()
    {
        try {
            recorder.stop();
            Log.e(TAG, "Stop Recording");
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Stop Recording failed, was called bevor start");
            ex.printStackTrace();
        }
        finally
        {
            recorder.reset();
            baseCameraHolder.GetSamsungCamera().lock();
            recorder.release();
            isWorking = false;
            eventHandler.WorkFinished(new File(mediaSavePath));
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
    }
}
