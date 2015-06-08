package com.troop.freedcam.sonyapi.modules;

import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 08.06.2015.
 */
public class VideoModuleSony extends AbstractModule implements I_CameraStatusChanged
{
    private static String TAG = VideoModuleSony.class.getSimpleName();
    CameraHolderSony cameraHolder;

    public VideoModuleSony(CameraHolderSony cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        this.name = AbstractModuleHandler.MODULE_VIDEO;
        this.cameraHolder = cameraHandler;
        cameraHolder.CameraStatusListner = this;
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
    public void DoWork()
    {

    }

    @Override
    public void LoadNeededParameters() {

    }

    @Override
    public void UnloadNeededParameters() {

    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        if (status.equals("IDLE") && isWorking)
        {
            this.isWorking = false;
            workfinished(true);
        }
        else if (status.equals("StillCapturing") && !isWorking) {
            this.isWorking = true;
            workstarted();
        }

    }
}
