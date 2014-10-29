package com.troop.freedcamv2.camera.modules;

import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.troop.androiddng.RawToDng;
import com.troop.freedcamv2.camera.BaseCameraHolder;
import com.troop.freedcamv2.ui.AppSettingsManager;
import com.troop.freedcamv2.utils.DeviceUtils;
import com.troop.freedcamv2.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.hardware.Camera.ShutterCallback;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements Camera.PictureCallback {

    public final String TAG = "freedcam.PictureModule";

    private String rawFormats = "bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,raw,bayer-qcom-10gbrg,bayer-qcom-10grbg,bayer-qcom-10rggb,bayer-qcom-10bggr,bayer-ideal-qcom-10grbg";
    private String jpegFormat = "jpeg";
    private String jpsFormat = "jps";

    public String OverRidePath = "";

    Handler handler;
    File file;
    byte bytes[];

    public PictureModule(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
        name = ModuleHandler.MODULE_PICTURE;
        handler = new Handler();
    }

//I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public void DoWork()
    {
        if (!this.isWorking)
            takePicture();
    }

    @Override
    public boolean IsWorking() {
        return false;
    }
//I_Module END

    private void takePicture()
    {
        this.isWorking = true;
        Log.d(TAG, "Start Taking Picture");
        try
        {
            //soundPlayer.PlayShutter();
            baseCameraHolder.TakePicture(shutterCallback,rawCallback,this);
            Log.d(TAG, "Picture Taking is Started");

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Take Picture Failed");
            ex.printStackTrace();
        }
    }


    ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter()
        {

        }
    };

    public Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera)
        {
            if (data!= null)
            {
                Log.d(TAG, "RawCallback data size: " + data.length);
            }
            else
                Log.d(TAG, "RawCallback data size is null" );
        }
    };


    public void onPictureTaken(byte[] data, Camera camera)
    {
        Log.d(TAG, "PictureCallback recieved! Data size: " + data.length);
        if (processCallbackData(data)) return;
        baseCameraHolder.StartPreview();
    }

    protected boolean processCallbackData(byte[] data) {
        if(data.length < 4500)
        {
            baseCameraHolder.errorHandler.OnError("Data size is < 4kb");
            isWorking = false;
            baseCameraHolder.StartPreview();
            return true;
        }
        else
        {
            baseCameraHolder.errorHandler.OnError("Datasize : " + StringUtils.readableFileSize(data.length));
        }
        file = createFileName();
        bytes = data;
        saveFileRunner.run();
        isWorking = false;
        if (baseCameraHolder.ParameterHandler.isExposureAndWBLocked)
            baseCameraHolder.ParameterHandler.LockExposureAndWhiteBalance(false);
        return false;
    }

    Runnable saveFileRunner = new Runnable() {
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
    };

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

    protected File createFileName()
    {
        Log.d(TAG, "Create FileName");
        String s1 = getStringAddTime();
        return  getFileAndChooseEnding(s1);
    }

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }

    protected File getFileAndChooseEnding(String s1)
    {
        String pictureFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
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
        return null;
    }
}
