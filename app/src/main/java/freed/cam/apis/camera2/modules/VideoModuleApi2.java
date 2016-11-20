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

package freed.cam.apis.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.location.Location;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoSource;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.CaptureStates;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private final String TAG = VideoModuleApi2.class.getSimpleName();
    private boolean isRecording;
    private Size previewSize;
    private VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface recorderSurface;
    private File recordingFile;

    private MediaRecorder mediaRecorder;

    public VideoModuleApi2( CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
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
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) parameterHandler.VideoProfiles;
        currentVideoProfile = profilesApi2.GetCameraProfile(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        startPreview();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void DestroyModule()
    {
        if (isRecording)
            stopRecording();
        Logger.d(TAG, "DestroyModule");
        cameraHolder.CaptureSessionH.CloseCaptureSession();
        previewsurface = null;
        super.DestroyModule();
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
        changeCaptureState(CaptureStates.RECORDING_START);
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

        cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(CaptureStates.RECORDING_STOP);
        changeCaptureState(CaptureStates.RECORDING_STOP);
        cameraHolder.CaptureSessionH.CreateCaptureSession();
        cameraUiWrapper.getActivityInterface().getImageSaver().scanFile(recordingFile);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview()
    {
        previewSize = new Size(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int orientation = 0;
        switch (sensorOrientation)
        {
            case 90:
                orientation = 270;
                break;
            case 180:
                orientation =0;
                break;
            case 270: orientation = 90;
                break;
            case 0: orientation = 180;
                break;
        }
        cameraHolder.CaptureSessionH.SetTextureViewSize(previewSize.getWidth(), previewSize.getHeight(), orientation,orientation+180,true);
        SurfaceTexture texture = cameraHolder.CaptureSessionH.getSurfaceTexture();
        texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        previewsurface = new Surface(texture);
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
        recordingFile = new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), ".mp4"));
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setMaxFileSize(3037822976L); //~2.8 gigabyte
        mediaRecorder.setMaxDuration(7200000); //2hours
        mediaRecorder.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Logger.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
                cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(CaptureStates.RECORDING_STOP);
                changeCaptureState(CaptureStates.RECORDING_STOP);
            }
        });

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                {
                    recordnextFile(mr);
                }
                else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
                {
                    recordnextFile(mr);
                }
            }
        });

        if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON)){
            Location location = cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation();
            if (location != null)
                mediaRecorder.setLocation((float) location.getLatitude(), (float) location.getLongitude());
        }
        switch (currentVideoProfile.Mode)
        {

            case Normal:
            case Highspeed:
                if (currentVideoProfile.isAudioActive)
                    mediaRecorder.setAudioSource(AudioSource.CAMCORDER);
                break;
            case Timelapse:
                break;
        }
        mediaRecorder.setVideoSource(VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
        setRecorderFilePath();

        mediaRecorder.setVideoEncodingBitRate(currentVideoProfile.videoBitRate);

        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,new Range<>(currentVideoProfile.videoFrameRate,currentVideoProfile.videoFrameRate));

      //  if(currentVideoProfile.Mode == VideoMediaProfile.VideoMode.SlowMO)
       //     int SlowFactor = currentVideoProfile.videoFrameRate /30;

        mediaRecorder.setVideoFrameRate(currentVideoProfile.videoFrameRate);

        mediaRecorder.setCaptureRate((double)currentVideoProfile.videoFrameRate);
        mediaRecorder.setVideoSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        mediaRecorder.setVideoEncoder(currentVideoProfile.videoCodec);

        switch (currentVideoProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if (currentVideoProfile.isAudioActive)
                {
                    mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
                    mediaRecorder.setAudioChannels(currentVideoProfile.audioChannels);
                    mediaRecorder.setAudioEncodingBitRate(currentVideoProfile.audioBitRate);
                    mediaRecorder.setAudioSamplingRate(currentVideoProfile.audioSampleRate);
                }
                break;
            case Timelapse:
                float frame = 30;
                if (!appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).equals(""))
                    frame = Float.parseFloat(appSettingsManager.getString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME).replace(",", "."));
                else
                    appSettingsManager.setString(AppSettingsManager.SETTING_VIDEOTIMELAPSEFRAME, "" + frame);
                mediaRecorder.setCaptureRate(frame);
                break;
        }



        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Logger.exception(e);
            cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(CaptureStates.RECORDING_STOP);
            changeCaptureState(CaptureStates.RECORDING_STOP);
            return;
        }
        recorderSurface = mediaRecorder.getSurface();
        cameraHolder.CaptureSessionH.AddSurface(recorderSurface,true);

        if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed)
            cameraHolder.CaptureSessionH.CreateCaptureSession(previewrdy);
        else
            cameraHolder.CaptureSessionH.CreateHighSpeedCaptureSession(previewrdy);
    }

    private void setRecorderFilePath() {
        if (!appSettingsManager.GetWriteExternal()) {
            mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
        }
        else
        {
            Uri uri = Uri.parse(appSettingsManager.GetBaseFolder());
            DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
            DocumentFile wr = df.createFile("*/*", recordingFile.getName());
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = cameraUiWrapper.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
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
    }

    private void recordnextFile(MediaRecorder mr) {
        stopRecording();
        startRecording();
    }

    private final StateCallback previewrdy = new StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed) {
                cameraHolder.CaptureSessionH.SetCaptureSession(cameraCaptureSession);
                cameraHolder.CaptureSessionH.StartRepeatingCaptureSession();
            }
            else
            {
                cameraHolder.CaptureSessionH.SetHighSpeedCaptureSession(cameraCaptureSession);
                cameraHolder.CaptureSessionH.StartHighspeedCaptureSession();
            }
            mediaRecorder.start();
            isRecording = true;
            cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(CaptureStates.RECORDING_START);
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Logger.d(TAG, "Failed to Config CaptureSession");
        }
    };
}
