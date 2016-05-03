package com.troop.freedcam.camera2.modules;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.IntervalModule;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    private BaseCameraHolderApi2 cameraHolder;

    private static String TAG = "freedcam.ModuleHandler";

    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder)
    {
        super(cameraHolder);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        initModules();
    }

    protected void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, moduleEventHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        IntervalModule intervalModule = new IntervalApi2(cameraHolder,moduleEventHandler,pictureModuleApi2);
        moduleList.put(intervalModule.ModuleName(), intervalModule);
        VideoModuleApi2 videoModuleApi2 = new VideoModuleApi2(cameraHolder,moduleEventHandler);
        moduleList.put(videoModuleApi2.ModuleName(), videoModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }
}
