/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera2.modules;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Size;
import android.view.Surface;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.I_RecorderStateChanged;
import com.freedcam.apis.basecamera.modules.ModuleEventHandler;
import com.freedcam.apis.basecamera.modules.VideoMediaProfile;
import com.freedcam.apis.camera2.CameraHolder;
import com.freedcam.apis.camera2.parameters.modes.VideoProfilesApi2;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = VideoModuleApi2.class.getSimpleName();
    private boolean isRecording = false;
    private Size previewSize;
    private VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface recorderSurface;

    private MediaRecorder mediaRecorder;

    public VideoModuleApi2(Context context, I_CameraUiWrapper cameraUiWrapper, ModuleEventHandler eventHandler) {
        super(context,cameraUiWrapper,eventHandler);
        name = KEYS.MODULE_VIDEO;
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
    public void InitModule()
    {
        Logger.d(TAG, "InitModule");
        super.InitModule();
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) ParameterHandler.VideoProfiles;
        currentVideoProfile = profilesApi2.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        startPreview();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void DestroyModule()
    {
        Logger.d(TAG, "DestroyModule");
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
        cameraHolder.CaptureSessionH.RemoveSurface(recorderSurface);
        recorderSurface = null;
        isRecording = false;

        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        cameraHolder.CaptureSessionH.CreateCaptureSession();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview()
    {

        previewSize = new Size(currentVideoProfile.videoFrameWidth,currentVideoProfile.videoFrameHeight);
        cameraHolder.CaptureSessionH.SetTextureViewSize(previewSize.getWidth(), previewSize.getHeight(), 270,90,true);

        SurfaceTexture texture = cameraHolder.CaptureSessionH.getSurfaceTexture();

        texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth,currentVideoProfile.videoFrameHeight);
        previewsurface = new Surface(texture);
       /* if (cameraHolder.mProcessor != null) {
            cameraHolder.mProcessor.kill();
        }*/
        cameraHolder.CaptureSessionH.AddSurface(previewsurface,true);


        cameraHolder.CaptureSessionH.CreateCaptureSession();

    }

    @Override
    public void stopPreview() {
        DestroyModule();
    }


    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void startPreviewVideo()
    {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Logger.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            }
        });

        mediaRecorder.setAudioSource(AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
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
        cameraHolder.CaptureSessionH.AddSurface(recorderSurface,true);

        cameraHolder.CaptureSessionH.CreateCaptureSession(previewrdy);
    }

    private StateCallback previewrdy = new StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            cameraHolder.CaptureSessionH.SetCaptureSession(cameraCaptureSession);
            cameraHolder.CaptureSessionH.StartRepeatingCaptureSession();
            mediaRecorder.start();
            isRecording = true;
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Logger.d(TAG, "Failed to Config CaptureSession");
        }
    };
}
