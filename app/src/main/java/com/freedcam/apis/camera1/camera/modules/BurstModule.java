package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by troop on 26.08.2014.
 */
public class BurstModule extends AbstractModule implements I_Callbacks.PreviewCallback
{
    private final String TAG = BurstModule.class.getSimpleName();
    boolean doBurst = false;
    String currentBurstFolder;
    int count;
    CameraHolderApi1 cameraHolderApi1;
    public BurstModule(CameraHolderApi1 cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        this.name = ModuleHandler.MODULE_BURST;
        this.cameraHolderApi1 = (CameraHolderApi1)cameraHandler;
    }

    @Override
    public String ShortName() {
        return "Burst";
    }

    @Override
    public String LongName() {
        return "Burst";
    }

    @Override
    public boolean DoWork() {
        return false;
    }

    @Override
    public void LoadNeededParameters() {

    }

    @Override
    public void UnloadNeededParameters() {

    }

    @Override
    public void onPreviewFrame(byte[] data, int imageFormat)
    {
        if (doBurst)
        {
            new Thread(new saveDataRunner(data)).start();
            count++;
        }

    }

    public void EnableBurst(boolean enable)
    {
        if (enable)
        {
            cameraHolderApi1.SetPreviewCallback(this);
            currentBurstFolder = createNewFolder();
            count = 0;

        }
        else
        {
            cameraHolderApi1.ResetPreviewCallback();

        }
        doBurst = enable;
    }


    private class saveDataRunner implements Runnable
    {
        private final byte[] bytes;

        public saveDataRunner(byte[]bytes)
        {
            this.bytes = bytes;
        }

        @Override
        public void run()
        {
            saveYuvImage(bytes);
        }
    }

    private void saveYuvImage(byte[]bytes)
    {
        File file = createFileName();
        Logger.d(TAG, "Saving file: " + file.getAbsolutePath());
        String[] split = ParameterHandler.PreviewSize.GetValue().split("x");
        Rect rect = new Rect(0,0,Integer.parseInt(split[0]),Integer.parseInt(split[1]));
        YuvImage img = new YuvImage(bytes, ImageFormat.NV21, Integer.parseInt(split[0]), Integer.parseInt(split[1]), null);
        OutputStream outStream = null;
        try
        {
            outStream = new FileOutputStream(file);
            img.compressToJpeg(rect, 100, outStream);
            outStream.flush();
            outStream.close();
        }
        catch (FileNotFoundException e)
        {
            Logger.exception(e);
        }
        catch (IOException e)
        {
            Logger.exception(e);
        }
    }

    private File createFileName()
    {
        return new File(currentBurstFolder + "/" + count + ".jpg");

    }

    private String createNewFolder() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/Burst/");
        if (!folder.exists())
            try {
                folder.mkdirs();
            }
            catch (Exception ex)
            {
                Logger.exception(ex);
            }

        File newBurstFolder = new File(folder.getAbsolutePath() + "/" + getTimeFolderName() + "/");
        if (!newBurstFolder.exists())
            try {
                newBurstFolder.mkdirs();
            }
            catch (Exception ex)
            {Logger.exception(ex);}

        return newBurstFolder.getAbsolutePath();
    }


    private String getTimeFolderName()
    {
        Date date = new Date();
        return (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
    }
}
