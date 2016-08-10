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

package freed.cam.apis.camera1.modules;

import android.hardware.Camera;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.jni.RawToDng;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;
import freed.utils.FreeDPool;
import freed.utils.Logger;
import freed.utils.StringUtils.FileEnding;




/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends ModuleAbstract implements Camera.PictureCallback
{

    private final String TAG = PictureModule.class.getSimpleName();
    private int burstcount;
    protected CameraHolder cameraHolder;
    protected boolean waitForPicture;


    public PictureModule(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        name = KEYS.MODULE_PICTURE;
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

//ModuleInterface START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        Logger.d(this.TAG, "DoWork:isWorking:"+ isWorking);
        if (!isWorking)
        {
            isWorking = true;
            String picformat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
            Logger.d(this.TAG,"DoWork:picformat:" + picformat);
            if (picformat.equals(KEYS.DNG) ||picformat.equals(KEYS.BAYER))
            {
                if (cameraUiWrapper.GetParameterHandler().ZSL != null && cameraUiWrapper.GetParameterHandler().ZSL.IsSupported()
                        && cameraUiWrapper.GetParameterHandler().ZSL.GetValue().equals("on") && ((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != CameraHolder.Frameworks.MTK)
                {
                    Logger.d(this.TAG,"ZSL is on turning it off");
                    cameraUiWrapper.GetParameterHandler().ZSL.SetValue("off", true);
                    Logger.d(this.TAG,"ZSL state after turning it off:" + cameraUiWrapper.GetParameterHandler().ZSL.GetValue());
                }
            }
            cameraUiWrapper.GetParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
            changeCaptureState(CaptureStates.image_capture_start);
            waitForPicture = true;
            burstcount = 0;
            if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
                cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
            cameraHolder.TakePicture(this);
            Logger.d(this.TAG,"TakePicture");
        }
        return true;

    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        Logger.d(TAG,"InitModule");
        cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp",true);
        if (cameraUiWrapper.GetParameterHandler().VideoHDR != null && cameraUiWrapper.GetParameterHandler().VideoHDR.IsSupported() && !cameraUiWrapper.GetParameterHandler().VideoHDR.GetValue().equals("off"))
            cameraUiWrapper.GetParameterHandler().VideoHDR.SetValue("off", true);
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 || appSettingsManager.getDevice() == Devices.ZTEADVIMX214) {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetZTE_AE();
        }
    }

    @Override
    public void DestroyModule()
    {
    }

    @Override
    public void onPictureTaken(final byte[] data, Camera camera)
    {
        Logger.d(this.TAG, "onPictureTaken():"+data.length);
        if (!waitForPicture)
        {
            Logger.d(this.TAG, "Got pic data but did not wait for pic");
            waitForPicture = false;
            changeCaptureState(CaptureStates.image_capture_stop);
            cameraHolder.StartPreview();
            return;
        }
        FreeDPool.Execute(new Runnable() {
            @Override
            public void run()
            {
                burstcount++;
                String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
                saveImage(data,picFormat);
            }
        });
        //Handel Burst capture
        if (cameraUiWrapper.GetParameterHandler().Burst != null && cameraUiWrapper.GetParameterHandler().Burst.IsSupported() && cameraUiWrapper.GetParameterHandler().Burst.GetValue() > 1)
        {
            Logger.d(this.TAG, "BurstCapture Count:" + burstcount + "/"+ cameraUiWrapper.GetParameterHandler().Burst.GetValue());
            if (burstcount == cameraUiWrapper.GetParameterHandler().Burst.GetValue())
            {
                Logger.d(this.TAG, "BurstCapture done");
                waitForPicture = false;
                isWorking = false;
                cameraHolder.StartPreview();
                changeCaptureState(CaptureStates.image_capture_stop);
            }
        }
        else //handel normal capture
        {
            isWorking = false;
            waitForPicture = false;
            cameraHolder.StartPreview();
            changeCaptureState(CaptureStates.image_capture_stop);
        }


    }

    protected void saveImage(byte[]data, String picFormat)
    {
        File toSave = getFile(getFileEnding(picFormat));
        Logger.d(this.TAG, "saveImage:"+toSave.getName() + " Filesize: "+data.length);
        if (picFormat.equals(FileEnding.DNG))
            saveDng(data,toSave);
        else
            saveBytesToFile(data,toSave);
        scanAndFinishFile(toSave);
    }

    private String getFileEnding(String picFormat)
    {
        if (picFormat.equals(KEYS.JPEG))
            return ".jpg";
        else if (picFormat.equals("jps"))
            return  ".jps";
        else if (!cameraUiWrapper.GetParameterHandler().IsDngActive() && (picFormat.equals(FileEnding.BAYER) || picFormat.equals(FileEnding.RAW)))
            return ".bayer";
        else if (cameraUiWrapper.GetParameterHandler().IsDngActive() && picFormat.contains(FileEnding.DNG))
            return ".dng";
        return "";
    }

    protected File getFile(String fileending)
    {
        if (cameraUiWrapper.GetParameterHandler().Burst != null && cameraUiWrapper.GetParameterHandler().Burst.IsSupported() && cameraUiWrapper.GetParameterHandler().Burst.GetValue() > 1)
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathBurst(appSettingsManager.GetWriteExternal(), fileending, burstcount));
        else
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), fileending));
    }

    protected void saveDng(byte[] data, File file)
    {
        RawToDng dngConverter = RawToDng.GetInstance();
        Logger.d(this.TAG,"saveDng");
        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
        {
            if (cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation() != null)
            {
                Location location = cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation();
                Logger.d(this.TAG, "location:" + location.toString());
                Altitude = location.getAltitude();
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                Provider = location.getProvider();
                gpsTime = location.getTime();
                dngConverter.SetGPSData(Altitude, Latitude, Longitude, Provider, gpsTime);
            }
        }
        float fnum = cameraUiWrapper.GetParameterHandler().getDevice().GetFnumber();
        float focal = cameraUiWrapper.GetParameterHandler().getDevice().GetFocal();
        dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.Orientation + "", 0);

        if (cameraUiWrapper.GetParameterHandler().CCT != null && cameraUiWrapper.GetParameterHandler().CCT.IsSupported())
        {
            String wb = cameraUiWrapper.GetParameterHandler().CCT.GetStringValue();
            Logger.d(this.TAG,"Set Manual WhiteBalance:"+ wb);
            if (!wb.equals(KEYS.AUTO))
            {
                dngConverter.SetWBCT(wb);
            }
        }

        if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP || VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP && !appSettingsManager.GetWriteExternal())
        {
            Logger.d(this.TAG, "Write To internal or kitkat<");
            checkFileExists(file);
            dngConverter.SetBayerData(data, file.getAbsolutePath());
            dngConverter.WriteDngWithProfile(cameraUiWrapper.GetParameterHandler().getDevice().getDngProfile(data.length));
            dngConverter.RELEASE();
        }
        else
        {
            DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
            Logger.d(this.TAG,"Filepath: " + df.getUri());
            DocumentFile wr = df.createFile("image/dng", file.getName().replace(".jpg", ".dng"));
            Logger.d(this.TAG,"Filepath: " + wr.getUri());
            ParcelFileDescriptor pfd = null;
            try {
                pfd = cameraUiWrapper.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
            if (pfd != null)
            {
                dngConverter.SetBayerDataFD(data, pfd, file.getName());
                dngConverter.WriteDngWithProfile(cameraUiWrapper.GetParameterHandler().getDevice().getDngProfile(data.length));
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
