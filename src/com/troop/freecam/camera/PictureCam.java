package com.troop.freecam.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.SavePictureTask;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.interfaces.SavePictureCallback;

import java.io.File;

/**
 * Created by troop on 18.10.13.
 */
public class PictureCam extends BaseCamera implements Camera.ShutterCallback, Camera.PictureCallback, SavePictureCallback
{
    protected MediaScannerManager scanManager;
    protected SoundPlayer soundPlayer;
    protected Context context;
    protected SavePicture savePicture;
    public boolean crop = false;

    public SavePictureCallback onsavePicture;


    public PictureCam(Context context,SharedPreferences preferences)
    {
        super(preferences);
        this.context = context;
        this.scanManager = new MediaScannerManager(context);
        soundPlayer = new SoundPlayer(context);
        savePicture = new SavePicture(scanManager);
        savePicture.onSavePicture = this;
    }

    public void TakePicture(boolean crop)
    {
        this.crop = crop;
        mCamera.takePicture(this, rawCallback, this);
    }

    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FreeCam", "onPictureTaken - raw");
        }
    };


    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {

        Log.d("PictureCallback", "DATAsize:" + data.length);
        boolean is3d = false;
        if (preferences.getString("switchcam", "3D").equals("3D"))
        {
            is3d = true;
        }

        savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d);

        mCamera.startPreview();
        //takePicture = false;
    }

    @Override
    public void onShutter()
    {
        soundPlayer.PlayShutter();
    }

    @Override
    public void onPictureSaved(File file)
    {

    }
}
