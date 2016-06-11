/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.modules;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.modules.I_Callbacks.PictureCallback;
import com.freedcam.apis.basecamera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.jni.RawToDng;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.FreeDPool;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;
import com.freedcam.utils.StringUtils.FileEnding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

//import com.drew.metadata.exif.ExifDirectory;



/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends AbstractModule implements PictureCallback
{

    private static String TAG = PictureModule.class.getSimpleName();
    private int burstcount = 0;
    //protected ParametersHandler ParameterHandler;
    protected CameraHolder cameraHolder;
    protected boolean waitForPicture = false;


    public PictureModule(Context context, I_CameraUiWrapper cameraUiWrapper)
    {
        super(context, cameraUiWrapper);
        name = KEYS.MODULE_PICTURE;
        //ParameterHandler = (ParametersHandler)cameraUiWrapper.GetParameterHandler();
        this.cameraHolder = (CameraHolder)cameraUiWrapper.GetCameraHolder();
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
        if (!isWorking)
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
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 ||appSettingsManager.getDevice() == Devices.ZTEADVIMX214) {
            ((ParametersHandler)ParameterHandler).SetZTESlowShutter();
        }
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
                String picFormat = ParameterHandler.PictureFormat.GetValue();
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
        if (picFormat.equals(FileEnding.DNG))
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
        else if (!ParameterHandler.IsDngActive() && (picFormat.equals(FileEnding.BAYER) || picFormat.equals(FileEnding.RAW)))
            return ".bayer";
        else if (ParameterHandler.IsDngActive() && picFormat.contains(FileEnding.DNG))
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
        float fnum = ParameterHandler.getDevice().GetFnumber();
        float focal = ParameterHandler.getDevice().GetFocal();
        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);

        if (ParameterHandler.CCT != null && ParameterHandler.CCT.IsSupported())
        {
            String wb = ParameterHandler.CCT.GetStringValue();
            Logger.d(TAG,"Set Manual WhiteBalance:"+ wb);
            if (!wb.equals(KEYS.AUTO))
            {
                dngConverter.SetWBCT(wb);
            }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal())
        {
            Logger.d(TAG, "Write To internal or kitkat<");
            checkFileExists(file);
            dngConverter.SetBayerData(data, file.getAbsolutePath());
            dngConverter.WriteDngWithProfile(ParameterHandler.getDevice().getDngProfile(data.length));
            dngConverter.RELEASE();
        }
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager,context);
            Logger.d(TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(".jpg", ".dng"));
            Logger.d(TAG,"Filepath: " + wr.getUri());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null)
            {
                dngConverter.SetBayerDataFD(data, pfd, file.getName());
                dngConverter.WriteDngWithProfile(ParameterHandler.getDevice().getDngProfile(data.length));
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
