package com.freedcam.apis.camera2.camera2.modules;

import android.content.Context;

import com.freedcam.apis.i_camera.AbstractCameraHolder;
import com.freedcam.apis.i_camera.modules.AbstractModule;
import com.freedcam.apis.i_camera.modules.IntervalModule;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 26.02.2016.
 */
public class IntervalApi2 extends IntervalModule implements I_PreviewWrapper
{
    private PictureModuleApi2 picModule;
    public IntervalApi2(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, AbstractModule picModule, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler, picModule,context,appSettingsManager);
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
