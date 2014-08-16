package com.troop.freecam.camera.modules;

import com.troop.freecam.camera.BaseCameraHolder;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;

/**
 * Created by troop on 16.08.2014.
 */
public class HdrModule extends AbstractModule {
    public HdrModule(BaseCameraHolder cameraHandler, SoundPlayer soundPlayer, AppSettingsManager Settings) {
        super(cameraHandler, soundPlayer, Settings);
        name = "HdrModule";
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
