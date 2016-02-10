package com.troop.freedcam.camera2.modules;

import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 26.11.2015.
 */
public class VideoModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = StringUtils.TAG +PictureModuleApi2.class.getSimpleName();
    private BaseCameraHolderApi2 cameraHolder;
    private boolean isRecording = false;

    public VideoModuleApi2(BaseCameraHolderApi2 cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, eventHandler);
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
        cameraHolder.StopPreview();
        cameraHolder.StartPreview();
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


        cameraHolder.mediaRecorder.start();
        isRecording = true;
        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);
    }

    private void stopRecording()
    {
        Log.d(TAG, "stopRecording");
        cameraHolder.mediaRecorder.stop();
        cameraHolder.mediaRecorder.reset();
        isRecording = false;
        eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        cameraHolder.StartPreview();
    }
}
