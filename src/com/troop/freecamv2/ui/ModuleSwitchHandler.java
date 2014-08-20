package com.troop.freecamv2.ui;

import com.troop.freecamv2.camera.CameraUiWrapper;
import com.troop.freecamv2.camera.modules.ModuleHandler;

import java.util.HashMap;

/**
 * Created by troop on 20.08.2014.
 */
public class ModuleSwitchHandler
{

    MainActivity_v2 activity;
    CameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    ModuleHandler moduleHandler;
    HashMap<String,String> modules;

    public ModuleSwitchHandler(MainActivity_v2 activity, CameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        this.moduleHandler = cameraUiWrapper.moduleHandler;
        modules = new HashMap<String, String>();
        modules.put("Picture", ModuleHandler.MODULE_PICTURE);
        modules.put("Video", ModuleHandler.MODULE_VIDEO);
        modules.put("HDR", ModuleHandler.MODULE_HDR);
    }
}
