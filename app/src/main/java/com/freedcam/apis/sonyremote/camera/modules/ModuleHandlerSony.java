package com.freedcam.apis.sonyremote.camera.modules;

import android.content.Context;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.sonyremote.camera.CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 13.12.2014.
 */
public class ModuleHandlerSony extends AbstractModuleHandler implements CameraHolder.I_CameraShotMode
{
    private CameraHolder cameraHolder;
    private final String TAG = ModuleHandlerSony.class.getSimpleName();

    public ModuleHandlerSony(CameraHolder cameraHolder, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder,context,appSettingsManager);
        this.cameraHolder = cameraHolder;
        cameraHolder.cameraShotMode = this;
        initModules();
    }

    protected void initModules()
    {
        PictureModuleSony pic = new PictureModuleSony(cameraHolder, moduleEventHandler,context,appSettingsManager);
        moduleList.put(pic.ModuleName(), pic);
        VideoModuleSony mov = new VideoModuleSony(cameraHolder, moduleEventHandler,context,appSettingsManager);
        moduleList.put(mov.ModuleName(), mov);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }

    @Override
    public void SetModule(String name)
    {
        if (name.equals(KEYS.MODULE_VIDEO))
            cameraHolder.SetShootMode("movie");
        else if (name.equals(KEYS.MODULE_PICTURE))
            cameraHolder.SetShootMode("still");
    }

    @Override
    public void onShootModeChanged(String mode)
    {
        Logger.d(TAG, "ShotmodeChanged:" + mode);
        if (currentModule !=null) {
            currentModule.SetWorkerListner(null);
        }
        if (mode.equals("still"))
        {
            currentModule = moduleList.get(KEYS.MODULE_PICTURE);

            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
            currentModule.InitModule();
        }
        else if (mode.equals("movie"))
        {
            currentModule = moduleList.get(KEYS.MODULE_VIDEO);
            moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
            currentModule.SetWorkerListner(workerListner);
            currentModule.InitModule();
        }
    }

    @Override
    public void onShootModeValuesChanged(String[] modes) {

    }
}
