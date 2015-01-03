package com.troop.freedcam.camera.modules;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 24.11.2014.
 */
public class PictureModuleThl5000 extends PictureModule
{
    public PictureModuleThl5000(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler) {
        super(baseCameraHolder, appSettingsManager, eventHandler);
    }

    @Override
    protected void takePicture()
    {
        this.isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try {
            //soundPlayer.PlayShutter();
            if (Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals(("dng"))
                    || Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw")) {
                parametersHandler.setTHL5000rawFilename(createFileName().getAbsolutePath());
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
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if (Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals(("dng"))
                || Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw")) {
            return;
        }
        else {
            if (processCallbackData(data)) return;
            baseCameraHolder.StartPreview();
        }
    }
}
