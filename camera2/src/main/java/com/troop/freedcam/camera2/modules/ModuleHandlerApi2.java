package com.troop.freedcam.camera2.modules;

import android.os.Handler;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.IntervalModule;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    BaseCameraHolderApi2 cameraHolder;

    private static String TAG = "freedcam.ModuleHandler";
    AppSettingsManager appSettingsManager;
    Handler backgroundHandler;

    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler backgroundHandler)
    {
        super(cameraHolder,appSettingsManager);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        this.backgroundHandler = backgroundHandler;
        initModules(appSettingsManager);
    }


    protected void initModules(AppSettingsManager appSettingsManager)
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, appSettingsManager, moduleEventHandler, backgroundHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalModule intervalModule = new IntervalModule(cameraHolder,appSettingsManager,moduleEventHandler,pictureModuleApi2);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(cameraHolder,appSettingsManager,moduleEventHandler);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
