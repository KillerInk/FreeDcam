package com.troop.freedcam.camera2.modules;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.IntervalModule;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends IntervalModule implements I_PreviewWrapper
{
    private PictureModuleApi2 picModule;
    public IntervalApi2(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, AbstractModule picModule) {
        super(cameraHandler, eventHandler, picModule);
        this.picModule = (PictureModuleApi2)picModule;
    }


    @Override
    public void startPreview() {
        picModule.startPreview();
    }

    @Override
    public void stopPreview() {
        picModule.stopPreview();
    }

    @Override
    public void LoadNeededParameters()
    {
        super.LoadNeededParameters();
        picModule.LoadNeededParameters();
    }

    @Override
    public void UnloadNeededParameters() {
        picModule.UnloadNeededParameters();
    }
}
