package com.troop.freedcam.camera.modules;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.image_saver.MediatekSaver;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private static String TAG = PictureModuleMTK.class.getSimpleName();
    String lastFile;
    String file;
    public PictureModuleMTK(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler, Handler backgroundHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler, backgroundHandler);

    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            startworking();
            final MediatekSaver mtksaver = new MediatekSaver(baseCameraHolder, this, handler, Settings.GetWriteExternal());
            mtksaver.TakePicture();

        }
        return true;
    }

}
