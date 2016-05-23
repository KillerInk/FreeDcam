package com.freedcam.apis.camera2.camera.modules;

import android.content.Context;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.IntervalModule;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.RenderScriptHandler;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    private CameraHolderApi2 cameraHolder;

    private static String TAG = "freedcam.ModuleHandler";
    private AppSettingsManager appSettingsManager;
    private RenderScriptHandler renderScriptHandler;


    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder, Context context, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler)
    {
        super(cameraHolder,context,appSettingsManager);
        this.cameraHolder = (CameraHolderApi2) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        this.renderScriptHandler = renderScriptHandler;
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
        StackingModuleApi2 stackingModuleApi2 = new StackingModuleApi2(cameraHolder,moduleEventHandler,context,appSettingsManager, renderScriptHandler);
        moduleList.put(stackingModuleApi2.ModuleName(), stackingModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }
}
