package com.troop.freecam.camera.modules;

import com.troop.freecam.camera.BaseCameraHolder;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class AbstractModule implements I_Module
{
    protected BaseCameraHolder baseCameraHolder;
    protected AppSettingsManager Settings;

    protected boolean isWorking = false;
    protected String name;

    protected SoundPlayer soundPlayer;

    public AbstractModule(BaseCameraHolder cameraHandler, SoundPlayer soundPlayer, AppSettingsManager Settings)
    {
        this.baseCameraHolder = cameraHandler;
        this.soundPlayer = soundPlayer;
        this.Settings = Settings;
    }

    @Override
    public String ModuleName() {
        return null;
    }

    @Override
    public void DoWork()
    {

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

}
