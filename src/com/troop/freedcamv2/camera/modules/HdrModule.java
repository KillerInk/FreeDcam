package com.troop.freedcamv2.camera.modules;

import com.troop.freedcamv2.camera.BaseCameraHolder;


import com.troop.freedcamv2.ui.AppSettingsManager;

/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends AbstractModule {
    public HdrModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        name = ModuleHandler.MODULE_HDR;
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END

}
