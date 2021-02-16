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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.media.ImageReader;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.opcodeprocessor.OpcodeProcessor;
import freed.cam.apis.camera2.modules.opcodeprocessor.OpcodeProcessorFactory;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.renderscript.RenderScriptProcessor;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
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

    public VideoModuleApi2(Camera2Fragment cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = FreedApplication.getStringFromRessources(R.string.module_video);
        videoRecorder = new VideoRecorder(cameraUiWrapper, new MediaRecorder());
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork() {
        if (cameraUiWrapper.getActivityInterface().getPermissionManager().isPermissionGranted(PermissionManager.Permissions.RecordAudio))
            startStopRecording();
        else
            cameraUiWrapper.getActivityInterface().getPermissionManager().requestPermission(PermissionManager.Permissions.RecordAudio);
    }

    private void startStopRecording() {
        mBackgroundHandler.post(() -> {
            if (!isRecording && !isLowStorage) {
                startRecording();
            } else if (isRecording) {
                stopRecording();
            }
            if (isLowStorage) {
                UserMessageHandler.sendMSG("Can't Record due to low storage space. Free some and try again.", false);
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
        //((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(new MyRSErrorHandler());
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) parameterHandler.get(SettingKeys.VideoProfiles);
        currentVideoProfile = profilesApi2.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        if (currentVideoProfile == null) {
            currentVideoProfile = SettingsManager.getInstance().getMediaProfiles().get(0);
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

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            stopRecording();
        Log.d(TAG, "DestroyModule");
        try {
            videoRecorder.release();
        } catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }
        if (PicReader != null)
        {
            Log.d(TAG, "Close Opcode PicReader");
            cameraUiWrapper.captureSessionHandler.RemoveSurface(PicReader.getSurface());
            PicReader.close();
            PicReader = null;
        }
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        cameraUiWrapper.getPreview().close();
        //((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(null);
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

    private void startRecording() {
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_start);
        Log.d(TAG, "startRecording");
        startPreviewVideo();
    }

    private void stopRecording() {
        Log.d(TAG, "stopRecording");
        videoRecorder.stop();

        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        if (opcodeProcessor != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            opcodeProcessor.stopRecording();

        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        cameraUiWrapper.captureSessionHandler.RemoveSurface(recorderSurface);
        recorderSurface = null;
        isRecording = false;

        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);

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
        if (SettingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.RenderScript.name())) {
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
            if (!SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
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
            mainHandler.post(() -> cameraUiWrapper.getPreview().setRotation(finalW, finalH,or));
            SurfaceTexture texture = cameraUiWrapper.getPreview().getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewsurface = new Surface(texture);

            cameraUiWrapper.getPreview().setOutputSurface(previewsurface);
            cameraUiWrapper.getPreview().setSize(previewSize.getWidth(),previewSize.getHeight());

            Surface camerasurface = cameraUiWrapper.getPreview().getInputSurface();
            cameraUiWrapper.captureSessionHandler.AddSurface(camerasurface, true);
            cameraUiWrapper.getPreview().start();
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
            if (!SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
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
            mainHandler.post(() -> cameraUiWrapper.getPreview().setRotation(finalW, finalH, or));

            SurfaceTexture texture = cameraUiWrapper.getPreview().getSurfaceTexture();
            texture.setDefaultBufferSize(w, h);
            previewsurface = new Surface(texture);

            cameraUiWrapper.captureSessionHandler.AddSurface(previewsurface, true);
        }

        if (active_op != OpCodes.off && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(TAG, "Create Opcode PicReader");
            PicReader = ImageReader.newInstance(320, 240, ImageFormat.JPEG, 3);
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



    private void startPreviewVideo()
    {
        String file = cameraUiWrapper.getActivityInterface().getFileListController().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), ".mp4");
        recordingFile = new FileHolder(new File(file),SettingsManager.getInstance().GetWriteExternal());
        //TODO handel uri based holder
        videoRecorder.setRecordingFile(((FileHolder)recordingFile).getFile());
        videoRecorder.setErrorListener((mr, what, extra) -> {
            Log.d(TAG, "error MediaRecorder:" + what + "extra:" + extra);
            changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        });

        videoRecorder.setInfoListener((mr, what, extra) -> {
            if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
            {
                recordnextFile(mr);
            }
            else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
            {
                recordnextFile(mr);
            }
        });

        if (SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_))){
            Location location = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
            if (location != null)
                videoRecorder.setLocation(location);
        }
        else
            videoRecorder.setLocation(null);

        videoRecorder.setCurrentVideoProfile(currentVideoProfile);
        videoRecorder.setVideoSource(VideoSource.SURFACE);
        videoRecorder.setOrientation(0);

        if(videoRecorder.prepare()) {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                recorderSurface = inputSurface;
            }
            else {*/
                recorderSurface = videoRecorder.getSurface();
            //}
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
            isRecording = false;
            changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        }
    }

    private void recordnextFile(MediaRecorder mr) {
        stopRecording();
        startRecording();
    }

    private final StateCallback previewSessionCallback = new StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigured Preview Session");
            cameraUiWrapper.captureSessionHandler.SetCaptureSession(session);

            cameraUiWrapper.parametersHandler.SetAppSettingsToParameters();
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
            Log.d(TAG, "Failed to configure Preview Session");
        }
    };

    private final StateCallback recordingSessionCallback = new StateCallback()
    {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "onConfigured Recording Session");
            mBackgroundHandler.post(new Runnable() {
                @Override
                public void run() {
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
                }
            });
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "Failed to Config RecordingSession");
            UserMessageHandler.sendMSG("Failed to Config CaptureSession",false);
            stopRecording();
        }

        @Override
        public void onClosed(@NonNull CameraCaptureSession session) {
            super.onClosed(session);
            startPreview();
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
        if (SettingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).isSupported())
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
