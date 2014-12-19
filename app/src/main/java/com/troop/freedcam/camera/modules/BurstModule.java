package com.troop.freedcam.camera.modules;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;


import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.parameters.modes.PreviewSizeParameter;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.ui.AppSettingsManager;

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
public class BurstModule extends AbstractModule implements Camera.PreviewCallback
{
    static String TAG = "freedcam.BurstModule";
    boolean doBurst = false;
    String currentBurstFolder;
    int count;
    BaseCameraHolder baseCameraHolder;
    public BurstModule(BaseCameraHolder cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler)
    {
        super(cameraHandler, Settings, eventHandler);
        this.name = ModuleHandler.MODULE_BURST;
        this.baseCameraHolder = (BaseCameraHolder)cameraHandler;
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
    public void DoWork() {
        super.DoWork();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera)
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
            baseCameraHolder.SetPreviewCallback(this);
            currentBurstFolder = createNewFolder();
            count = 0;

        }
        else
        {
            baseCameraHolder.SetPreviewCallback(null);

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
        Log.d(TAG, "Saving file: " + file.getAbsolutePath());
        if (true/*baseCameraHolder.ParameterHandler.PreviewFormat.GetFormat() == ImageFormat.NV21*/)
        {
            PreviewSizeParameter previewSizeParameter = (PreviewSizeParameter)baseCameraHolder.ParameterHandler.PreviewSize;
            Rect rect = new Rect(0,0,previewSizeParameter.GetWidth(),previewSizeParameter.GetHeight());
            YuvImage img = new YuvImage(bytes, ImageFormat.NV21, previewSizeParameter.GetWidth(), previewSizeParameter.GetHeight(), null);
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
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
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
                ex.printStackTrace();
            }

        File newBurstFolder = new File(folder.getAbsolutePath() + "/" + getTimeFolderName() + "/");
        if (!newBurstFolder.exists())
            try {
                newBurstFolder.mkdirs();
            }
            catch (Exception ex)
            {ex.printStackTrace();}

        return newBurstFolder.getAbsolutePath();
    }


    private String getTimeFolderName()
    {
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        return  s;
    }
}
