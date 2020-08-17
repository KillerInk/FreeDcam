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
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Camera2EXT.OpModes;
import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.record.VideoRecorder;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.modes.VideoProfilesApi2;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.file.holder.BaseHolder;
import freed.file.holder.FileHolder;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
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
    protected ImageReader PicReader;

    private VideoRecorder videoRecorder;
    protected Camera2Fragment cameraUiWrapper;

    public VideoModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        this.cameraUiWrapper = (Camera2Fragment) cameraUiWrapper;
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

        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);
        VideoProfilesApi2 profilesApi2 = (VideoProfilesApi2) parameterHandler.get(SettingKeys.VideoProfiles);
        currentVideoProfile = profilesApi2.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).get());
        if (currentVideoProfile == null) {
            currentVideoProfile = profilesApi2.GetCameraProfile(SettingsManager.get(SettingKeys.VideoProfiles).getValues()[0]);
        }
        parameterHandler.get(SettingKeys.VideoProfiles).fireStringValueChanged(currentVideoProfile.ProfileName);
        Log.d(TAG, "Create VideoRecorder");
        videoRecorder = new VideoRecorder(cameraUiWrapper, new MediaRecorder());
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
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
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
        cameraUiWrapper.captureSessionHandler.RemoveSurface(recorderSurface);
        recorderSurface = null;
        isRecording = false;

        changeCaptureState(ModuleHandlerAbstract.CaptureStates.video_recording_stop);

        fireOnWorkFinish(recordingFile);
        //TODO fix mediascan
        //FreedApplication.ScanFile(recordingFile);

        cameraUiWrapper.captureSessionHandler.CreateCaptureSession();
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
        final int w, h, or;
        w = previewSize.getWidth();
        h = previewSize.getHeight();
        if (SettingsManager.get(SettingKeys.orientationHack).get())
            orientation = (360 + orientation+180)%360;
        or = orientation;
        mainHandler.post(() -> cameraUiWrapper.captureSessionHandler.SetTextureViewSize(w, h, or, false));

        SurfaceTexture texture = cameraUiWrapper.getTexturView().getSurfaceTexture();
        texture.setDefaultBufferSize(w, h);
        previewsurface = new Surface(texture);

        cameraUiWrapper.captureSessionHandler.AddSurface(previewsurface, true);



        if(currentVideoProfile.ProfileName.contains("2EIS2") || currentVideoProfile.ProfileName.contains("3EIS3")||currentVideoProfile.ProfileName.contains("xEISx")){
            PicReader = ImageReader.newInstance(320, 240, ImageFormat.YUV_420_888, 2);
            PicReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    reader.acquireLatestImage().close();
                }
            },null);
            cameraUiWrapper.captureSessionHandler.AddSurface(PicReader.getSurface(), false);
        }
        else if (PicReader != null)
        {
            PicReader.close();
            PicReader = null;
        }

        if (SettingsManager.get(SettingKeys.ENABLE_VIDEO_OPMODE).get()) {
            if (currentVideoProfile.ProfileName.contains("2EIS2")) {
                cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_RealTimeEIS);
            } else if (currentVideoProfile.ProfileName.contains("3EIS3")) {
                cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_LookAheadEIS);
            } else if (currentVideoProfile.ProfileName.contains("xEISx")) {
                cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_VidHanceEIS60);
            } else if (currentVideoProfile.ProfileName.contains("3hdr")) {
                cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.qbcHDR);
            } else {
                cameraUiWrapper.captureSessionHandler.setOPMODE(0);
            }
            cameraUiWrapper.captureSessionHandler.CreateCustomCaptureSession();
        }
        else
            cameraUiWrapper.captureSessionHandler.CreateCaptureSession();

        Range<Integer> fps = new Range<>(currentVideoProfile.videoFrameRate, currentVideoProfile.videoFrameRate);
        cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps,true);

        if(currentVideoProfile.ProfileName.contains("2EIS2") || currentVideoProfile.ProfileName.contains("3EIS3")||currentVideoProfile.ProfileName.contains("xEISx")) {
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.eis_mode, (byte) 1,true);
        }

    }



    public Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
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
        String file = cameraUiWrapper.getActivityInterface().getFileListController().getStorageFileManager().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), ".mp4");
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

        if (SettingsManager.get(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_))){
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
            recorderSurface = videoRecorder.getSurface();
            cameraUiWrapper.captureSessionHandler.AddSurface(recorderSurface, true);

            if (SettingsManager.get(SettingKeys.ENABLE_VIDEO_OPMODE).get()) {
                if (currentVideoProfile.ProfileName.contains("2EIS2")) {
                    cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_RealTimeEIS);
                } else if (currentVideoProfile.ProfileName.contains("3EIS3")) {
                    cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_LookAheadEIS);
                } else if (currentVideoProfile.ProfileName.contains("xEISx")) {
                    cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.OP_VidHanceEIS60);
                } else if (currentVideoProfile.ProfileName.contains("3hdr")) {
                    cameraUiWrapper.captureSessionHandler.setOPMODE(OpModes.qbcHDR);
                } else {
                    cameraUiWrapper.captureSessionHandler.setOPMODE(0);
                }
                cameraUiWrapper.captureSessionHandler.CreateCustomCaptureSession(previewrdy);
            }
            else
            {
                if (currentVideoProfile.Mode != VideoMediaProfile.VideoMode.Highspeed)
                    cameraUiWrapper.captureSessionHandler.CreateCaptureSession(previewrdy);
                else
                    cameraUiWrapper.captureSessionHandler.CreateHighSpeedCaptureSession(previewrdy);
            }


            Range<Integer> fps = new Range<>(currentVideoProfile.videoFrameRate, currentVideoProfile.videoFrameRate);
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps,true);

            if (SettingsManager.get(SettingKeys.ENABLE_VIDEO_OPMODE).get()) {
                if (currentVideoProfile.ProfileName.contains("2EIS2") || currentVideoProfile.ProfileName.contains("3EIS3") || currentVideoProfile.ProfileName.contains("xEISx")) {
                    cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.eis_mode, (byte) 1,true);
                }
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

    private final StateCallback previewrdy = new StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            cameraUiWrapper.captureSessionHandler.SetCaptureSession(cameraCaptureSession);
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

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {
            Log.d(TAG, "Failed to Config CaptureSession");
            UserMessageHandler.sendMSG("Failed to Config CaptureSession",false);
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
}
