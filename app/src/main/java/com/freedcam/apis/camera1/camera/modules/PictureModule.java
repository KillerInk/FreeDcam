package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.KEYS;
import com.freedcam.jni.RawToDng;
import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//import com.drew.metadata.exif.ExifDirectory;



/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements I_Callbacks.PictureCallback
{

    private static String TAG = PictureModule.class.getSimpleName();
    private int burstcount = 0;
    protected CamParametersHandler ParameterHandler;
    protected CameraHolderApi1 cameraHolder;
    protected boolean waitForPicture = false;


    public PictureModule(CameraHolderApi1 cameraHolder, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager)
    {
        super(cameraHolder, eventHandler,context,appSettingsManager);
        name = ModuleHandler.MODULE_PICTURE;
        ParameterHandler = (CamParametersHandler)cameraHolder.GetParameterHandler();
        this.cameraHolder = cameraHolder;
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
    public boolean DoWork()
    {
        Logger.d(TAG, "DoWork:isWorking:"+isWorking);
        if (!this.isWorking)
        {
            isWorking = true;
            String picformat = ParameterHandler.PictureFormat.GetValue();
            Logger.d(TAG,"DoWork:picformat:" + picformat);
            if (picformat.equals(KEYS.DNG) ||picformat.equals(KEYS.BAYER))
            {
                if (ParameterHandler.ZSL != null && ParameterHandler.ZSL.IsSupported() && ParameterHandler.ZSL.GetValue().equals("on"))
                {
                    Logger.d(TAG,"ZSL is on turning it off");
                    ParameterHandler.ZSL.SetValue("off", true);
                    Logger.d(TAG,"ZSL state after turning it off:" + ParameterHandler.ZSL.GetValue());
                }
            }
            changeWorkState(CaptureModes.image_capture_start);
            waitForPicture = true;
            burstcount = 0;
            cameraHolder.TakePicture(null, this);
            Logger.d(TAG,"TakePicture");
        }
        return true;

    }

    @Override
    public void InitModule()
    {
        Logger.d(TAG,"InitModule");
        ParameterHandler.PreviewFormat.SetValue("yuv420sp",true);
        if (ParameterHandler.VideoHDR != null && ParameterHandler.VideoHDR.IsSupported() && !ParameterHandler.VideoHDR.GetValue().equals("off"))
            ParameterHandler.VideoHDR.SetValue("off", true);
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
            ParameterHandler.SetZTESlowShutter();
    }

    @Override
    public void DestroyModule()
    {
    }

    @Override
    public void onPictureTaken(final byte[] data)
    {
        Logger.d(TAG, "onPictureTaken():"+data.length);
        if (!waitForPicture)
        {
            Logger.d(TAG, "Got pic data but did not wait for pic");
            waitForPicture = false;
            changeWorkState(CaptureModes.image_capture_stop);
            cameraHolder.StartPreview();
            return;
        }
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                burstcount++;
                final String picFormat = ParameterHandler.PictureFormat.GetValue();
                saveImage(data,picFormat);
            }
        });
        //Handel Burst capture
        if (ParameterHandler.Burst != null && ParameterHandler.Burst.IsSupported() && ParameterHandler.Burst.GetValue() > 1)
        {
            Logger.d(TAG, "BurstCapture Count:" + burstcount + "/"+ParameterHandler.Burst.GetValue());
            if (burstcount == ParameterHandler.Burst.GetValue())
            {
                Logger.d(TAG, "BurstCapture done");
                waitForPicture = false;
                isWorking = false;
                cameraHolder.StartPreview();
                changeWorkState(CaptureModes.image_capture_stop);
            }
        }
        else //handel normal capture
        {
            isWorking = false;
            waitForPicture = false;
            cameraHolder.StartPreview();
            changeWorkState(CaptureModes.image_capture_stop);
        }


    }

    protected void saveImage(byte[]data, String picFormat)
    {
        File toSave = getFile(getFileEnding(picFormat));
        Logger.d(TAG, "saveImage:"+toSave.getName() + " Filesize: "+data.length);
        if (picFormat.equals(StringUtils.FileEnding.DNG))
            saveDng(data,toSave);
        else
            saveBytesToFile(data,toSave);
        MediaScannerManager.ScanMedia(context,toSave);
        eventHandler.WorkFinished(toSave);
    }

    private String getFileEnding(String picFormat)
    {
        if (picFormat.equals(KEYS.JPEG))
            return ".jpg";
        else if (picFormat.equals("jps"))
            return  ".jps";
        else if (!ParameterHandler.IsDngActive() && (picFormat.equals(StringUtils.FileEnding.BAYER) || picFormat.equals(StringUtils.FileEnding.RAW)))
            return ".bayer";
        else if (ParameterHandler.IsDngActive() && picFormat.contains(StringUtils.FileEnding.DNG))
            return ".dng";
        return "";
    }

    protected File getFile(String fileending)
    {
        if (ParameterHandler.Burst != null && ParameterHandler.Burst.IsSupported() && ParameterHandler.Burst.GetValue() > 1)
            return new File(StringUtils.getFilePathBurst(appSettingsManager.GetWriteExternal(), fileending, burstcount));
        else
            return new File(StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), fileending));
    }

    protected void saveDng(byte[] data, File file)
    {
        RawToDng dngConverter = RawToDng.GetInstance();
        Logger.d(TAG,"saveDng");
        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (cameraHolder.gpsLocation != null)
        {
            Logger.d(TAG, "Has GPS");
            Altitude = cameraHolder.gpsLocation.getAltitude();
            Latitude = cameraHolder.gpsLocation.getLatitude();
            Longitude = cameraHolder.gpsLocation.getLongitude();
            Provider = cameraHolder.gpsLocation.getProvider();
            gpsTime = cameraHolder.gpsLocation.getTime();
            dngConverter.SetGPSData(Altitude, Latitude, Longitude, Provider, gpsTime);
        }
        float fnum = ParameterHandler.GetFnumber();
        float focal = ParameterHandler.GetFocal();
        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);

        if (ParameterHandler.CCT != null && ParameterHandler.CCT.IsSupported())
        {
            String wb = ParameterHandler.CCT.GetStringValue();
            Logger.d(TAG,"Set Manual WhiteBalance:"+ wb);
            if (!wb.equals("Auto"))
            {
                dngConverter.SetWBCT(wb);
            }
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal()))
        {
            Logger.d(TAG, "Write To internal or kitkat<");
            checkFileExists(file);
            dngConverter.SetBayerData(data, file.getAbsolutePath());
            dngConverter.WriteDngWithProfile(ParameterHandler.Device.getDngProfile(data.length));
            dngConverter.RELEASE();
        }
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
            Logger.d(TAG,"Filepath: " +df.getUri().toString());
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(".jpg", ".dng"));
            Logger.d(TAG,"Filepath: " +wr.getUri().toString());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null)
            {
                dngConverter.SetBayerDataFD(data, pfd, file.getName());
                dngConverter.WriteDngWithProfile(ParameterHandler.Device.getDngProfile(data.length));
                dngConverter.RELEASE();
                try {
                    pfd.close();
                } catch (IOException e) {
                    Logger.exception(e);
                }
                pfd = null;
            }
        }
    }
}
