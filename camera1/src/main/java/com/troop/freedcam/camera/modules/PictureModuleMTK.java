package com.troop.freedcam.camera.modules;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
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
    public void DoWork()
    {
        if (!this.isWorking)
        {
            startworking();
            /*if (parametersHandler.IsDngActive())
            {
                file = StringUtils.getFilePath(Settings.GetWriteExternal(),"");
                parametersHandler.setMTKRaw(true);
               // parametersHandler.setMTKrawFilename(file + ".raw");
            }
            else
            {
                file = StringUtils.getFilePath(Settings.GetWriteExternal(),".jpg");;
                parametersHandler.setMTKRaw(true);
                //parametersHandler.setMTKrawFilename("");
            }*/

            baseCameraHolder.TakePicture(null,null, picCallback);

        }

    }

    I_Callbacks.PictureCallback picCallback = new I_Callbacks.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data)
        {
            Log.e(TAG, "Start Saving Bytes");
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file+".jpg");
                outStream.write(data);
                outStream.flush();
                outStream.close();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            baseCameraHolder.StartPreview();
            File f = new File(file +".jpg");
            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), f);
            if (parametersHandler.IsDngActive())
                MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), new File(file + ".raw"));
            stopworking();
            eventHandler.WorkFinished(f);
        }
    };
}
