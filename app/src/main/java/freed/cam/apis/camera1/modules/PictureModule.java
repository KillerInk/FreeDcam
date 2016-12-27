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
import android.os.Handler;
import android.util.Log;

import java.io.File;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.BasePictureModule;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.dng.DngProfile;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends BasePictureModule implements Camera.PictureCallback
{

    private final String TAG = PictureModule.class.getSimpleName();
    private int burstcount;
    protected CameraHolder cameraHolder;
    protected boolean waitForPicture;


    public PictureModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler);
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
    public void DoWork()
    {
        Log.d(this.TAG, "DoWork:isWorking:"+ isWorking);
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                isWorking = true;
                String picformat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
                Log.d(TAG,"DoWork:picformat:" + picformat);
                if (picformat.equals(KEYS.DNG) ||picformat.equals(KEYS.BAYER))
                {
                    if (cameraUiWrapper.GetParameterHandler().ZSL != null && cameraUiWrapper.GetParameterHandler().ZSL.IsSupported()
                            && cameraUiWrapper.GetParameterHandler().ZSL.GetValue().equals("on") && ((CameraHolder) cameraUiWrapper.GetCameraHolder()).DeviceFrameWork != CameraHolder.Frameworks.MTK)
                    {
                        Log.d(TAG,"ZSL is on turning it off");
                        cameraUiWrapper.GetParameterHandler().ZSL.SetValue("off", true);
                        Log.d(TAG,"ZSL state after turning it off:" + cameraUiWrapper.GetParameterHandler().ZSL.GetValue());
                    }
                }
                cameraUiWrapper.GetParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
                changeCaptureState(CaptureStates.image_capture_start);
                waitForPicture = true;
                burstcount = 0;
                if (cameraUiWrapper.GetAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
                    cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
                cameraHolder.TakePicture(PictureModule.this);
                Log.d(TAG,"TakePicture");
            }
        });
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        Log.d(TAG,"InitModule");
        cameraUiWrapper.GetParameterHandler().PreviewFormat.SetValue("yuv420sp",true);
        if (cameraUiWrapper.GetParameterHandler().VideoHDR != null && cameraUiWrapper.GetParameterHandler().VideoHDR.IsSupported() && !cameraUiWrapper.GetParameterHandler().VideoHDR.GetValue().equals("off"))
            cameraUiWrapper.GetParameterHandler().VideoHDR.SetValue("off", true);
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 || appSettingsManager.getDevice() == Devices.ZTEADVIMX214) {
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetZTE_AE();
        }
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        if(data == null)
            return;
        Log.d(this.TAG, "onPictureTaken():"+data.length);
        if (!waitForPicture)
        {
            Log.d(this.TAG, "Got pic data but did not wait for pic");
            waitForPicture = false;
            changeCaptureState(CaptureStates.image_capture_stop);
            startPreview();
            return;
        }
        burstcount++;
        String picFormat = cameraUiWrapper.GetParameterHandler().PictureFormat.GetValue();
        saveImage(data,picFormat);
        //Handel Burst capture
        if (cameraUiWrapper.GetParameterHandler().Burst != null && cameraUiWrapper.GetParameterHandler().Burst.IsSupported() && cameraUiWrapper.GetParameterHandler().Burst.GetValue() > 1)
        {
            Log.d(this.TAG, "BurstCapture Count:" + burstcount + "/"+ cameraUiWrapper.GetParameterHandler().Burst.GetValue());
            if (burstcount == cameraUiWrapper.GetParameterHandler().Burst.GetValue())
            {
                Log.d(this.TAG, "BurstCapture done");
                waitForPicture = false;
                isWorking = false;
                startPreview();
                changeCaptureState(CaptureStates.image_capture_stop);
            }
        }
        else //handel normal capture
        {
            isWorking = false;
            waitForPicture = false;

            startPreview();
            changeCaptureState(CaptureStates.image_capture_stop);
        }
        data = null;
    }

    protected void startPreview()
    {
        //workaround to keep ae locked
        if (cameraHolder.GetCameraParameters().getAutoExposureLock() == true)
        {
            cameraUiWrapper.GetParameterHandler().ExposureLock.SetValue(KEYS.FALSE,true);
            cameraUiWrapper.GetParameterHandler().ExposureLock.SetValue(KEYS.TRUE,true);
            //cameraHolder.GetCameraParameters().setAutoExposureLock(true);
        }
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.MotoG3 ||cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.MotoG_Turbo)
        {
            MotoPreviewResetLogic();

        }else
            cameraHolder.StartPreview();

    }

    public void MotoPreviewResetLogic()
    {

        if(cameraUiWrapper.GetAppSettingsManager().GetCurrentCamera() == 0) {
            cameraUiWrapper.GetAppSettingsManager().SetCurrentCamera(1);
            cameraUiWrapper.StopCamera();
            cameraUiWrapper.StartCamera();

            cameraUiWrapper.GetAppSettingsManager().SetCurrentCamera(0);
            cameraUiWrapper.StopCamera();
            cameraUiWrapper.StartCamera();
        }else {
            cameraUiWrapper.GetAppSettingsManager().SetCurrentCamera(0);
            cameraUiWrapper.StopCamera();
            cameraUiWrapper.StartCamera();

            cameraUiWrapper.GetAppSettingsManager().SetCurrentCamera(1);
            cameraUiWrapper.StopCamera();
            cameraUiWrapper.StartCamera();
        }
    }

    private void ShutterResetLogic()
    {
        System.out.println("BANKAI "+cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue());
        if(!cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().contains("/")&&!cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue().contains("auto"))
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetZTE_RESET_AE_SETSHUTTER(cameraUiWrapper.GetParameterHandler().ManualShutter.GetStringValue());
    }

    protected void saveImage(byte[]data, String picFormat)
    {
        File toSave = getFile(getFileEnding(picFormat));
        Log.d(this.TAG, "saveImage:"+toSave.getName() + " Filesize: "+data.length);
        if (picFormat.equals(FileEnding.DNG))
            saveDng(data,toSave);
        else {

            saveJpeg(toSave,data);
        }
        if(appSettingsManager.getDevice() == Devices.ZTE_ADV || appSettingsManager.getDevice() == Devices.ZTEADV234 || appSettingsManager.getDevice() == Devices.ZTEADVIMX214)
            ShutterResetLogic();

        fireInternalOnWorkFinish(toSave);
    }

    protected void fireInternalOnWorkFinish(File tosave)
    {
        fireOnWorkFinish(tosave);
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

        float fnum = cameraUiWrapper.GetParameterHandler().getDevice().GetFnumber();
        float focal = cameraUiWrapper.GetParameterHandler().getDevice().GetFocal();
        float exposuretime = cameraUiWrapper.GetParameterHandler().getDevice().getCurrentExposuretime();
        int iso = cameraUiWrapper.GetParameterHandler().getDevice().getCurrentIso();
        String wb = null;
        if (cameraUiWrapper.GetParameterHandler().CCT != null && cameraUiWrapper.GetParameterHandler().CCT.IsSupported())
        {
            wb = cameraUiWrapper.GetParameterHandler().CCT.GetStringValue();
            if (wb.equals(KEYS.AUTO))
                wb = null;
            Log.d(this.TAG,"Set Manual WhiteBalance:"+ wb);
        }
        DngProfile dngProfile = cameraUiWrapper.GetParameterHandler().getDevice().getDngProfile(data.length);
        Log.d(TAG, "found dngProfile:" + (dngProfile != null));
        int orientation = cameraUiWrapper.getActivityInterface().getOrientation();
        saveRawToDng(file,data, fnum,focal,exposuretime,iso,orientation,wb,dngProfile);
        data = null;

    }
}
