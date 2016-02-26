package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.camera2.parameters.modes.VideoProfilesApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.i_camera.modules.VideoMediaProfile;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;
import com.troop.freedcam.utils.VideoUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.troop.freedcam.camera2.BaseCameraHolderApi2.getSizeForPreviewDependingOnImageSize;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = StringUtils.TAG +PictureModuleApi2.class.getSimpleName();
    BaseCameraHolderApi2 cameraHolder;
    boolean isRecording = false;
    Size previewSize;
    VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface camerasurface;

    public MediaRecorder mediaRecorder;

    public VideoModuleApi2(BaseCameraHolderApi2 cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        this.cameraHolder = cameraHandler;
        this.Settings = Settings;
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
        cameraHolder.ModulePreview = this;
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) ParameterHandler.VideoProfiles;
        currentVideoProfile = profilesApi2.GetCameraProfile(Settings.getString(AppSettingsManager.SETTING_VIDEPROFILE));
        cameraHolder.StartPreview();
        super.LoadNeededParameters();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
        try {
            cameraHolder.mCaptureSession.stopRepeating();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ex){}
        cameraHolder.mPreviewRequestBuilder.removeTarget(camerasurface);
        cameraHolder.mPreviewRequestBuilder.removeTarget(previewsurface);
        Log.d(TAG, "Stop Preview");
        if (cameraHolder.mCaptureSession != null)
            cameraHolder.mCaptureSession.close();
        cameraHolder.mCaptureSession = null;
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
        Log.d(TAG, "startRecording");
        startPreviewVideo();
    }

    private void stopRecording()
    {
        Log.d(TAG, "stopRecording");
        mediaRecorder.stop();
        mediaRecorder.reset();
        isRecording = false;
        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        cameraHolder.StartPreview();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPreview()
    {
        previewSize = new Size(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight); //BaseCameraHolderApi2.getSizeForPreviewDependingOnVideo(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), baseCameraHolder.characteristics, currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);

        SurfaceTexture texture = baseCameraHolder.textureView.getSurfaceTexture();

        texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth,currentVideoProfile.videoFrameHeight);

        previewsurface = new Surface(texture);


        if (baseCameraHolder.mProcessor != null) {
            baseCameraHolder.mProcessor.kill();
        }
        baseCameraHolder.mProcessor.Reset(previewSize.getWidth(), previewSize.getHeight());

        baseCameraHolder.mProcessor.setOutputSurface(previewsurface);
        camerasurface = baseCameraHolder.mProcessor.getInputSurface();
        baseCameraHolder.mPreviewRequestBuilder.addTarget(camerasurface);
        baseCameraHolder.textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);
        matrix.setRectToRect(viewRect, viewRect, Matrix.ScaleToFit.FILL);
        if (Settings.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
            matrix.postRotate(180, viewRect.centerX(), viewRect.centerY());
        else
            matrix.postRotate(0, viewRect.centerX(), viewRect.centerY());
        baseCameraHolder.textureView.setTransform(matrix);

        try {

            baseCameraHolder.createPreviewCaptureSession(camerasurface, null);

        } catch (CameraAccessException e)
        {};

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
                Log.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            }
        });

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(StringUtils.getFilePath(Settings.GetWriteExternal(), ".mp4"));

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
            e.printStackTrace();
            return;
        }

        previewSize = new Size(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight); //BaseCameraHolderApi2.getSizeForPreviewDependingOnVideo(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), baseCameraHolder.characteristics, currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);

        SurfaceTexture texture = baseCameraHolder.textureView.getSurfaceTexture();

        texture.setDefaultBufferSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);

        previewsurface = new Surface(texture);


        try {
            baseCameraHolder.mPreviewRequestBuilder = baseCameraHolder.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            baseCameraHolder.cameraChangedListner.onCameraError("MediaRecorder Prepare failed");
            return;
        }

        if (baseCameraHolder.mProcessor != null) {
            baseCameraHolder.mProcessor.kill();
        }
        baseCameraHolder.mProcessor.Reset(previewSize.getWidth(), previewSize.getHeight());

        baseCameraHolder.mProcessor.setOutputSurface(previewsurface);
        camerasurface = baseCameraHolder.mProcessor.getInputSurface();
        baseCameraHolder.mPreviewRequestBuilder.addTarget(camerasurface);
        baseCameraHolder.mPreviewRequestBuilder.addTarget(mediaRecorder.getSurface());
        baseCameraHolder.textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);
        matrix.setRectToRect(viewRect, viewRect, Matrix.ScaleToFit.FILL);
        if (Settings.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
            matrix.postRotate(180, viewRect.centerX(), viewRect.centerY());
        else
            matrix.postRotate(0, viewRect.centerX(), viewRect.centerY());
        baseCameraHolder.textureView.setTransform(matrix);

        try {
                baseCameraHolder.mCameraDevice.createCaptureSession(Arrays.asList(camerasurface, mediaRecorder.getSurface()), previewrdy, null);

        } catch (CameraAccessException e)
        {};
    }

    private CameraCaptureSession.StateCallback previewrdy = new CameraCaptureSession.StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            baseCameraHolder.mCaptureSession = cameraCaptureSession;
            try {
                baseCameraHolder.mCaptureSession.setRepeatingRequest(baseCameraHolder.mPreviewRequestBuilder.build(),
                        baseCameraHolder.mCaptureCallback, null);
                baseCameraHolder.SetLastUsedParameters(baseCameraHolder.mPreviewRequestBuilder);
                mediaRecorder.start();
                isRecording = true;
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };
}
