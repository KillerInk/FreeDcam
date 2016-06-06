package com.freedcam.apis.camera1.camera.modules;


import android.content.Context;
import android.os.Build;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.IntervalModule;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 16.08.2014.
 */
public class ModuleHandler extends AbstractModuleHandler
{
    private CameraHolder cameraHolder;
    private static String TAG = "freedcam.ModuleHandler";


    public  ModuleHandler (AbstractCameraHolder cameraHolder, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder,context,appSettingsManager);
        this.cameraHolder = (CameraHolder) cameraHolder;
        initModules();

    }

    protected void initModules()
    {
        //init the Modules DeviceDepending
        //splitting modules make the code foreach device cleaner
        if (cameraHolder.DeviceFrameWork == CameraHolder.Frameworks.MTK)
        {
            Logger.d(TAG, "load mtk picmodule");
            PictureModuleMTK thl5000 = new PictureModuleMTK(this.cameraHolder, moduleEventHandler,context,appSettingsManager);
            moduleList.put(thl5000.ModuleName(), thl5000);
            IntervalModule intervalModule = new IntervalModule(cameraHolder,moduleEventHandler,thl5000,context,appSettingsManager);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }
        else//else //use default pictureModule
        {
            Logger.d(TAG, "load default picmodule");
            PictureModule pictureModule = new PictureModule(this.cameraHolder, moduleEventHandler,context,appSettingsManager);
            moduleList.put(pictureModule.ModuleName(), pictureModule);
            IntervalModule intervalModule = new IntervalModule(cameraHolder,moduleEventHandler,pictureModule,context,appSettingsManager);
            moduleList.put(intervalModule.ModuleName(), intervalModule);
        }

        if (cameraHolder.DeviceFrameWork == CameraHolder.Frameworks.LG)
        {
            Logger.d(TAG, "load lg videomodule");
            VideoModuleG3 videoModuleG3 = new VideoModuleG3(this.cameraHolder, moduleEventHandler,context,appSettingsManager);
            moduleList.put(videoModuleG3.ModuleName(), videoModuleG3);
        }
        else
        {
            Logger.d(TAG, "load default videomodule");
            VideoModule videoModule = new VideoModule(this.cameraHolder, moduleEventHandler,context,appSettingsManager);
            moduleList.put(videoModule.ModuleName(), videoModule);
        }

        Logger.d(TAG, "load hdr module");
        BracketModule bracketModule = new BracketModule(this.cameraHolder, moduleEventHandler,context,appSettingsManager);
        moduleList.put(bracketModule.ModuleName(), bracketModule);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            StackingModule sTax = new StackingModule(this.cameraHolder, moduleEventHandler, context, appSettingsManager);
            moduleList.put(sTax.ModuleName(), sTax);
        }

        //BurstModule burstModule = new BurstModule(this.cameraHolder, soundPlayer, appSettingsManager, moduleEventHandler);
        //moduleList.put(burstModule.ModuleName(), burstModule);

    }

}
