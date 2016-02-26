package com.troop.freedcam.camera2.modules;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.IntervalModule;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends IntervalModule implements I_PreviewWrapper
{
    PictureModuleApi2 pictureModuleApi2;
    public IntervalApi2(AbstractCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler, AbstractModule picModule) {
        super(cameraHandler, Settings, eventHandler, picModule);
        this.pictureModuleApi2 = (PictureModuleApi2)picModule;
    }


    @Override
    public void startPreview() {
        pictureModuleApi2.startPreview();
    }

    @Override
    public void stopPreview() {
        pictureModuleApi2.stopPreview();
    }

    @Override
    public void LoadNeededParameters() {
        pictureModuleApi2.LoadNeededParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        pictureModuleApi2.UnloadNeededParameters();
    }
}
