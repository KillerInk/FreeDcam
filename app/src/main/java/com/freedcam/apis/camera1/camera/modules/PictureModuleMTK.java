package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.modules.image_saver.MediatekSaver;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private static String TAG = PictureModuleMTK.class.getSimpleName();
    public PictureModuleMTK(CameraHolderApi1 cameraHolderApi1, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolderApi1, eventHandler,context,appSettingsManager);

    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            startworking();
            final MediatekSaver mtksaver = new MediatekSaver(cameraHolderApi1, this, context,appSettingsManager);
            mtksaver.TakePicture();

        }
        return true;
    }

}
