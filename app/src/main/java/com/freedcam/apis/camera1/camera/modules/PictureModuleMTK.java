package com.freedcam.apis.camera1.camera.modules;

import com.freedcam.apis.camera1.camera.BaseCameraHolder;
import com.freedcam.apis.camera1.camera.modules.image_saver.MediatekSaver;
import com.freedcam.apis.i_camera.modules.ModuleEventHandler;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private static String TAG = PictureModuleMTK.class.getSimpleName();
    public PictureModuleMTK(BaseCameraHolder baseCameraHolder, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, eventHandler);

    }

    @Override
    public boolean DoWork()
    {
        if (!this.isWorking)
        {
            startworking();
            final MediatekSaver mtksaver = new MediatekSaver(baseCameraHolder, this);
            mtksaver.TakePicture();

        }
        return true;
    }

}
