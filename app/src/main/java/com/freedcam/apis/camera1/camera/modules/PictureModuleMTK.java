package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.camera1.camera.modules.image_saver.MediatekSaver;
import com.freedcam.apis.i_camera.modules.AbstractModule;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private static String TAG = PictureModuleMTK.class.getSimpleName();
    public PictureModuleMTK(BaseCameraHolder baseCameraHolder, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(baseCameraHolder, eventHandler,context,appSettingsManager);

    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            startworking();
            final MediatekSaver mtksaver = new MediatekSaver(baseCameraHolder, this, context,appSettingsManager);
            mtksaver.TakePicture();

        }
        return true;
    }

}
