package com.troop.freedcam.camera.modules;

import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleThl5000 extends PictureModule
{
    private static String TAG = StringUtils.TAG + PictureModuleThl5000.class.getSimpleName();
    String lastFile;
    public PictureModuleThl5000(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
    }

    @Override
    protected void takePicture()
    {
        this.isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try {
            //soundPlayer.PlayShutter();
            String format = Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
            if (format.equals("dng") || format.equals("raw"))
            {
                lastFile = createFileName().getAbsolutePath();
                Log.d(TAG, "Save File To :" + lastFile);
                parametersHandler.setTHL5000rawFilename(lastFile);
                baseCameraHolder.TakePicture(shutterCallback, rawCallback, this);
            } else
                baseCameraHolder.TakePicture(shutterCallback, rawCallback, this);
            Log.d(TAG, "Picture Taking is Started");
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }

    @Override
    public void onPictureTaken(byte[] data) {
        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        String format = Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
        if (format.equals("dng") || format.equals("raw"))
        {
            Log.d(TAG, "Check if Raw file exists: " + new File(lastFile).exists());
            eventHandler.WorkFinished(file);
            workfinished(true);
            baseCameraHolder.StartPreview();
        }
        else {
            if (processCallbackData(data)) return;
            baseCameraHolder.StartPreview();
        }
    }
}
