package com.troop.freedcam.camera.modules;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.I_Module;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class AbstractModule implements I_Module
{
    protected BaseCameraHolder baseCameraHolder;
    protected AppSettingsManager Settings;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    protected String name;

    protected ModuleEventHandler eventHandler;

    public AbstractModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler)
    {
        this.baseCameraHolder = cameraHandler;
        this.Settings = Settings;
        this.eventHandler = eventHandler;
        this.ParameterHandler = baseCameraHolder.ParameterHandler;
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

    @Override
    public void LoadNeededParameters() {

    }

    @Override
    public void UnloadNeededParameters() {

    }
}
