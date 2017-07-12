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

import com.troop.freedcam.R;

import java.io.File;
import java.util.Date;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.BasePictureModule;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.dng.DngProfile;
import freed.utils.AppSettingsManager;
import freed.utils.Log;
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
    protected long startcapturetime;


    public PictureModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_picture);
        this.cameraHolder = (CameraHolder)cameraUiWrapper.getCameraHolder();
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
        Log.d(this.TAG, "startWork:isWorking:"+ isWorking);
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                isWorking = true;
                String picformat = cameraUiWrapper.getParameterHandler().PictureFormat.GetStringValue();
                Log.d(TAG,"startWork:picformat:" + picformat);
                if (picformat.equals(appSettingsManager.getResString(R.string.dng_)) || picformat.equals(appSettingsManager.getResString(R.string.bayer_)))
                {
                    if (cameraUiWrapper.getAppSettingsManager().zeroshutterlag.isSupported()
                            && cameraUiWrapper.getParameterHandler().ZSL.GetStringValue().equals(cameraUiWrapper.getResString(R.string.on_)))
                    {
                        Log.d(TAG,"ZSL is on turning it off");
                        cameraUiWrapper.getParameterHandler().ZSL.SetValue(cameraUiWrapper.getResString(R.string.off_), true);
                        Log.d(TAG,"ZSL state after turning it off:" + cameraUiWrapper.getParameterHandler().ZSL.GetValue());
                    }

                }
                cameraUiWrapper.getParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
                changeCaptureState(CaptureStates.image_capture_start);
                waitForPicture = true;
                burstcount = 0;
                if (cameraUiWrapper.getAppSettingsManager().getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_)))
                    cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
                startcapturetime =new Date().getTime();
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
        changeCaptureState(CaptureStates.image_capture_stop);
        if (cameraUiWrapper.getParameterHandler() == null)
            return;
        cameraUiWrapper.getParameterHandler().PreviewFormat.SetValue("yuv420sp",true);
        if (cameraUiWrapper.getAppSettingsManager().videoHDR.isSupported() && !cameraUiWrapper.getParameterHandler().VideoHDR.GetStringValue().equals(cameraUiWrapper.getResString(R.string.off_)))
            cameraUiWrapper.getParameterHandler().VideoHDR.SetValue(cameraUiWrapper.getResString(R.string.off_), true);
        if(appSettingsManager.isZteAe()) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_AE();
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
        String picFormat = cameraUiWrapper.getParameterHandler().PictureFormat.GetStringValue();
        saveImage(data,picFormat);
        //Handel Burst capture
        if (cameraUiWrapper.getAppSettingsManager().manualBurst.isSupported() && cameraUiWrapper.getParameterHandler().Burst.GetValue() > 1)
        {
            Log.d(this.TAG, "BurstCapture Count:" + burstcount + "/"+ cameraUiWrapper.getParameterHandler().Burst.GetValue());
            if (burstcount == cameraUiWrapper.getParameterHandler().Burst.GetValue())
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
        if (cameraHolder.GetCameraParameters().getAutoExposureLock())
        {
            cameraUiWrapper.getParameterHandler().ExposureLock.SetValue(cameraUiWrapper.getResString(R.string.false_),true);
            cameraUiWrapper.getParameterHandler().ExposureLock.SetValue(cameraUiWrapper.getResString(R.string.true_),true);
        }
        if(cameraUiWrapper.getAppSettingsManager().needRestartAfterCapture())
        {
            MotoPreviewResetLogic();

        }else
            cameraHolder.StartPreview();

    }

    public void MotoPreviewResetLogic()
    {

        if(cameraUiWrapper.getAppSettingsManager().GetCurrentCamera() == 0) {
            cameraUiWrapper.getAppSettingsManager().SetCurrentCamera(1);
            cameraUiWrapper.stopCamera();
            cameraUiWrapper.startCamera();

            cameraUiWrapper.getAppSettingsManager().SetCurrentCamera(0);
            cameraUiWrapper.stopCamera();
            cameraUiWrapper.startCamera();
        }else {
            cameraUiWrapper.getAppSettingsManager().SetCurrentCamera(0);
            cameraUiWrapper.stopCamera();
            cameraUiWrapper.startCamera();

            cameraUiWrapper.getAppSettingsManager().SetCurrentCamera(1);
            cameraUiWrapper.stopCamera();
            cameraUiWrapper.startCamera();
        }
    }

    private void ShutterResetLogic()
    {
        System.out.println("BANKAI "+cameraUiWrapper.getParameterHandler().ManualShutter.GetStringValue());
        if(!cameraUiWrapper.getParameterHandler().ManualShutter.GetStringValue().contains("/")&&!cameraUiWrapper.getParameterHandler().ManualShutter.GetStringValue().contains("auto"))
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_RESET_AE_SETSHUTTER(cameraUiWrapper.getParameterHandler().ManualShutter.GetStringValue());
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
        if(appSettingsManager.isZteAe())
            ShutterResetLogic();

        fireInternalOnWorkFinish(toSave);
    }

    protected void fireInternalOnWorkFinish(File tosave)
    {
        fireOnWorkFinish(tosave);
    }
    private String getFileEnding(String picFormat)
    {
        if (picFormat.equals(cameraUiWrapper.getResString(R.string.jpeg_)))
            return ".jpg";
        else if (picFormat.equals("jps"))
            return  ".jps";
        else if (!cameraUiWrapper.getParameterHandler().IsDngActive() && (picFormat.equals(FileEnding.BAYER) || picFormat.equals(FileEnding.RAW)))
            return ".bayer";
        else if (cameraUiWrapper.getParameterHandler().IsDngActive() && picFormat.contains(FileEnding.DNG))
            return ".dng";
        return "";
    }

    protected File getFile(String fileending)
    {
        if (cameraUiWrapper.getAppSettingsManager().manualBurst.isSupported() && cameraUiWrapper.getParameterHandler().Burst.GetValue() > 1)
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathBurst(appSettingsManager.GetWriteExternal(), fileending, burstcount));
        else
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), fileending));
    }

    protected void saveDng(byte[] data, File file)
    {

        float fnum = ((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFnumber();
        float focal = ((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFocal();
        float exposuretime = cameraUiWrapper.getParameterHandler().getCurrentExposuretime();
        if (exposuretime == 0 && startcapturetime != 0)
        {
            exposuretime = new Date().getTime() - startcapturetime;
        }
        int iso = cameraUiWrapper.getParameterHandler().getCurrentIso();
        String wb = null;
        if (cameraUiWrapper.getParameterHandler().CCT != null && cameraUiWrapper.getParameterHandler().CCT.IsSupported())
        {
            wb = cameraUiWrapper.getParameterHandler().CCT.GetStringValue();
            if (wb.equals(cameraUiWrapper.getResString(R.string.auto_)))
                wb = null;
            Log.d(this.TAG,"Set Manual WhiteBalance:"+ wb);
        }
        DngProfile dngProfile = cameraUiWrapper.getAppSettingsManager().getDngProfilesMap().get((long)data.length);
        String cmat = appSettingsManager.matrixset.get();
        if (cmat != null && !cmat.equals("") &&!cmat.equals("off")) {
            dngProfile.matrixes = appSettingsManager.getMatrixesMap().get(cmat);
        }
        Log.d(TAG, "found dngProfile:" + (dngProfile != null));
        int orientation = cameraUiWrapper.getActivityInterface().getOrientation();
        saveRawToDng(file,data, fnum,focal,exposuretime,iso,orientation,wb,dngProfile,0);
        data = null;

    }
}
