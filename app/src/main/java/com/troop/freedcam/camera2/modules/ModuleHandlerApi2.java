package com.troop.freedcam.camera2.modules;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.HdrModule;
import com.troop.freedcam.camera.modules.LongExposureModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.modules.PictureModule;
import com.troop.freedcam.camera.modules.PictureModuleO3D;
import com.troop.freedcam.camera.modules.PictureModuleThl5000;
import com.troop.freedcam.camera.modules.VideoModule;
import com.troop.freedcam.camera.modules.VideoModuleG3;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    BaseCameraHolderApi2 cameraHolder;

    final String TAG = "freedcam.ModuleHandler";

    public  ModuleHandlerApi2 (I_CameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        moduleList  = new HashMap<String, AbstractModule>();
        moduleEventHandler = new ModuleEventHandler();
        PictureModules = new ArrayList<String>();
        PictureModules.add(ModuleHandler.MODULE_PICTURE);
        PictureModules.add(ModuleHandler.MODULE_BURST);
        PictureModules.add(ModuleHandler.MODULE_HDR);
        //PictureModules.add();
        VideoModules = new ArrayList<String>();
        VideoModules.add(ModuleHandler.MODULE_VIDEO);
        AllModules = new ArrayList<String>();
        AllModules.add(ModuleHandler.MODULE_ALL);
        LongeExpoModules = new ArrayList<String>();
        LongeExpoModules.add(ModuleHandler.MODULE_LONGEXPO);
        initModules();

    }


    @Override
    public void SetModule(String name) {
        super.SetModule(name);
    }

    @Override
    public String GetCurrentModuleName() {
        return super.GetCurrentModuleName();
    }

    @Override
    public AbstractModule GetCurrentModule() {
        return super.GetCurrentModule();
    }

    @Override
    public boolean DoWork() {
        return super.DoWork();
    }

    private void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
