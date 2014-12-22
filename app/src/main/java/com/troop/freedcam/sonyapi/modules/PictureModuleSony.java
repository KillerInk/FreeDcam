package com.troop.freedcam.sonyapi.modules;

import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 22.12.2014.
 */
public class PictureModuleSony extends AbstractModule
{
    CameraHolderSony cameraHolder;
    public PictureModuleSony() {
        super();
    }

    public PictureModuleSony(CameraHolderSony cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_PICTURE;
        this.cameraHolder = cameraHandler;
    }

    @Override
    public String ModuleName() {
        return super.ModuleName();
    }

    @Override
    public void DoWork() {
        super.DoWork();
    }

    @Override
    public boolean IsWorking() {
        return super.IsWorking();
    }

    @Override
    public void LoadNeededParameters() {
        super.LoadNeededParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        super.UnloadNeededParameters();
    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }
}
