package com.troop.freecam.camera.modules;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.BaseCameraHolder;
import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.utils.DeviceUtils;

import java.io.File;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements Camera.PictureCallback {


    private boolean isWorking;

    public final String TAG = "freecam.PictureModule";

    public PictureModule()
    {
    }

    public PictureModule(BaseCameraHolder baseCameraHolder, SoundPlayer soundPlayer, AppSettingsManager appSettingsManager)
    {
        super(baseCameraHolder, soundPlayer, appSettingsManager);

    }


    @Override
    public String ModuleName() {
        return null;
    }

    @Override
    public void SetCameraHandler(BaseCameraHolder cameraHandler) {
        this.baseCameraHolder = cameraHandler;
    }

    @Override
    public void DoWork()
    {
        takePicture();
    }

    @Override
    public boolean IsWorking() {
        return false;
    }

    private void takePicture()
    {
        isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try
        {
            soundPlayer.PlayShutter();
            baseCameraHolder.GetCamera().takePicture(null, null, this);
            Log.d(TAG, "Picture Taking is Started");

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        //File tiff = getOutputMediaFile(3);
        File file;
        boolean isRaw = false;

        String tmp = Settings.pictureFormat.Get();
        if (tmp.equals("jps"))
            file = getOutputMediaFile(1);
        else if (tmp.equals("raw")) {
            file = getOutputMediaFile(4);
            isRaw = true;
        }
        else
            file = getOutputMediaFile(2);

        writeDebug("OnPictureTaken callback recieved");
        boolean is3d = false;

        if (Settings.Cameras.GetCamera().equals(AppSettingsManager.Preferences.MODE_3D))
        {
            is3d = true;
        }
        writeDebug("start saving to sd");
        try {


            if (!isRaw)
            {
                savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d, file);
                writeDebug("save successed");

            }
            else
            {
                int black = 0;
                if(DeviceUtils.isZTEADV()||DeviceUtils.isLGADV())
                    black = 43;
                long aa = System.currentTimeMillis();

                RawDecodeX RDCD = new RawDecodeX();
                Void params = null;
                RDCD.execute(params);
                savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d, file);

                long ab = System.currentTimeMillis();
                long contime = ab - aa;

                Log.d(" Raw Save Time",String.valueOf(contime));
            }


        }
        catch (Exception ex)
        {
            Log.e(TAG, "saving to sd failed");
            ex.printStackTrace();
        }

        try {
            writeDebug("try to start preview");

            mCamera.startPreview();
            if (DeviceUtils.isEvo3d())
                parametersManager.LensShade.set(Settings.LensShade.get());
        }
        catch (Exception ex)
        {
            Log.e(TAG, "preview start failed");
            ex.printStackTrace();
        }

        IsWorking = false;
        data = null;

    }
}
