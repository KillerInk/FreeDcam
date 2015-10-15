package com.troop.freedcam.camera.modules;


import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends AbstractModuleHandler
{
    BaseCameraHolder cameraHolder;
    private static String TAG = "freedcam.ModuleHandler";
    android.os.Handler backgroundHandler;

    public  ModuleHandler (AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager , android.os.Handler backgroundHandler)
    {
        super(cameraHolder, appSettingsManager);
        this.cameraHolder = (BaseCameraHolder) cameraHolder;
        this.backgroundHandler = backgroundHandler;
        initModules();

    }

    protected void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (DeviceUtils.isMediaTekDevice())
        {
            Log.d(TAG, "load mtk picmodule");
            PictureModuleMTK thl5000 = new PictureModuleMTK(this.cameraHolder, appSettingsManager, moduleEventHandler, backgroundHandler);
            moduleList.put(thl5000.ModuleName(), thl5000);
        }
        /*else if (DeviceUtils.isOmap())
        {
            Log.d(TAG, "load omap picmodule");
            PictureModuleO3D omap = new PictureModuleO3D(this.cameraHolder,appSettingsManager,moduleEventHandler);
            moduleList.put(omap.ModuleName(),omap);
        }*/
        else //use default pictureModule
        {
            Log.d(TAG, "load default picmodule");
            PictureModule pictureModule = new PictureModule(this.cameraHolder, appSettingsManager, moduleEventHandler, backgroundHandler);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
        }

        if (cameraHolder.hasLGFrameWork)
        {
            Log.d(TAG, "load lg videomodule");
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else if(cameraHolder.hasSamsungFrameWork) {
            Log.d(TAG, "load samsung videomodule");
            VideoModuleSamsung videoModuleSamsung = new VideoModuleSamsung(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModuleSamsung.ModuleName(), videoModuleSamsung);
        }
        else
        {
            Log.d(TAG, "load default videomodule");
            VideoModule videoModule = new VideoModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        Log.d(TAG, "load hdr module");
        HdrModule hdrModule = new HdrModule(this.cameraHolder,appSettingsManager, moduleEventHandler, backgroundHandler);
        moduleList.put(hdrModule.ModuleName(), hdrModule);

        //BurstModule burstModule = new BurstModule(this.cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

        //LongExposureModule longExposureModule = new LongExposureModule(this.cameraHolder, appSettingsManager, moduleEventHandler);
        //moduleList.put(longExposureModule.ModuleName(), longExposureModule);
    }

}
