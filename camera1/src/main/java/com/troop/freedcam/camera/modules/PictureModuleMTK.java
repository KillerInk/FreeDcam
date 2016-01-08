package com.troop.freedcam.camera.modules;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.FocusHandler;
import com.troop.freedcam.camera.modules.image_saver.MediatekSaver;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleMTK extends PictureModule
{
    private static String TAG = PictureModuleMTK.class.getSimpleName();
    String lastFile;
    CamParametersHandler parametersHandler;
    String file;
    public PictureModuleMTK(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler, Handler backgroundHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler, backgroundHandler);
        this.parametersHandler = (CamParametersHandler)baseCameraHolder.ParameterHandler;
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
