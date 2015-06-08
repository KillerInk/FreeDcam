package com.troop.freedcam.sonyapi.modules;

import android.util.Log;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends AbstractModuleHandler implements CameraHolderSony.I_CameraShotMode
{
    CameraHolderSony cameraHolder;
    final String TAG = ModuleHandlerSony.class.getSimpleName();

    public ModuleHandlerSony(CameraHolderSony cameraHolder, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder, appSettingsManager);
        this.cameraHolder = cameraHolder;
        cameraHolder.cameraShotMode = this;
        initModules();
    }

    protected void initModules()
    {
        PictureModuleSony pic = new PictureModuleSony(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(pic.ModuleName(), pic);
        VideoModuleSony mov = new VideoModuleSony(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(mov.ModuleName(), mov);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public void SetModule(String name)
    {
        if (name.equals(MODULE_VIDEO))
            cameraHolder.SetShootMode("movie");
        else if (name.equals(MODULE_PICTURE))
            cameraHolder.SetShootMode("still");
    }

    @Override
    public void onShootModeChanged(String mode)
    {
        if (currentModule !=null) {
            currentModule.SetWorkerListner(null);
        }
        if (mode.equals("still"))
        {
            currentModule = moduleList.get(MODULE_PICTURE);
            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
        }
        else if (mode.equals("movie"))
        {
            currentModule = moduleList.get(MODULE_VIDEO);
            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
        }
    }

    @Override
    public void onShootModeValuesChanged(String[] modes) {

    }
}
