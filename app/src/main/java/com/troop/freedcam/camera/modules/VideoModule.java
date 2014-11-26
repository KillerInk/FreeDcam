package com.troop.freedcam.camera.modules;

import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
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
            //


            //baseCameraHolder.StopPreview();
            //baseCameraHolder.StartPreview();



            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //baseCameraHolder.ParameterHandler.setString("video-size","");

            //baseCameraHolder.SetCameraParameters(baseCameraHolder.ParameterHandler.getParameters());
            //baseCameraHolder.ParameterHandler.PreviewSize.SetValue("3840x2160", true);

            Log.d(TAG, "InitMediaRecorder");

            baseCameraHolder.GetCamera().unlock();
            recorder =  initRecorder();



            /*
            recorder.setOutputFormat(prof.fileFormat);
            String[] split = Settings.getString(AppSettingsManager.SETTING_VIDEOSIZE).split("x");
            int w = Integer.parseInt(split[0]);
            int h = Integer.parseInt(split[1]);
            recorder.setVideoSize(w,h);
            recorder.setVideoEncoder(prof.videoCodec);
            recorder.setVideoEncodingBitRate(prof.videoBitRate);
            recorder.setVideoFrameRate(30);

            recorder.setAudioChannels(prof.audioChannels);
            recorder.setAudioEncoder(prof.audioCodec);
            recorder.setAudioEncodingBitRate(prof.audioBitRate);*/



            //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //recorder.setVideoSize(parametersManager.videoModes.Width, parametersManager.videoModes.Height);
            /*recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);*/

            /*recorder.setVideoEncodingBitRate(20000000);
            recorder.setVideoFrameRate(30);*/
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
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        CamcorderProfile prof = baseCameraHolder.ParameterHandler.VideoProfiles.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        recorder.setProfile(prof);
        return recorder;
    }

    protected void setRecorderOutPutFile(String s)
    {
        recorder.setOutputFile(s);
    }

    @Override
    public void LoadNeededParameters()
    {

        if (Settings.getString(AppSettingsManager.SETTING_VIDEOHDR).equals("on") && baseCameraHolder.ParameterHandler.VideoHDR.IsSupported());
            baseCameraHolder.ParameterHandler.VideoHDR.SetValue("on", true);
    }

    @Override
    public void UnloadNeededParameters() {
        baseCameraHolder.ParameterHandler.PreviewFormat.SetValue("yuv420sp", true);
        if (baseCameraHolder.ParameterHandler.VideoHDR.IsSupported())
            baseCameraHolder.ParameterHandler.VideoHDR.SetValue("off", true);
    }
}
