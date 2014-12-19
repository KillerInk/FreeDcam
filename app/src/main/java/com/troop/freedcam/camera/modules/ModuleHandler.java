package com.troop.freedcam.camera.modules;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;


import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
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




    public static final String MODULE_VIDEO = "module_video";
    public static final String MODULE_PICTURE = "module_picture";
    public static final String MODULE_HDR = "module_hdr";
    public static final String MODULE_BURST = "module_burst";
    public static final String MODULE_LONGEXPO = "module_longexposure";
    public static final String MODULE_ALL = "module_all";

    public  ModuleHandler (I_CameraHolder cameraHolder, AppSettingsManager appSettingsManager)
    {
        this.cameraHolder = (BaseCameraHolder) cameraHolder;
        this.appSettingsManager = appSettingsManager;
        moduleList  = new HashMap<String, AbstractModule>();
        moduleEventHandler = new ModuleEventHandler();
        PictureModules = new ArrayList<String>();
        PictureModules.add(MODULE_PICTURE);
        PictureModules.add(MODULE_BURST);
        PictureModules.add(MODULE_HDR);
        //PictureModules.add();
        VideoModules = new ArrayList<String>();
        VideoModules.add(MODULE_VIDEO);
        AllModules = new ArrayList<String>();
        AllModules.add(MODULE_ALL);
        LongeExpoModules = new ArrayList<String>();
        LongeExpoModules.add(MODULE_LONGEXPO);
        initModules();

    }

    public void SetModule(String name)
    {
        if (currentModule !=null)
            currentModule.UnloadNeededParameters();
        currentModule = moduleList.get(name);
        currentModule.LoadNeededParameters();
        moduleEventHandler.ModuleHasChanged(currentModule.ModuleName());
        Log.d(TAG, "Set Module to " + name);
    }

    public String GetCurrentModuleName()
    {
        if (currentModule != null)
            return currentModule.name;
        else return "";
    }

    public AbstractModule GetCurrentModule()
    {
        if (currentModule != null)
            return currentModule;
        return null;
    }

    public boolean DoWork()
    {
        if (currentModule != null) {
            currentModule.DoWork();
            return true;
        }
        else
            return false;
    }

    private void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (DeviceUtils.isMediaTekTHL5000())
        {
            PictureModuleThl5000 thl5000 = new PictureModuleThl5000(cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(thl5000.ModuleName(), thl5000);
        }
        else if (DeviceUtils.isOmap())
        {
            PictureModuleO3D omap = new PictureModuleO3D(cameraHolder,appSettingsManager,moduleEventHandler);
            moduleList.put(omap.ModuleName(),omap);
        }
        else //use default pictureModule
        {
            PictureModule pictureModule = new PictureModule(cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
        }

        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21 ) {
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            VideoModule videoModule = new VideoModule(cameraHolder, appSettingsManager, moduleEventHandler);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        HdrModule hdrModule = new HdrModule(cameraHolder,appSettingsManager, moduleEventHandler);
        moduleList.put(hdrModule.ModuleName(), hdrModule);

        //BurstModule burstModule = new BurstModule(cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

        LongExposureModule longExposureModule = new LongExposureModule(cameraHolder, appSettingsManager, moduleEventHandler);
        moduleList.put(longExposureModule.ModuleName(), longExposureModule);
    }

}
