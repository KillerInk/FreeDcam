package com.troop.freedcam.camera2.modules;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractModuleApi2 extends AbstractModule
{
    protected BaseCameraHolderApi2 baseCameraHolder;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    public String name;

    protected ModuleEventHandler eventHandler;

    public AbstractModuleApi2(BaseCameraHolderApi2 cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler)
    {
        this.baseCameraHolder = cameraHandler;
        this.eventHandler = eventHandler;
        this.ParameterHandler = baseCameraHolder.ParameterHandler;
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork() {
        return true;
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
