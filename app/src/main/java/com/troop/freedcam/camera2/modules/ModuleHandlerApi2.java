package com.troop.freedcam.camera2.modules;

import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 12.12.2014.
 */
public class ModuleHandlerApi2 extends AbstractModuleHandler
{

    BaseCameraHolderApi2 cameraHolder;

    final String TAG = "freedcam.ModuleHandler";

    public  ModuleHandlerApi2 (AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder,appSettingsManager);
        this.cameraHolder = (BaseCameraHolderApi2) cameraHolder;
        initModules();
    }


    protected void initModules()
    {
        PictureModuleApi2 pictureModuleApi2 = new PictureModuleApi2(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(pictureModuleApi2.ModuleName(), pictureModuleApi2);
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner

    }
}
