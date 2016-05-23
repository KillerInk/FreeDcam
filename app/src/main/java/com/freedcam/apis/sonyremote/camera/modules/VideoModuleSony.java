package com.freedcam.apis.sonyremote.camera.modules;

import android.content.Context;

import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.sonyremote.camera.CameraHolderSony;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 08.06.2015.
 */
public class VideoModuleSony extends AbstractModule implements I_CameraStatusChanged
{
    private static String TAG = VideoModuleSony.class.getSimpleName();
    private CameraHolderSony cameraHolder;

    public VideoModuleSony(CameraHolderSony cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler, context,appSettingsManager);
        this.name = AbstractModuleHandler.MODULE_VIDEO;
        this.cameraHolder = cameraHandler;

    }

    @Override
    public String LongName() {
        return "Movie";
    }

    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
        {
            cameraHolder.StartRecording();
        }
        else cameraHolder.StopRecording();
        return true;
    }

    @Override
    public void InitModule() {
        cameraHolder.CameraStatusListner = this;
        onCameraStatusChanged(cameraHolder.GetCameraStatus());
    }

    @Override
    public void DestroyModule() {

    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        if (status.equals("IDLE") && isWorking)
        {
            this.isWorking = false;
            changeWorkState(AbstractModuleHandler.CaptureModes.video_recording_stop);
        }
        else if (status.equals("MovieWaitRecStart") && !isWorking) {
            this.isWorking = true;
            changeWorkState(AbstractModuleHandler.CaptureModes.video_recording_start);
        }

    }
}
