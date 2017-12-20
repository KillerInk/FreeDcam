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
import android.graphics.ImageFormat;
import android.graphics.Rect;
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
import android.text.TextUtils;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private final String TAG = VideoModuleApi2.class.getSimpleName();
    private boolean isRecording;
    private VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface recorderSurface;
    private File recordingFile;

    private MediaRecorder mediaRecorder;

    public VideoModuleApi2( CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_video);
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (cameraUiWrapper.getActivityInterface().getPermissionManager().hasRecordAudioPermission(null))
            startStopRecording();
    }

    private void startStopRecording()
    {
        if (isRecording)
            stopRecording();
        else
            startRecording();
    }

    @Override
    public void InitModule()
    {
        Log.d(TAG, "InitModule");
        super.InitModule();
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) parameterHandler.get(Settings.VideoProfiles);
        currentVideoProfile = profilesApi2.GetCameraProfile(SettingsManager.get(Settings.VideoProfiles).get());
        if (currentVideoProfile == null)
        {
            currentVideoProfile = profilesApi2.GetCameraProfile(SettingsManager.get(Settings.VideoProfiles).getValues()[0]);
        }
        parameterHandler.get(Settings.VideoProfiles).fireStringValueChanged(currentVideoProfile.ProfileName);
        startPreview();
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void DestroyModule()
    {
        if (isRecording)
            stopRecording();
        Log.d(TAG, "DestroyModule");
        cameraHolder.captureSessionHandler.CloseCaptureSession();
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
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_start);
        Log.d(TAG, "startRecording");
        startPreviewVideo();
    }

    private void stopRecording()
    {
        Log.d(TAG, "stopRecording");
        mediaRecorder.stop();
        mediaRecorder.reset();
        cameraHolder.captureSessionHandler.StopRepeatingCaptureSession();
        cameraHolder.captureSessionHandler.RemoveSurface(recorderSurface);
        recorderSurface = null;
        isRecording = false;

        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        cameraHolder.captureSessionHandler.CreateCaptureSession();
        fireOnWorkFinish(recordingFile);
        cameraUiWrapper.getActivityInterface().ScanFile(recordingFile);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview()
    {
        Size previewSize = getSizeForPreviewDependingOnImageSize(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), cameraHolder.characteristics, currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
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
        final int w,h, or;
        w = previewSize.getWidth();
        h = previewSize.getHeight();
        or = orientation;
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                    cameraHolder.captureSessionHandler.SetTextureViewSize(w, h, or,or+180,true);
            }
        });

        SurfaceTexture texture = cameraHolder.captureSessionHandler.getSurfaceTexture();
        if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed)
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        else
            texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        previewsurface = new Surface(texture);
        cameraHolder.captureSessionHandler.AddSurface(previewsurface,true);
        cameraHolder.captureSessionHandler.CreateCaptureSession();
    }

    public Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= cameraHolder.captureSessionHandler.displaySize.x && s.getHeight() <= cameraHolder.captureSessionHandler.displaySize.y && (double)s.getWidth()/s.getHeight() == ratio)
                sizes.add(s);

        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CameraHolderApi2.CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            return choices[0];
        }
    }

    @Override
    public void stopPreview() {
        DestroyModule();
    }


    @TargetApi(VERSION_CODES.LOLLIPOP)
    private void startPreviewVideo()
    {
        recordingFile = new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), ".mp4"));
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setMaxFileSize(3037822976L); //~2.8 gigabyte
        mediaRecorder.setMaxDuration(7200000); //2hours
        mediaRecorder.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
                changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
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

        if (SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_))){
            Location location = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
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

        try {
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(currentVideoProfile.videoFrameRate, currentVideoProfile.videoFrameRate),true);
        }catch (Exception e)
        {
            Log.WriteEx(e);
        }

      //  if(currentVideoProfile.Mode == VideoMediaProfile.VideoMode.SlowMO)
       //     int SlowFactor = currentVideoProfile.videoFrameRate /30;

        /*if(currentVideoProfile.videoFrameRate == 120 && currentVideoProfile.videoFrameWidth == 1920)
            mediaRecorder.setVideoFrameRate(60);
        else*/
            mediaRecorder.setVideoFrameRate(currentVideoProfile.videoFrameRate);

        /*setCaptureRate

        Added in API level 11
        void setCaptureRate (double fps)
        Set video frame capture rate. This can be used to set a different video frame capture rate than the recorded video's playback rate.
        !!!!!! This method also sets the recording mode to time lapse.!!!!!
        In time lapse video recording, only video is recorded.
        Audio related parameters are ignored when a time lapse recording session starts, if an application sets them.*/
        //mediaRecorder.setCaptureRate((double)currentVideoProfile.videoFrameRate);

        mediaRecorder.setVideoSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        try {
            mediaRecorder.setVideoEncoder(currentVideoProfile.videoCodec);
        }
        catch (IllegalArgumentException ex)
        {
            mediaRecorder.reset();
            UserMessageHandler.sendMSG("VideoCodec not Supported",false);
        }

        switch (currentVideoProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if (currentVideoProfile.isAudioActive)
                {
                    try {
                        mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        mediaRecorder.reset();
                        UserMessageHandler.sendMSG("AudioCodec not Supported",false);
                    }
                    mediaRecorder.setAudioChannels(currentVideoProfile.audioChannels);
                    mediaRecorder.setAudioEncodingBitRate(currentVideoProfile.audioBitRate);
                    mediaRecorder.setAudioSamplingRate(currentVideoProfile.audioSampleRate);
                }
                break;
            case Timelapse:
                float frame = 30;
                if (!TextUtils.isEmpty(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME)))
                    frame = Float.parseFloat(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME).replace(",", "."));
                else
                    SettingsManager.getInstance().setApiString(SettingsManager.TIMELAPSEFRAME, "" + frame);
                mediaRecorder.setCaptureRate(frame);
                break;
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException ex) {
            Log.WriteEx(ex);
            changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
            return;
        }
        recorderSurface = mediaRecorder.getSurface();
        cameraHolder.captureSessionHandler.AddSurface(recorderSurface,true);

        if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed)
            cameraHolder.captureSessionHandler.CreateCaptureSession(previewrdy);
        else
            cameraHolder.captureSessionHandler.CreateHighSpeedCaptureSession(previewrdy);
    }

    private void setRecorderFilePath() {
        if (!SettingsManager.getInstance().GetWriteExternal()) {
            mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
        }
        else
        {
            Uri uri = Uri.parse(SettingsManager.getInstance().GetBaseFolder());
            DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
            DocumentFile wr = df.createFile("*/*", recordingFile.getName());
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = cameraUiWrapper.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                mediaRecorder.setOutputFile(fileDescriptor.getFileDescriptor());
            } catch (FileNotFoundException e) {
                Log.WriteEx(e);
                try {
                    fileDescriptor.close();
                } catch (IOException e1) {
                    Log.WriteEx(e1);
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
            cameraHolder.captureSessionHandler.SetCaptureSession(cameraCaptureSession);
            if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed) {

                cameraHolder.captureSessionHandler.StartRepeatingCaptureSession();
            }
            else
            {
                cameraHolder.captureSessionHandler.StartHighspeedCaptureSession();
            }
            mediaRecorder.start();
            isRecording = true;

        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "Failed to Config CaptureSession");
        }
    };

    @Override
    public void internalFireOnWorkDone(File file) {

    }
}
