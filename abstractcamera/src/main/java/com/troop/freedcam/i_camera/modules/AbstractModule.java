package com.troop.freedcam.i_camera.modules;


import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class AbstractModule implements I_Module
{
    protected AbstractCameraHolder baseCameraHolder;
    protected AppSettingsManager Settings;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    public String name;

    protected ModuleEventHandler eventHandler;
    protected AbstractModuleHandler.I_worker workerListner;
    final static String TAG = AbstractModule.class.getSimpleName();

    public AbstractModule(){};

    public AbstractModule(AbstractCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler)
    {
        this.baseCameraHolder = cameraHandler;
        this.Settings = Settings;
        this.eventHandler = eventHandler;
        this.ParameterHandler = baseCameraHolder.ParameterHandler;
    }

    public void SetWorkerListner(AbstractModuleHandler.I_worker workerListner)
    {
        this.workerListner = workerListner;
    }

    protected void workstarted()
    {
        Log.d(TAG, "work started");
        if (this.workerListner != null)
            workerListner.onWorkStarted();
    }

    protected void workfinished(final boolean finish)
    {
        Log.d(TAG, "work finished");
        if (workerListner != null)
            workerListner.onWorkFinished(finish);
    }

    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public abstract boolean DoWork();

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    /**
     * this gets called when the module gets loaded. set here specific paramerters that are needed by the module
     */
    @Override
    public abstract void LoadNeededParameters();

    /**
     * this gets called when module gets unloaded reset the parameters that where set on LoadNeededParameters
     */
    @Override
    public abstract void UnloadNeededParameters();

    @Override
    public abstract String LongName();

    @Override
    public abstract String ShortName();
}
