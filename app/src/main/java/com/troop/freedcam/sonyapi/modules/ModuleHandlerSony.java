package com.troop.freedcam.sonyapi.modules;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;

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
        initModules();
    }

    protected void initModules()
    {
        PictureModuleSony pic = new PictureModuleSony(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(pic.ModuleName(), pic);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
