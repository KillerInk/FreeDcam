package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.manager.MediaScannerManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecam.interfaces.SavePictureCallback;
import com.troop.freecam.utils.SavePicture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by troop on 18.10.13.
 */
public class PictureCam extends BaseCamera implements Camera.ShutterCallback, Camera.PictureCallback, SavePictureCallback
{


    protected MediaScannerManager scanManager;
    protected SoundPlayer soundPlayer;
    protected CamPreview context;
    protected SavePicture savePicture;
    public boolean crop = false;

    public SavePictureCallback onsavePicture;
    public boolean IsWorking = false;

    //byte[] rawbuffer = new byte[31457280];



    public PictureCam(CamPreview context,SharedPreferences preferences)
    {
        super(preferences);
        this.context = context;
        this.scanManager = new MediaScannerManager(context.getContext());
        soundPlayer = new SoundPlayer(context.getContext());
        savePicture = new SavePicture(scanManager, preferences);
        savePicture.onSavePicture = this;
    }

    //private static final int CAMERA_MSG_RAW_IMAGE = 0x080;
    //private native final void _addCallbackBuffer(
            //byte[] callbackBuffer, int msgType);

    public void TakePicture(boolean crop)
    {
        IsWorking = true;
        this.crop = crop;
        //mCamera.addRawImageCallbackBuffer(rawbuffer);
        //mCamera.addCallbackBuffer(rawbuffer);
        //_addCallbackBuffer(rawbuffer, CAMERA_MSG_RAW_IMAGE);

        /*try {
            Class c = Class.forName("android.hardware.Camera");
            //public final void android.hardware.Camera.addRawImageCallbackBuffer([B)
            Method m = c.getMethod("addRawImageCallbackBuffer", byte[].class);
            //m.invoke(c, rawbuffer);
            //int i = m.length;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/
        mCamera.takePicture(this, rawCallback,this);
    }

    /** Handles data for raw picture */
    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("FreeCam", "onPictureTaken - raw");
            //if (data != null)

                //saveRawData(data);
        }
    };


    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {

        Log.d("PictureCallback", "DATAsize:" + data.length);
        boolean is3d = false;
        if (Settings.Cameras.GetCamera().equals(SettingsManager.Preferences.MODE_3D))
        {
            is3d = true;
        }

        savePicture.SaveToSD(data, crop, mCamera.getParameters().getPictureSize(), is3d);

        mCamera.startPreview();
        IsWorking = false;
        data = null;
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

    private void saveRawData(byte[] data)
    {
        File file = SavePicture.getFilePath("raw", Environment.getExternalStorageDirectory());
        FileOutputStream outStream = null;
        try {
        outStream = new FileOutputStream(file);

        outStream.write(data);
        outStream.flush();
        outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
