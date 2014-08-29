package com.troop.freecamv2.camera.modules;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.defcomk.jni.libraw.RawUtils;
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

    private String rawFormats = "bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,bayer-ideal-qcom-10grbg";
    private String jpegFormat = "jpeg";
    private String jpsFormat = "jps";

    public PictureModule(BaseCameraHolder baseCameraHolder, SoundPlayer soundPlayer, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, soundPlayer, appSettingsManager, eventHandler);
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
        if (!isWorking)
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
            baseCameraHolder.TakePicture(null,null,this);
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
        File file = createFileName();

        saveFile save = new saveFile(data, file);
        new Thread(save).start();
        isWorking = false;

        baseCameraHolder.StartPreview();
    }


    private class saveFile implements Runnable {

        private final byte[] bytes;
        private final File file;

        public saveFile(byte[] bytes, File file)
        {
            this.bytes = bytes;
            this.file = file;
        }
        @Override
        public void run() {
            saveBytesToFile(bytes, file);
            eventHandler.WorkFinished(file);
        }
    }

    private void saveBytesToFile(byte[] bytes, File fileName)
    {
        if (fileName.getAbsolutePath().endsWith(".raw"))
        {
            String tiff = fileName.getAbsolutePath();
            tiff = tiff.replace(".raw", ".tiff");
            RawUtils.unpackRawByte(tiff, bytes, 0, 2.0f, 3.83f, 0.10f, 100.00f);
        }
        else {
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
            return new File((new StringBuilder(String.valueOf(s1))).append(".jps").toString());
        return  null;
    }
}
