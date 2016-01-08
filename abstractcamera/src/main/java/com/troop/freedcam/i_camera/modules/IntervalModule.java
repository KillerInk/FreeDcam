package com.troop.freedcam.i_camera.modules;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalModule extends AbstractModule implements AbstractModuleHandler.I_worker
{
    AbstractModule picModule;
    IntervalHandler intervalHandler;

    public IntervalModule(AbstractCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler, AbstractModule picModule) {
        super(cameraHandler, Settings, eventHandler);
        this.picModule = picModule;
        picModule.SetWorkerListner(this);
        intervalHandler = new IntervalHandler(Settings,picModule);
        this.name = AbstractModuleHandler.MODULE_INTERVAL;
    }

    @Override
    public String ShortName() {
        return "Int";
    }

    @Override
    public String LongName() {
        return "Interval";
    }

    @Override
    public boolean DoWork()
    {
        if (!intervalHandler.IsWorking())
        {
            intervalHandler.StartInterval();
            return true;
        } else {
            intervalHandler.CancelInterval();
            return false;
        }
    }

    @Override
    public void LoadNeededParameters() {

    }

    @Override
    public void UnloadNeededParameters() {

    }

    @Override
    public void onWorkStarted()
    {
        workstarted();
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        workfinished(finished);
        intervalHandler.DoNextInterval();
    }

    @Override
    public boolean IsWorking() {
        return intervalHandler.IsWorking();
    }
}
