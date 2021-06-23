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
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;

import com.troop.freedcam.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.opcodeprocessor.OpcodeProcessor;
import freed.cam.apis.camera2.modules.opcodeprocessor.OpcodeProcessorFactory;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.cam.event.capture.CaptureStates;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.OrientationUtil;
import freed.utils.PermissionManager;
import freed.utils.VideoMediaProfile;

/**
 * Created by troop on 26.11.2015.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class VideoModuleApi2 extends AbstractModuleApi2 {
    private final String TAG = VideoModuleApi2.class.getSimpleName();
    private boolean isRecording;
    private boolean isLowStorage;
    private VideoMediaProfile currentVideoProfile;
    private Surface previewsurface;
    private Surface recorderSurface;
    private BaseHolder recordingFile;
    //protected ContinouseYuvCapture PicReader;
    protected ImageReader PicReader;

    private VideoRecorder videoRecorder;
    //private Surface inputSurface;

    private OpcodeProcessor opcodeProcessor;
    private OpCodes active_op = OpCodes.off;
    private PermissionManager permissionManager;
    private UserMessageHandler userMessageHandler;

    public VideoModuleApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        permissionManager = ActivityAbstract.permissionManager();
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        name = FreedApplication.getStringFromRessources(R.string.module_video);
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork() {
        if (permissionManager.isPermissionGranted(PermissionManager.Permissions.RecordAudio))
            startStopRecording();
        else
            permissionManager.requestPermission(PermissionManager.Permissions.RecordAudio);
    }

    private void startStopRecording() {
        mBackgroundHandler.post(() -> {
            if (!isRecording && !isLowStorage) {
                startRecording(true);
            } else if (isRecording) {
                stopRecording(true);
            }
            if (isLowStorage) {
                userMessageHandler.sendMSG("Can't Record due to low storage space. Free some and try again.", false);
            }
        });

    }

    @Override
    public void IsLowStorage(Boolean x) {
        isLowStorage = x;
    }

    @Override
    public void InitModule() {
        Log.d(TAG, "InitModule");
        super.InitModule();
        changeCaptureState(CaptureStates.video_recording_stop);
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) parameterHandler.get(SettingKeys.VideoProfiles);
        currentVideoProfile = profilesApi2.GetCameraProfile(settingsManager.get(SettingKeys.VideoProfiles).get());
        if (currentVideoProfile == null) {
            currentVideoProfile = settingsManager.getMediaProfiles().get(0);
        }
        Log.d(TAG, "VideoMediaProfile: " + currentVideoProfile.getXmlString());
        parameterHandler.get(SettingKeys.VideoProfiles).fireStringValueChanged(currentVideoProfile.ProfileName);

        active_op = OpCodes.get(currentVideoProfile.opcode);
        Log.d(TAG, "Opcode " + active_op.name() + ":" +active_op.GetInt());
        if (Build.VERSION.SDK_INT >= VERSION_CODES.N && active_op != OpCodes.off) {
            opcodeProcessor = OpcodeProcessorFactory.getOpCodeProcessor(active_op, cameraUiWrapper.captureSessionHandler);
        }

        Log.d(TAG, "Create VideoRecorder");
        videoRecorder = new VideoRecorder(cameraUiWrapper, new MediaRecorder());

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            inputSurface = MediaCodec.createPersistentInputSurface();
            videoRecorder.setInputSurface(inputSurface);
        }*/

        startPreview();
        if (parameterHandler.get(SettingKeys.PictureFormat) != null)
            parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).setViewState(AbstractParameter.ViewState.Hidden);
    }

    @Override
    public void DestroyModule() {
        if (parameterHandler.get(SettingKeys.PictureFormat) != null)
            parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).setViewState(AbstractParameter.ViewState.Visible);
        if (isRecording)
            stopRecording(true);
        Log.d(TAG, "DestroyModule");
        try {
            videoRecorder.release();
        } catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }
        if (PicReader != null)
        {
            Log.d(TAG, "Close Opcode PicReader");
            Log.d(TAG, "remove surface picture");
            cameraUiWrapper.captureSessionHandler.RemoveSurface(PicReader.getSurface());
            PicReader.close();
            PicReader = null;
        }
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        previewController.close();
        //((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(null);
        videoRecorder.release();
        videoRecorder = null;
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

    private void startRecording(boolean addSurface) {
        changeCaptureState(CaptureStates.video_recording_start);
        Log.d(TAG, "startRecording");
        startPreviewVideo(addSurface);
    }

    private void stopRecording(boolean removesurface) {
        Log.d(TAG, "stopRecording");
        videoRecorder.stop();

        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        if (opcodeProcessor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            opcodeProcessor.stopRecording();
        Log.d(TAG,"remove surface record");
        if (removesurface)
            cameraUiWrapper.captureSessionHandler.RemoveSurface(recorderSurface);
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();

        recorderSurface = null;
        isRecording = false;

        changeCaptureState(CaptureStates.video_recording_stop);

        fireOnWorkFinish(recordingFile);
    }

    @Override
    public void startPreview() {
        Size previewSize;
        if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed) {
            if (currentVideoProfile.videoFrameWidth > 3840) {
                previewSize = new Size(1280, 720);
            } else {

                previewSize = getSizeForPreviewDependingOnImageSize(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), cameraHolder.characteristics, currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
            }
        } else {
            if (currentVideoProfile.videoFrameWidth > 3840) {
                previewSize = new Size(3840, 2160);
            } else {
                previewSize = new Size(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
            }
        }

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int orientation = 0;
        int orientationToSet = (360 + sensorOrientation) % 360;
        if (settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name())) {
            Log.d(TAG, "RenderScriptPreview");
            int rotation = 0;
            switch (orientationToSet)
            {
                case 90:
                    rotation = 0;
                    break;
                case 180:
                    rotation =90;
                    break;
                case 270: rotation = 180;
                    break;
                case 0: rotation = 270;
                    break;
            }
            final int or = OrientationUtil.getOrientation(rotation);

            Log.d(TAG, "rotation to set : " + or);
            int w = previewSize.getWidth();
            int h = previewSize.getHeight();
            if (!settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
                if (or == 90 || or == 270) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            else
            {
                if (or == 0 || or == 180) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            int finalW = w;
            int finalH = h;
            mainHandler.post(() -> previewController.setRotation(finalW, finalH,or));
            SurfaceTexture texture = previewController.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewsurface = new Surface(texture);

            previewController.setOutputSurface(previewsurface);
            previewController.setSize(previewSize.getWidth(),previewSize.getHeight());

            Surface camerasurface = previewController.getInputSurface();
            Log.d(TAG, "Add preview surface RS");
            cameraUiWrapper.captureSessionHandler.AddSurface(camerasurface, true);
            previewController.start();
        }
        else {
            switch (orientationToSet) {
                case 90:
                    orientation = 270;
                    break;
                case 180:
                    orientation = 180;
                    break;
                case 270:
                    orientation = 270;
                    break;
                case 0:
                    orientation = 180;
                    break;
            }
            int w, h, or;
            w = previewSize.getWidth();
            h = previewSize.getHeight();
            or = OrientationUtil.getOrientation(orientation);
            if (!settingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
                if (or == 0 || or == 180) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            else
            {
                if (or == 90 || or == 270) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            int finalW = w;
            int finalH = h;
            mainHandler.post(() -> previewController.setRotation(finalW, finalH, or));
            previewController.setSize(finalW, finalH);
            SurfaceTexture texture = previewController.getSurfaceTexture();
            texture.setDefaultBufferSize(w, h);
            previewsurface = new Surface(texture);
            Log.d(TAG, "add preview surface normal");
            cameraUiWrapper.captureSessionHandler.AddSurface(previewsurface, true);
        }

        if (active_op != OpCodes.off && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Create Opcode PicReader");
            PicReader = ImageReader.newInstance(320, 240, ImageFormat.JPEG, 3);
            Log.d(TAG, "add surface picture");
            cameraUiWrapper.captureSessionHandler.AddSurface(PicReader.getSurface(), false);

            Log.d(TAG, "Create Preview OpCodeSession" + active_op.name() + ":" + active_op.GetInt());
            applyQcomSettingsToSession(active_op);
            opcodeProcessor.createOpCodeSession(previewSessionCallback);
        } else {
            Log.d(TAG, "Create normal Preview Session");
            cameraUiWrapper.captureSessionHandler.CreateCaptureSession(previewSessionCallback);
        }

    }

    public Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= cameraUiWrapper.captureSessionHandler.displaySize.x && s.getHeight() <= cameraUiWrapper.captureSessionHandler.displaySize.y && (double)s.getWidth()/s.getHeight() == ratio)
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



    private void startPreviewVideo(boolean addsurface)
    {
        String file = fileListController.getNewFilePath(settingsManager.GetWriteExternal(), ".mp4");
        recordingFile = new FileHolder(new File(file),settingsManager.GetWriteExternal());
        //TODO handel uri based holder
        videoRecorder.setRecordingFile(((FileHolder)recordingFile).getFile());
        videoRecorder.setErrorListener((mr, what, extra) -> {
            Log.e(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            changeCaptureState(CaptureStates.video_recording_stop);
            if (what == MediaRecorder.MEDIA_ERROR_SERVER_DIED)
                Log.e(TAG, "MEDIA_ERROR_SERVER_DIED");
            else if (what == MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN)
                Log.e(TAG, "MEDIA_RECORDER_ERROR_UNKNOWN");
            else if (what == 200)
                Log.e(TAG, "MEDIA_RECORDER_ERROR_VIDEO_NO_SYNC_FRAME");
            else if (what == 1000)
                Log.e(TAG, "MEDIA_RECORDER_TRACK_ERROR_LIST_END");
        });

        videoRecorder.setInfoListener((mr, what, extra) -> {
            Log.d(TAG, "onMediaRecorderInfo what:" + what +" extra:" + extra);
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING)
            {
                Log.d(TAG, "MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_APPROACHING");
                setNextFile();
            }
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
            {
                Log.d(TAG, "MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED");
                recordnextFile(mr);
            }
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED && extra == 0)
            {
                Log.d(TAG, "MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED");
                recordnextFile(mr);
            }
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED)
            {
                Log.d(TAG, "MediaRecorder.MEDIA_RECORDER_INFO_NEXT_OUTPUT_FILE_STARTED");
            }
        });

        if (settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_))){
            Location location = locationManager.getCurrentLocation();
            if (location != null)
                videoRecorder.setLocation(location);
        }
        else
            videoRecorder.setLocation(null);

        videoRecorder.setCurrentVideoProfile(currentVideoProfile);
        videoRecorder.setVideoSource(VideoSource.SURFACE);
        videoRecorder.setOrientation(0);

        if(videoRecorder.prepare()) {
            Log.d(TAG,"video recorder prepared");
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                recorderSurface = inputSurface;
            }
            else {*/
                recorderSurface = videoRecorder.getSurface();
            //}
            Log.d(TAG, "add surface record");
            if (addsurface)
                cameraUiWrapper.captureSessionHandler.AddSurface(recorderSurface, true);
            applyQcomSettingsToSession(active_op);

            if (active_op != OpCodes.off && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                //opcodeProcessor = OpcodeProcessorFactory.getOpCodeProcessor(active_op, cameraUiWrapper.captureSessionHandler);
                opcodeProcessor.createOpCodeSession(recordingSessionCallback);
            }
            else
            {
                if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed)
                    cameraUiWrapper.captureSessionHandler.CreateCaptureSession(recordingSessionCallback);
                else
                    cameraUiWrapper.captureSessionHandler.CreateHighSpeedCaptureSession(recordingSessionCallback);
            }

        }
        else{
            Log.d(TAG, "failed to prepare Video recorder");
            isRecording = false;
            changeCaptureState(CaptureStates.video_recording_stop);
        }
    }

    private void recordnextFile(MediaRecorder mr) {

        if (Build.VERSION.SDK_INT < VERSION_CODES.O) {
            Log.d(TAG, "recordnextFile");
            stopRecording(false);
            startRecording(false);
        }
    }

    private void setNextFile()
    {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            Log.d(TAG, "setNextFile");
            String file = fileListController.getNewFilePath(settingsManager.GetWriteExternal(), ".mp4");
            try {
                videoRecorder.setNextFile(new File(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final StateCallback previewSessionCallback = new StateCallback() {

        private final String TAG = VideoModuleApi2.this.TAG + ".previewSessionCallback";

        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigured");
            cameraUiWrapper.captureSessionHandler.SetCaptureSession(session);

            cameraUiWrapper.getParameterHandler().SetAppSettingsToParameters();
            if (opcodeProcessor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d(TAG, "opcodeProcessor.prepareRecording");
                opcodeProcessor.prepareRecording();
            }

            Range<Integer> fps = new Range<>(currentVideoProfile.videoFrameRate, currentVideoProfile.videoFrameRate);
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps,false);
            cameraUiWrapper.captureSessionHandler.StartRepeatingCaptureSession();

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed");
        }

        @Override
        public void onActive(@NonNull CameraCaptureSession session) {
            super.onActive(session);
            Log.d(TAG, "onActive");
        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            super.onClosed(session);
            Log.d(TAG, "onClosed");
        }

        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
            super.onReady(session);
            Log.d(TAG, "onReady");
        }

        @Override
        public void onSurfacePrepared(@NonNull CameraCaptureSession session, @NonNull Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG, "onSurfacePrepared");
        }
    };

    private final StateCallback recordingSessionCallback = new StateCallback()
    {

        private final String TAG = VideoModuleApi2.this.TAG + ".recordingSessionCallback";

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigured");
            cameraUiWrapper.captureSessionHandler.SetCaptureSession(cameraCaptureSession);
            if (opcodeProcessor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                opcodeProcessor.startRecording();

            Range<Integer> fps = new Range<>(currentVideoProfile.videoFrameRate, currentVideoProfile.videoFrameRate);
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps,true);

            if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed) {
                cameraUiWrapper.captureSessionHandler.StartRepeatingCaptureSession();
            }
            else
            {
                int index = getHFRResIndex();
                cameraHolder.setOpModeForHFRVideoStreamToActiveCamera(index);
                cameraUiWrapper.captureSessionHandler.StartHighspeedCaptureSession();
            }

            videoRecorder.start();
            isRecording = true;
           /* mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {

                }
            });*/
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigureFailed");
            userMessageHandler.sendMSG("Failed to Config CaptureSession",false);
            stopRecording(true);
        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            super.onClosed(session);
            Log.d(TAG, "onClosed");
            startPreview();
        }

        @Override
        public void onActive(@NonNull CameraCaptureSession session) {
            super.onActive(session);
            Log.d(TAG, "onActive");
        }

        @Override
        public void onReady(@NonNull CameraCaptureSession session) {
            super.onReady(session);
            Log.d(TAG, "onReady");
        }

        @Override
        public void onSurfacePrepared(@NonNull CameraCaptureSession session, @NonNull Surface surface) {
            super.onSurfacePrepared(session, surface);
            Log.d(TAG, "onSurfacePrepared");
        }
    };

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {

    }

    private int getHFRResIndex()
    {
        int index = -1;
        StreamConfigurationMap smap = cameraHolder.characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size sizes[] = smap.getHighSpeedVideoSizes();
        for (int i = 0; i < sizes.length; i++)
        {
            if (sizes[i].getWidth() == currentVideoProfile.videoFrameWidth && sizes[i].getHeight() == currentVideoProfile.videoFrameHeight)
                index = i;
        }
        return index;
    }

    private void applyQcomSettingsToSession(OpCodes active_op) {

        setQcomVideoHdr();
        if (opcodeProcessor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            opcodeProcessor.applyOpCodeToSession();
    }

    private void setQcomVideoHdr()
    {
        if (settingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).isSupported())
        {
            switch (currentVideoProfile.videoHdr)
            {
                case 0:
                    cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.HDR10_VIDEO, CaptureRequestQcom.HDR10_VIDEO_OFF, false);
                    break;
                case 1:
                    cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.HDR10_VIDEO, CaptureRequestQcom.HDR10_VIDEO_HLG, false);
                    break;
                case 2:
                    cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.HDR10_VIDEO, CaptureRequestQcom.HDR10_VIDEO_PQ, false);
                    break;
            }
        }
    }
}
