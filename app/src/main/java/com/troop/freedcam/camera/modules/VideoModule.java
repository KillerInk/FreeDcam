package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.androiddng.DeviceUtils;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.modes.VideoProfilesParameter;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 16.08.2014.
 */
public class VideoModule extends AbstractModule
{
    private static String TAG = "freedcam.VideoModule";

    protected MediaRecorder recorder;
    String mediaSavePath;
    BaseCameraHolder baseCameraHolder;
    CamParametersHandler camParametersHandler;

    public VideoModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name  = ModuleHandler.MODULE_VIDEO;
        this.baseCameraHolder = cameraHandler;
        camParametersHandler = (CamParametersHandler) ParameterHandler;
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
            baseCameraHolder.errorHandler.OnError("Stop Recording failed, was called bevor start");
            ex.printStackTrace();
        }
        finally
        {
            recorder.reset();
            baseCameraHolder.GetCamera().lock();
            recorder.release();
            isWorking = false;
            final File file = new File(mediaSavePath);
            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
    }

    protected void prepareRecorder()
    {
        try
        {
            Log.d(TAG, "InitMediaRecorder");
            isWorking = true;
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

            if (Settings.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
                recorder.setOrientationHint(180);
            else
                recorder.setOrientationHint(0);

            recorder.setPreviewDisplay(baseCameraHolder.getSurfaceHolder().getSurface());
            try {
                Log.d(TAG,"Preparing Recorder");
                recorder.prepare();
                Log.d(TAG, "Recorder Prepared, Starting Recording");
                recorder.start();
                Log.d(TAG, "Recording started");
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);

            } catch (Exception e)
            {
                Log.e(TAG,"Recording failed");
                baseCameraHolder.errorHandler.OnError("Start Recording failed");
                e.printStackTrace();
                recorder.reset();
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
                isWorking = false;
                baseCameraHolder.GetCamera().lock();
                recorder.release();
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
            baseCameraHolder.errorHandler.OnError("Start Recording failed");
            recorder.reset();
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
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
        return recorder;
    }

    protected void setRecorderOutPutFile(String s)
    {
        recorder.setOutputFile(s);
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
        ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported())
            ParameterHandler.VideoHDR.SetValue("off", true);
    }

    private void loadProfileSpecificParameters()
    {
        if(DeviceUtils.isZTEADV())
            camParametersHandler.setString("slow_shutter", "-1");
        //baseCameraHolder.SetCameraParameters(camParametersHandler.getParameters());
        VideoProfilesParameter videoProfilesG3Parameter = (VideoProfilesParameter)ParameterHandler.VideoProfiles;
        if (videoProfilesG3Parameter != null) {
            String sprof = Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE);
            CamcorderProfile prof = videoProfilesG3Parameter.GetCameraProfile(sprof);
            String size = prof.videoFrameWidth + "x" + prof.videoFrameHeight;
            ParameterHandler.PreviewSize.SetValue(size, false);
            ParameterHandler.VideoSize.SetValue(size, true);
        }
    }
}
