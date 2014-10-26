package com.troop.freecamv2.camera.modules;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.troop.androiddng.RawToDng;
import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.ui.AppSettingsManager;
import com.troop.freecamv2.utils.DeviceUtils;
import com.troop.freecamv2.utils.StringUtils;

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

    private String rawFormats = "bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,raw,,bayer-qcom-10gbrg,bayer-qcom-10grbg,bayer-qcom-10rggb,bayer-qcom-10bggr,bayer-ideal-qcom-10grbg";
    private String jpegFormat = "jpeg";
    private String jpsFormat = "jps";

    public String OverRidePath = "";
    int hdrCount = 0;
    boolean hdr = false;

    public PictureModule(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
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
        if (baseCameraHolder.ParameterHandler.AE_Bracket.IsSupported())
        {
            if (!baseCameraHolder.ParameterHandler.AE_Bracket.GetValue().equals("Off"))
            {
                hdrCount = 0;
                hdr =true;
            }
            else
                hdr = false;
        }

        try
        {
            //soundPlayer.PlayShutter();
            baseCameraHolder.TakePicture(null,rawCallback,this);
            Log.d(TAG, "Picture Taking is Started");

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }

    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            if (data!= null)
            {
                Log.d(TAG, "RawCallback data size: " + data.length);
                File file = createFileName();
                final saveFile save = new saveFile(data.clone(), file);
                final Thread worker = new Thread(save);
                worker.start();
            }
            else
                Log.d(TAG, "RawCallback data size is null" );
            //if (data != null)
            //saveRawData(data);
        }
    };


    public void onPictureTaken(byte[] data, Camera camera)
    {
        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if(data.length < 4500)
        {
            baseCameraHolder.errorHandler.OnError("Data size is < 4kb");
            isWorking = false;
            baseCameraHolder.StartPreview();
            return;
        }
        else
        {
            baseCameraHolder.errorHandler.OnError("Datasize : " + StringUtils.readableFileSize(data.length));
        }
        File file = createFileName();


        final saveFile save = new saveFile(data.clone(), file);
        final Thread worker = new Thread(save);
        worker.start();
        isWorking = false;
        if (!DeviceUtils.isHTCADV())
            baseCameraHolder.ParameterHandler.LockExposureAndWhiteBalance(false);
        /*if (hdr && hdrCount == 2)
            baseCameraHolder.StartPreview();
        else*/
            baseCameraHolder.StartPreview();
    }


    private class saveFile implements Runnable {

        private byte[] bytes;
        private File file;

        public saveFile(byte[] bytes, File file)
        {
            this.bytes = bytes;
            this.file = file;
        }
        @Override
        public void run()
        {
            if (OverRidePath == "")
            {
                if (!file.getAbsolutePath().endsWith(".dng")) {
                    saveBytesToFile(bytes, file);
                    eventHandler.WorkFinished(file);
                    bytes = null;
                    file = null;
                } else {
                    String rawSize = baseCameraHolder.ParameterHandler.GetRawSize();
                    String raw[] = rawSize.split("x");
                    int w = Integer.parseInt(raw[0]);
                    int h = Integer.parseInt(raw[1]);
                    RawToDng.ConvertRawBytesToDng(bytes, file.getAbsolutePath(), w, h);
                    eventHandler.WorkFinished(file);
                }
            }
            else
            {
                file = new File(OverRidePath);
                saveBytesToFile(bytes, file);
                eventHandler.WorkFinished(file);
                bytes = null;
                file = null;
            }

        }
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
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        String s1 = (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
        if (hdr)
        {
            s1 += "HDR" + hdrCount;
            hdrCount++;
        }
        if (baseCameraHolder.ParameterHandler.dngSupported)
        {
            if(Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("dng"))
                return new File((new StringBuilder(String.valueOf(s1))).append("_" + pictureFormat).append(".dng").toString());
            if(Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("raw"))
                return new File((new StringBuilder(String.valueOf(s1))).append("_" + pictureFormat).append(".raw").toString());
            if(Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("jpeg"))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
        }
        else
        {
            if (rawFormats.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append("_" + pictureFormat).append(".raw").toString());
            if (jpegFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jpg").toString());
            if (jpsFormat.contains(pictureFormat))
                return new File((new StringBuilder(String.valueOf(s1))).append(".jps").toString());
        }
        return  null;
    }
}
