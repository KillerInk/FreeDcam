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
import android.text.TextUtils;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Date;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.dng.DngProfile;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
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
    protected long startcapturetime;
    private boolean isBurstCapture = false;


    public PictureModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_picture);
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
        Log.d(this.TAG, "DoWork:isWorking:"+ isWorking + " " + Thread.currentThread().getName());
        if(isWorking){
            Log.d(TAG,"Work in Progress,skip it");
            return;
        }

        mBackgroundHandler.post(() -> {
            isWorking = true;
            String picformat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).GetStringValue();
            Log.d(TAG,"startWork:picformat:" + picformat);
            if (picformat.equals(SettingsManager.getInstance().getResString(R.string.dng_)) || picformat.equals(SettingsManager.getInstance().getResString(R.string.bayer_)))
            {
                if (SettingsManager.get(SettingKeys.ZSL).isSupported()
                        && cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).GetStringValue().equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)))
                {
                    Log.d(TAG,"ZSL is on turning it off");
                    cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_), true);
                    Log.d(TAG,"ZSL state after turning it off:" + cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).GetValue());
                }

            }
            cameraUiWrapper.getParameterHandler().SetPictureOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
            changeCaptureState(CaptureStates.image_capture_start);
            waitForPicture = true;
            ParameterInterface burst = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst);
            if (burst != null && burst.getViewState() == AbstractParameter.ViewState.Visible && burst.GetValue()+1 > 1) {
                burstcount = burst.GetValue()+1;
                isBurstCapture = true;
            }
            else
                burstcount = 1;
            if (SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.on_)))
                cameraHolder.SetLocation(cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation());
            startcapturetime =new Date().getTime();
            cameraHolder.TakePicture(PictureModule.this);
            Log.d(TAG,"TakePicture");
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
        cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("yuv420sp",true);
        ParameterInterface videohdr = cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR);
        if (SettingsManager.get(SettingKeys.VideoHDR).isSupported() && !videohdr.GetStringValue().equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_)))
            videohdr.SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.off_), true);
        if(SettingsManager.getInstance().isZteAe()) {
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_AE();
        }
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        Log.d(this.TAG, "onPictureTaken " + Thread.currentThread().getName());
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
        burstcount--;
        String picFormat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).GetStringValue();
        saveImage(data,picFormat);
        //Handel Burst capture
        if (burstcount == 0)
        {
            isWorking = false;
            waitForPicture = false;
            isBurstCapture = false;
            startPreview();
            changeCaptureState(CaptureStates.image_capture_stop);
        }
    }

    protected void startPreview()
    {
        Log.d(this.TAG, "startPreview " + Thread.currentThread().getName());
        //workaround to keep ae locked
        if (cameraHolder.GetCameraParameters().getAutoExposureLock())
        {
            cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureLock).SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.false_),true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureLock).SetValue(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.true_),true);
        }
        if(SettingsManager.get(SettingKeys.needRestartAfterCapture).get())
        {
            MotoPreviewResetLogic();

        }else
            cameraHolder.StartPreview();

    }

    public void MotoPreviewResetLogic()
    {

        if(SettingsManager.getInstance().GetCurrentCamera() == 0) {
            SettingsManager.getInstance().SetCurrentCamera(1);
            cameraUiWrapper.stopCameraAsync();
            cameraUiWrapper.startCameraAsync();

            SettingsManager.getInstance().SetCurrentCamera(0);
            cameraUiWrapper.stopCameraAsync();
            cameraUiWrapper.startCameraAsync();
        }else {
            SettingsManager.getInstance().SetCurrentCamera(0);
            cameraUiWrapper.stopCameraAsync();
            cameraUiWrapper.startCameraAsync();

            SettingsManager.getInstance().SetCurrentCamera(1);
            cameraUiWrapper.stopCameraAsync();
            cameraUiWrapper.startCameraAsync();
        }
    }

    private void ShutterResetLogic()
    {
        ParameterInterface expotime = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_ExposureTime);
        if(!expotime.GetStringValue().contains("/") && !expotime.GetStringValue().contains("auto"))
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_RESET_AE_SETSHUTTER(expotime.GetStringValue());
    }

    protected void saveImage(byte[]data, String picFormat)
    {
        Log.d(this.TAG, "saveImage " + Thread.currentThread().getName());
        final File toSave = getFile(getFileEnding(picFormat));
        Log.d(this.TAG, "saveImage:"+toSave.getName() + " Filesize: "+data.length);
        if (picFormat.equals(FileEnding.DNG))
            saveDng(data,toSave);
        else {
            saveJpeg(data,toSave);
        }
        if(SettingsManager.getInstance().isZteAe())
            ShutterResetLogic();

        //fireInternalOnWorkFinish(toSave);
    }

    @Override
    public void internalFireOnWorkDone(File file) {
        fireOnWorkFinish(file);
    }

    private String getFileEnding(String picFormat)
    {
        if (picFormat.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_)))
            return ".jpg";
        else if (picFormat.equals("jps"))
            return  ".jps";
        else if ((picFormat.equals(FileEnding.BAYER) || picFormat.equals(FileEnding.RAW)))
            return ".bayer";
        else if (picFormat.contains(FileEnding.DNG))
            return ".dng";
        return "";
    }

    protected File getFile(String fileending)
    {
        if (isBurstCapture)
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePathBurst(SettingsManager.getInstance().GetWriteExternal(), fileending, burstcount));
        else
            return new File(cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), fileending));
    }

    protected void saveJpeg(byte[] data, File file)
    {
        ImageSaveTask task = new ImageSaveTask(cameraUiWrapper.getActivityInterface(),this);
        task.setBytesTosave(data,ImageSaveTask.JPEG);
        task.setFilePath(file, SettingsManager.getInstance().GetWriteExternal());
        ImageManager.putImageSaveTask(task);
    }

    protected void saveDng(byte[] data, File file)
    {
        ImageSaveTask task = new ImageSaveTask(cameraUiWrapper.getActivityInterface(),this);
        task.setFnum(((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFnumber());
        task.setFocal(((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFocal());
        float exposuretime = cameraUiWrapper.getParameterHandler().getCurrentExposuretime();
        if (exposuretime == 0 && startcapturetime != 0)
        {
            exposuretime = new Date().getTime() - startcapturetime;
        }
        task.setExposureTime(exposuretime);
        task.setFlash((int)((ParametersHandler) cameraUiWrapper.getParameterHandler()).getFlash());
        task.setIso(cameraUiWrapper.getParameterHandler().getCurrentIso());
        String wb = null;

        ParameterInterface wbct = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Whitebalance);
        if (wbct != null && wbct.getViewState() == AbstractParameter.ViewState.Visible)
        {
            wb = wbct.GetStringValue();
            if (wb.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.auto_)))
                wb = null;
            Log.d(this.TAG,"Set Manual WhiteBalance:"+ wb);
            task.setWhiteBalance(wb);
        }
        DngProfile dngProfile = SettingsManager.getInstance().getDngProfilesMap().get((long)data.length);
        String cmat = SettingsManager.get(SettingKeys.MATRIX_SET).get();
        if (cmat != null && !TextUtils.isEmpty(cmat)&&!cmat.equals("off")) {
            dngProfile.matrixes = SettingsManager.getInstance().getMatrixesMap().get(cmat);
        }
        task.setDngProfile(dngProfile);
        Log.d(TAG, "found dngProfile:" + (dngProfile != null));
        if (SettingsManager.getInstance().getIsFrontCamera())
            task.setOrientation(cameraUiWrapper.getActivityInterface().getOrientation()+180);
        else
            task.setOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
        task.setFilePath(file, SettingsManager.getInstance().GetWriteExternal());
        task.setBytesTosave(data,ImageSaveTask.RAW10);
        if (!SettingsManager.get(SettingKeys.LOCATION_MODE).get().equals(SettingsManager.getInstance().getResString(R.string.off_)))
            task.setLocation(cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation());
        ImageManager.putImageSaveTask(task);
    }
}
