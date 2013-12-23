package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;

import com.troop.freecam.CamPreview;
import com.troop.freecam.SavePictureTask;
import com.troop.freecam.manager.ParametersManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by troop on 18.10.13.
 */
public class VideoCam extends PictureCam
{

    protected MediaRecorder recorder;
    String mediaSavePath;
    public boolean IsRecording = false;
    public ParametersManager parametersManager;
    public String lastPicturePath;

    public VideoCam(CamPreview context, SharedPreferences preferences)
    {
        super(context, preferences);
    }

    public void StartRecording()
    {
        try
        {

            if (parametersManager.isOrientationFIX())
                fixParametersOrientation();
        mCamera.unlock();
        File sdcardpath = Environment.getExternalStorageDirectory();

        recorder.setCamera(mCamera);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        if (parametersManager.getParameters().getPreviewSize().height == 1080)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P));
        if (parametersManager.getParameters().getPreviewSize().height == 720)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
            if (parametersManager.getParameters().getPreviewSize().width == 960)
                recorder.setVideoSize(960, 720);
            else
                recorder.setVideoSize(1280,720);
        }
        if (parametersManager.getParameters().getPreviewSize().height == 480)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
            if (parametersManager.getParameters().getPreviewSize().height == 800)
                recorder.setVideoSize(800, 480);
            if (parametersManager.getParameters().getPreviewSize().height == 640)
                recorder.setVideoSize(640,480);

        }
        /*if (parameters.getPreviewSize().height == 576)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
            recorder.setVideoSize(720,576);
        }*/
        if (parametersManager.getParameters().getPreviewSize().height == 240)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
            recorder.setVideoSize(320,240);
        }
        if (parametersManager.getParameters().getPreviewSize().height == 288)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
        if (parametersManager.getParameters().getPreviewSize().height == 160)
        {
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_CIF));
            recorder.setVideoSize(240,160);
        }
        if (parametersManager.getParameters().getPreviewSize().height == 144)
            recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_QCIF));

        if (preferences.getBoolean("upsidedown", false) == true)
        {
            String rota = parametersManager.getParameters().get("rotation");

            if (rota != null && rota.equals("180"))
                recorder.setOrientationHint(180);
            if (rota == null)
                recorder.setOrientationHint(0);
        }
        //recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaSavePath = SavePictureTask.getFilePath("mp4", sdcardpath).getAbsolutePath();
        recorder.setOutputFile(mediaSavePath);
        recorder.setPreviewDisplay(context.getHolder().getSurface());
        try {
            recorder.prepare();
            recorder.start();
            IsRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        }
        catch (NullPointerException ex)
        {

        }

    }

    public  void StopRecording()
    {
        IsRecording = false;
        recorder.stop();
        scanManager.startScan(mediaSavePath);
        lastPicturePath = mediaSavePath;
        recorder.reset();
        mCamera.lock();
    }


    @Override
    protected void CloseCamera()
    {
        if (IsRecording)
            StopRecording();
        recorder.reset();
        recorder.release();
        recorder = null;
        super.CloseCamera();
    }

    @Override
    protected void OpenCamera() {
        super.OpenCamera();
        recorder = new MediaRecorder();
    }

    private void fixParametersOrientation()
    {
        String tmp = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D);

        if(!tmp.equals(ParametersManager.SwitchCamera_MODE_3D) && !tmp.equals(ParametersManager.SwitchCamera_MODE_2D))
        {
            // mCamera.setDisplayOrientation(0);
            parametersManager.getParameters().setRotation(0);
        }
        else
        {
            //mCamera.setDisplayOrientation(180);
            parametersManager.getParameters().setRotation(180);
        }
    }
}
