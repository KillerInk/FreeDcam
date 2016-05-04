package com.freedcam.apis.i_camera.modules;


import android.content.Context;

import com.freedcam.apis.i_camera.AbstractCameraHolder;
import com.freedcam.apis.i_camera.interfaces.I_Module;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 15.08.2014.
 */
public abstract class AbstractModule implements I_Module
{
    protected AbstractCameraHolder baseCameraHolder;
    protected AbstractParameterHandler ParameterHandler;

    protected boolean isWorking = false;
    public String name;

    protected ModuleEventHandler eventHandler;
    protected AbstractModuleHandler.I_worker workerListner;
    final static String TAG = AbstractModule.class.getSimpleName();
    protected Context context;
    protected AppSettingsManager appSettingsManager;


    public AbstractModule(AbstractCameraHolder cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        this.baseCameraHolder = cameraHandler;
        this.eventHandler = eventHandler;
        this.ParameterHandler = baseCameraHolder.GetParameterHandler();
        this.context = context;
        this.appSettingsManager = appSettingsManager;
    }

    public void SetWorkerListner(AbstractModuleHandler.I_worker workerListner)
    {
        this.workerListner = workerListner;
    }

    protected void workstarted()
    {
        Logger.d(TAG, "work started");
        if (this.workerListner != null)
            workerListner.onWorkStarted();
    }

    protected void workfinished(final boolean finish)
    {
        Logger.d(TAG, "work finished");
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
