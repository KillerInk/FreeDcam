package com.freedcam.apis.camera2.camera2.modules;

import android.content.Context;

import com.freedcam.apis.camera2.camera2.BaseCameraHolderApi2;
import com.freedcam.apis.i_camera.AbstractCameraHolder;
import com.freedcam.apis.i_camera.modules.AbstractModuleHandler;
import com.freedcam.apis.i_camera.modules.IntervalModule;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    private BaseCameraHolderApi2 cameraHolder;

    private static String TAG = "freedcam.ModuleHandler";
    private AppSettingsManager appSettingsManager;


    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder,context,appSettingsManager);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        initModules();
    }

    protected void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, moduleEventHandler,context,appSettingsManager);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalModule intervalModule = new IntervalApi2(cameraHolder,moduleEventHandler,pictureModuleApi2,context,appSettingsManager);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(cameraHolder,moduleEventHandler,context,appSettingsManager);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }
}
