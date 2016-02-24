package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
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
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
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
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = StringUtils.TAG +PictureModuleApi2.class.getSimpleName();
    BaseCameraHolderApi2 cameraHolder;
    boolean isRecording = false;
    private Size largestVideoSize;
    int videowidth, videoheight;
    Size previewSize;

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
        //cameraHolder.StopPreview();
        //cameraHolder.StartPreview();
        super.LoadNeededParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
        cameraHolder.StopPreview();
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


        mediaRecorder.start();
        isRecording = true;
        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
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

    @Override
    public void startPreview() {

    }

    @Override
    public void stopPreview() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startPreviewVideo()
    {
        largestVideoSize = Collections.max(
                Arrays.asList(baseCameraHolder.map.getOutputSizes(MediaRecorder.class)),
                new BaseCameraHolderApi2.CompareSizesByArea());

        Size video[] = baseCameraHolder.map.getOutputSizes(MediaRecorder.class);
        Size re [] = baseCameraHolder.map.getOutputSizes(TextureView.class);
        String[] split = baseCameraHolder.VideoSize.split("x");

        int width, height;
        if (split.length < 2)
        {
            videowidth = 1280;
            videoheight = 720;
        }
        else
        {
            videowidth = Integer.parseInt(split[0]);
            videoheight = Integer.parseInt(split[1]);
        }

        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mediaRecorder.setOutputFile(StringUtils.getFilePath(Settings.GetWriteExternal(), ".mp4"));

        mediaRecorder.setVideoEncodingBitRate(VideoUtils.getVideoBitrate("Low"));

        mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Log.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            }
        });
        mediaRecorder.setVideoFrameRate(30);
        mediaRecorder.setVideoSize(1920, 1080);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(2);
        mediaRecorder.setAudioEncodingBitRate(VideoUtils.getAudioBitrate("Extreme"));
        mediaRecorder.setAudioSamplingRate(VideoUtils.getAudioSample("Medium"));

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        previewSize = getSizeForPreviewDependingOnImageSize(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888),baseCameraHolder.characteristics, videowidth, videoheight);

        SurfaceTexture texture = baseCameraHolder.textureView.getSurfaceTexture();
        assert texture != null;

        texture.setDefaultBufferSize(1920,1080);


        baseCameraHolder.previewsurface = new Surface(texture);
        try {
            baseCameraHolder.mPreviewRequestBuilder = baseCameraHolder.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            baseCameraHolder.cameraChangedListner.onCameraError("MediaRecorder Prepare failed");
            return;
        }

        List<Surface> surfaces = new ArrayList<Surface>();

        Surface previewSurface = new Surface(texture);
        surfaces.add(previewSurface);
        baseCameraHolder.mPreviewRequestBuilder.addTarget(previewSurface);

        Surface recorderSurface = mediaRecorder.getSurface();
        surfaces.add(recorderSurface);
        baseCameraHolder.mPreviewRequestBuilder.addTarget(recorderSurface);

        baseCameraHolder.configureTransform(videowidth, videoheight,displaySize);

        try {

            baseCameraHolder.mCameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    baseCameraHolder.mCaptureSession = cameraCaptureSession;
                    baseCameraHolder.updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, null);

        } catch (CameraAccessException e)
        {};




    }
}
