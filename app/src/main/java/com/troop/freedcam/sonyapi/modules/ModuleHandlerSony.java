package com.troop.freedcam.sonyapi.modules;

import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
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
        super(cameraHolder, appSettingsManager);
        this.cameraHolder = cameraHolder;
    }

    protected void initModules()
    {

        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
