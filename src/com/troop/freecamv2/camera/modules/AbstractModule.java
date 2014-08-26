package com.troop.freecamv2.camera.modules;

import com.troop.freecamv2.camera.BaseCameraHolder;

import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecamv2.ui.AppSettingsManager;

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
    protected ModuleEventHandler eventHandler;

    public AbstractModule(BaseCameraHolder cameraHandler, SoundPlayer soundPlayer, AppSettingsManager Settings, ModuleEventHandler eventHandler)
    {
        this.baseCameraHolder = cameraHandler;
        this.soundPlayer = soundPlayer;
        this.Settings = Settings;
        this.eventHandler = eventHandler;
    }

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

}
