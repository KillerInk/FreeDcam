package com.troop.freedcam.camera.modules;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;


import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends AbstractModuleHandler
{
    BaseCameraHolder cameraHolder;
    final String TAG = "freedcam.ModuleHandler";

    public  ModuleHandler (AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder, appSettingsManager);
        this.cameraHolder = (BaseCameraHolder) cameraHolder;


        initModules();

    }

    protected void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (DeviceUtils.isMediaTekTHL5000())
        {
            PictureModuleThl5000 thl5000 = new PictureModuleThl5000(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(thl5000.ModuleName(), thl5000);
        }
        else if (DeviceUtils.isOmap())
        {
            PictureModuleO3D omap = new PictureModuleO3D(this.cameraHolder,appSettingsManager,moduleEventHandler);
            moduleList.put(omap.ModuleName(),omap);
        }
        else //use default pictureModule
        {
            PictureModule pictureModule = new PictureModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
        }

        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21 ) {
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            VideoModule videoModule = new VideoModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        HdrModule hdrModule = new HdrModule(this.cameraHolder,appSettingsManager, moduleEventHandler);
        moduleList.put(hdrModule.ModuleName(), hdrModule);

        //BurstModule burstModule = new BurstModule(this.cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

        /*LongExposureModule longExposureModule = new LongExposureModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(longExposureModule.ModuleName(), longExposureModule);*/
    }

}
