package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractModule
{
    public final String TAG = "freedcam.VideoModule";

    protected MediaRecorder recorder;
    String mediaSavePath;

    public VideoModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name  = ModuleHandler.MODULE_VIDEO;
    }


    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public String LongName() {
        return "Movie";
    }

//I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!isWorking)
            startRecording();
        else
            stopRecording();

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END


    private void startRecording()
    {
        prepareRecorder();
        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
    }

    private void stopRecording()
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
            baseCameraHolder.GetCamera().lock();
            recorder.release();
            isWorking = false;
            eventHandler.WorkFinished(new File(mediaSavePath));
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
    }

    private void prepareRecorder()
    {
        try
        {
            Log.d(TAG, "InitMediaRecorder");

            baseCameraHolder.GetCamera().unlock();
            recorder =  initRecorder();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Log.e("MediaRecorder", "ErrorCode: " + what + " Extra: " + extra);
                }
            });

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

    protected MediaRecorder initRecorder() {
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

        recorder.setProfile(prof);

        if (profile.contains("Timelapse"))
        {
            float frame = Float.parseFloat(Settings.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
            recorder.setCaptureRate(frame);
        }
        return recorder;
    }

    protected void setRecorderOutPutFile(String s)
    {
        recorder.setOutputFile(s);
    }

    @Override
    public void LoadNeededParameters()
    {

        if (Settings.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && ParameterHandler.VideoHDR.IsSupported());
            ParameterHandler.VideoHDR.SetValue("on", true);
    }

    @Override
    public void UnloadNeededParameters() {
        ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
        if (ParameterHandler.VideoHDR.IsSupported())
            ParameterHandler.VideoHDR.SetValue("off", true);
    }
}
