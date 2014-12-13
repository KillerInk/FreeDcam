package com.troop.freedcam.sonyapi.modules;

import com.troop.freedcam.camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera2.modules.PictureModuleApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends AbstractModuleHandler
{
    CameraHolderSony cameraHolder;

    public ModuleHandlerSony(CameraHolderSony cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = cameraHolder;
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

        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
