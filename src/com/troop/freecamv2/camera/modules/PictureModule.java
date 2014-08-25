package com.troop.freecamv2.camera.modules;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.troop.freecamv2.camera.BaseCameraHolder;

import com.troop.freecam.manager.SoundPlayer;
import com.troop.freecamv2.ui.AppSettingsManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements Camera.PictureCallback {

    public final String TAG = "freecam.PictureModule";

    private String rawFormats = "bayer-qcom-10gbrg,bayer-qcom-10grbg,bayer-qcom-10rggb,bayer-qcom-10bggr,bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,bayer-ideal-qcom-10grbg";
    private String jpegFormat = "jpeg";
    private String jpsFormat = "jps";

    public PictureModule(BaseCameraHolder baseCameraHolder, SoundPlayer soundPlayer, AppSettingsManager appSettingsManager)
    {
        super(baseCameraHolder, soundPlayer, appSettingsManager);
        name = ModuleHandler.MODULE_PICTURE;
    }

//I_Module START
    @Override
    public String ModuleName() {
        return name;
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
//I_Module END

    private void takePicture()
    {
        isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try
        {
            //soundPlayer.PlayShutter();
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
        Log.d(TAG, "PictureCallback recieved");
        saveBytesToFile(data, createFileName());
        isWorking = false;
        baseCameraHolder.StartPreview();
    }


    private void saveBytesToFile(byte[] bytes, File fileName)
    {
        Log.d(TAG, "Start Saving Bytes");
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(fileName);
            outStream.write(bytes);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "End Saving Bytes");
    }

    private File createFileName()
    {
        Log.d(TAG, "Create FileName");
        String pictureFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();

        if(rawFormats.contains(pictureFormat))
            return new File((new StringBuilder(String.valueOf(s1))).append(".raw").toString());
        if(jpegFormat.contains(pictureFormat))
            return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
        if (jpsFormat.contains(pictureFormat))
            return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
        return  null;
    }
}
