package com.troop.freedcam.camera.modules;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.modules.image_saver.DngSaver;
import com.troop.freedcam.camera.modules.image_saver.I_WorkeDone;
import com.troop.freedcam.camera.modules.image_saver.JpegSaver;
import com.troop.freedcam.camera.modules.image_saver.RawSaver;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.drew.metadata.exif.ExifDirectory;



/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements I_WorkeDone {

    private static String TAG = PictureModule.class.getSimpleName();

    protected String rawFormats = "bayer-mipi-10gbrg,bayer-mipi-10grbg,bayer-mipi-10rggb,bayer-mipi-10bggr,raw,bayer-qcom-10gbrg,bayer-qcom-10grbg,bayer-qcom-10rggb,bayer-qcom-10bggr,bayer-ideal-qcom-10grbg,bayer-ideal-qcom-10bggr";
    protected String jpegFormat = "jpeg";
    protected String jpsFormat = "jps";

    protected String lastBayerFormat;
    private String lastPicSize;
    RawToDng dngConverter;
    boolean dngcapture = false;

    private HandlerThread backgroundThread;
    Handler handler;
    ////////////
//defcomg 31-1-2015 Pull Orientation From Sesnor

    public String OverRidePath = "";
    CamParametersHandler parametersHandler;
    BaseCameraHolder baseCameraHolder;
    boolean dngJpegShot = false;


    public PictureModule(BaseCameraHolder baseCameraHolder, AppSettingsManager appSettingsManager, ModuleEventHandler eventHandler)
    {
        super(baseCameraHolder, appSettingsManager, eventHandler);
        this.baseCameraHolder = baseCameraHolder;
        name = ModuleHandler.MODULE_PICTURE;

        parametersHandler = (CamParametersHandler)baseCameraHolder.ParameterHandler;
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public String LongName() {
        return "Picture";
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
        {
            startworking();
            final String picFormat = baseCameraHolder.ParameterHandler.PictureFormat.GetValue();
            if (picFormat.equals("jpeg"))
            {
                final JpegSaver jpegSaver = new JpegSaver(baseCameraHolder, this, handler);
                jpegSaver.TakePicture();
            }
            else if (!parametersHandler.isDngActive && (picFormat.contains("bayer") || picFormat.contains("raw")))
            {
                final RawSaver rawSaver =  new RawSaver(baseCameraHolder,this, handler);
                rawSaver.TakePicture();
            }
            else if (parametersHandler.isDngActive && (picFormat.contains("bayer") || picFormat.contains("raw")))
            {
                DngSaver dngSaver = new DngSaver(baseCameraHolder, this, handler);
                dngSaver.TakePicture();
            }
        }

    }

    private void sendMsg(final String msg)
    {
          baseCameraHolder.errorHandler.OnError(msg);
    }



    @Override
    public void LoadNeededParameters()
    {
        startThread();
        if (ParameterHandler.AE_Bracket != null && ParameterHandler.AE_Bracket.IsSupported())
            ParameterHandler.AE_Bracket.SetValue("false", true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported() && ParameterHandler.VideoHDR.GetValue().equals("off"))
            ParameterHandler.VideoHDR.SetValue("off", true);
        //if (ParameterHandler.CameraMode.IsSupported() && ParameterHandler.CameraMode.GetValue().equals("1"))
            //ParameterHandler.CameraMode.SetValue("0", true);
        //if (ParameterHandler.ZSL.IsSupported() && !ParameterHandler.ZSL.GetValue().equals("off"))
            //ParameterHandler.ZSL.SetValue("off", true);
        //if(ParameterHandler.MemoryColorEnhancement.IsSupported() && ParameterHandler.MemoryColorEnhancement.GetValue().equals("enable"))
            //ParameterHandler.MemoryColorEnhancement.SetValue("disable",true);
        //if (ParameterHandler.DigitalImageStabilization.IsSupported() && ParameterHandler.DigitalImageStabilization.GetValue().equals("enable"))
            //ParameterHandler.DigitalImageStabilization.SetValue("disable", true);

        if(DeviceUtils.isZTEADV())
           parametersHandler.setString("slow_shutter", "-1");

    }

    protected void startThread() {
        backgroundThread = new HandlerThread("PictureModuleThread");
        backgroundThread.start();
        handler = new Handler(backgroundThread.getLooper());
    }

    @Override
    public void UnloadNeededParameters()
    {
        stopThread();
    }

    protected void stopThread() {
        if (Build.VERSION.SDK_INT>17)
            backgroundThread.quitSafely();
        else
            backgroundThread.quit();
        try {
            backgroundThread.join();
            backgroundThread = null;
            handler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void startworking()
    {
        isWorking = true;
        workstarted();
    }

    protected void stopworking()
    {
        isWorking = false;
        workfinished(true);
    }

    @Override
    public void OnWorkDone(File file)
    {
        baseCameraHolder.StartPreview();
        MediaScannerManager.ScanMedia(Settings.context.getApplicationContext() , file);
        stopworking();
        eventHandler.WorkFinished(file);
    }

    @Override
    public void OnError(String error)
    {
        sendMsg(error);
        stopworking();

    }
}
