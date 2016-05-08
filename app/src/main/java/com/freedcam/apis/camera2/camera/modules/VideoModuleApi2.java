package com.freedcam.apis.camera2.camera.modules;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Size;
import android.view.Surface;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.parameters.modes.VideoProfilesApi2;
import com.freedcam.apis.basecamera.camera.modules.I_RecorderStateChanged;
import com.freedcam.apis.basecamera.camera.modules.VideoMediaProfile;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = VideoModuleApi2.class.getSimpleName();
    private CameraHolderApi2 cameraHolder;
    private boolean isRecording = false;
    private Size previewSize;
    private VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface recorderSurface;

    private MediaRecorder mediaRecorder;

    public VideoModuleApi2(CameraHolderApi2 cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        this.cameraHolder = cameraHandler;
        this.name = AbstractModuleHandler.MODULE_VIDEO;
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        if (isRecording)
            stopRecording();
        else
            startRecording();
        return true;
    }

    @Override
    public boolean IsWorking() {
        return super.IsWorking();
    }

    @Override
    public void LoadNeededParameters()
    {
        Logger.d(TAG, "LoadNeededParameters");
        cameraHolder.ModulePreview = this;
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) ParameterHandler.VideoProfiles;
        currentVideoProfile = profilesApi2.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        cameraHolder.StartPreview();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void UnloadNeededParameters()
    {
        Logger.d(TAG, "UnloadNeededParameters");
        cameraHolder.CaptureSessionH.CloseCaptureSession();
        previewsurface = null;
    }

    @Override
    public String LongName() {
        return "Video";
    }

    @Override
    public String ShortName() {
        return "Vid";
    }

    private void startRecording()
    {

        /*int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = ORIENTATIONS.get(rotation);
        MediaRecorder.setOrientationHint(orientation);*/
        Logger.d(TAG, "startRecording");
        startPreviewVideo();
    }

    private void stopRecording()
    {
        Logger.d(TAG, "stopRecording");
        mediaRecorder.stop();
        mediaRecorder.reset();
        baseCameraHolder.CaptureSessionH.RemoveSurface(recorderSurface);
        recorderSurface = null;
        isRecording = false;

        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        baseCameraHolder.CaptureSessionH.CreateCaptureSession();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview()
    {
        previewSize = new Size(currentVideoProfile.videoFrameWidth,currentVideoProfile.videoFrameHeight);
        baseCameraHolder.CaptureSessionH.SetTextureViewSize(previewSize.getWidth(), previewSize.getHeight(), 270,90,true);

        SurfaceTexture texture = baseCameraHolder.textureView.getSurfaceTexture();

        texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth,currentVideoProfile.videoFrameHeight);
        previewsurface = new Surface(texture);
       /* if (cameraHolderApi1.mProcessor != null) {
            cameraHolderApi1.mProcessor.kill();
        }*/
        baseCameraHolder.CaptureSessionH.AddSurface(previewsurface,true);


        baseCameraHolder.CaptureSessionH.CreateCaptureSession();

    }

    @Override
    public void stopPreview() {
        UnloadNeededParameters();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startPreviewVideo()
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Logger.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            }
        });

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (!appSettingsManager.GetWriteExternal()) {
            mediaRecorder.setOutputFile(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".mp4"));
        }
        else
        {
            Uri uri = Uri.parse(appSettingsManager.GetBaseFolder());
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
            DocumentFile wr = df.createFile("*/*", new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".mp4")).getName());
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                mediaRecorder.setOutputFile(fileDescriptor.getFileDescriptor());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                try {
                    fileDescriptor.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        mediaRecorder.setVideoEncodingBitRate(currentVideoProfile.videoBitRate);
        mediaRecorder.setVideoFrameRate(currentVideoProfile.videoFrameRate);
        mediaRecorder.setVideoSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        mediaRecorder.setVideoEncoder(currentVideoProfile.videoCodec);

        mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
        mediaRecorder.setAudioChannels(currentVideoProfile.audioChannels);
        mediaRecorder.setAudioEncodingBitRate(currentVideoProfile.audioBitRate);
        mediaRecorder.setAudioSamplingRate(currentVideoProfile.audioSampleRate);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Logger.exception(e);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            return;
        }
        recorderSurface = mediaRecorder.getSurface();
        baseCameraHolder.CaptureSessionH.AddSurface(recorderSurface,true);

        baseCameraHolder.CaptureSessionH.CreateCaptureSession(previewrdy);
    }

    private CameraCaptureSession.StateCallback previewrdy = new CameraCaptureSession.StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            baseCameraHolder.mCaptureSession = cameraCaptureSession;
            try {
                baseCameraHolder.mCaptureSession.setRepeatingRequest(baseCameraHolder.mPreviewRequestBuilder.build(),
                        baseCameraHolder.cameraBackroundValuesChangedListner, null);
                //cameraHolderApi1.SetLastUsedParameters(cameraHolderApi1.mPreviewRequestBuilder);
                mediaRecorder.start();
                isRecording = true;
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
            } catch (CameraAccessException e)
            {
                baseCameraHolder.mCaptureSession = null;
                Logger.exception(e);
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };
}
