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
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.apis.camera1.Camera1;
import freed.cam.apis.camera1.Camera1Utils;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.event.capture.CaptureStates;
import freed.cam.previewpostprocessing.PreviewController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.dng.DngProfile;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils.FileEnding;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule extends ModuleAbstract<Camera1> implements Camera.PictureCallback
{

    private final String TAG = PictureModule.class.getSimpleName();
    private int burstcount;
    protected CameraHolder cameraHolder;
    protected boolean waitForPicture;
    protected long startcapturetime;
    private boolean isBurstCapture = false;
    protected PreviewController previewController;
    protected ImageManager imageManager;


    public PictureModule(Camera1 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler)
    {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = FreedApplication.getStringFromRessources(R.string.module_picture);
        this.cameraHolder = cameraUiWrapper.getCameraHolder();
        previewController = ActivityFreeDcamMain.previewController();
        imageManager = FreedApplication.imageManager();
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
            String picformat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).getStringValue();
            Log.d(TAG,"startWork:picformat:" + picformat);
            if (picformat.equals(FreedApplication.getStringFromRessources(R.string.dng_)) || picformat.equals(FreedApplication.getStringFromRessources(R.string.bayer_)))
            {
                if (settingsManager.get(SettingKeys.ZSL).isSupported()
                        && cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).getStringValue().equals(FreedApplication.getStringFromRessources(R.string.on_)))
                {
                    Log.d(TAG,"ZSL is on turning it off");
                    cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).setStringValue(FreedApplication.getStringFromRessources(R.string.off_), true);
                    Log.d(TAG,"ZSL state after turning it off:" + cameraUiWrapper.getParameterHandler().get(SettingKeys.ZSL).getIntValue());
                }

            }
            cameraUiWrapper.getParameterHandler().SetPictureOrientation(orientationManager.getCurrentOrientation());
            changeCaptureState(CaptureStates.image_capture_start);
            waitForPicture = true;
            ParameterInterface burst = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst);
            if (burst != null && burst.getViewState() == AbstractParameter.ViewState.Visible && burst.getIntValue()+1 > 1) {
                burstcount = burst.getIntValue()+1;
                isBurstCapture = true;
            }
            else
                burstcount = 1;
            if (settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_)))
                cameraHolder.SetLocation(locationManager.getCurrentLocation());
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
        //cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewFormat).SetValue("yuv420sp",true);
        createPreview();

        ParameterInterface videohdr = cameraUiWrapper.getParameterHandler().get(SettingKeys.VideoHDR);
        if (settingsManager.get(SettingKeys.VideoHDR).isSupported() && !videohdr.getStringValue().equals(FreedApplication.getStringFromRessources(R.string.off_)))
            videohdr.setStringValue(FreedApplication.getStringFromRessources(R.string.off_), true);
        if(settingsManager.isZteAe()) {
            cameraUiWrapper.getParameterHandler().SetZTE_AE();
        }
    }

    private void createPreview()
    {
        Size sizefromCam = new Size(cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureSize).getStringValue());
        List<Size> sizes = new ArrayList<>();
        String[] stringsSizes = cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).getStringValues();
        final Size size;
        for (String s : stringsSizes) {
            sizes.add(new Size(s));
        }
        size = Camera1Utils.getOptimalPreviewSize(sizes, sizefromCam.width, sizefromCam.height, true);

        Log.d(TAG, "set size to " + size.width + "x" + size.height);
        if (!settingsManager.getGlobal(SettingKeys.PREVIEW_POST_PROCESSING_MODE).get().equals(PreviewPostProcessingModes.off.name())) {
            if(size == null || previewController.getSurfaceTexture() == null)
                return;
            cameraHolder.StopPreview();
            previewController.stop();
            cameraHolder.setSurface((Surface) null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                previewController.getSurfaceTexture().setDefaultBufferSize(size.width, size.height);
            }

            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).setStringValue(size.width + "x" + size.height, false);
            Surface surface = new Surface(previewController.getSurfaceTexture());
            previewController.setOutputSurface(surface);
            previewController.setSize(size.width, size.height);
            previewController.setHistogram(false);

            cameraHolder.setSurface(previewController.getInputSurface());
            cameraHolder.fireOnCameraChangedAspectRatioEvent(size);
            cameraHolder.StartPreview();
            previewController.start();
        }
        else
        {
            cameraHolder.StopPreview();
            if (((CameraHolder)cameraHolder).canSetSurfaceDirect()) {
                cameraHolder.setSurface((Surface)null);
                Surface surface = new Surface(previewController.getSurfaceTexture());
                cameraHolder.setSurface(surface);
            }
            else
                ((CameraHolder)cameraHolder).setTextureView(previewController.getSurfaceTexture());

            Log.d(TAG, "set size to " + size.width + "x" + size.height);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.PreviewSize).setStringValue(size.width + "x" + size.height, false);
            previewController.setSize(size.width, size.height);
            previewController.setRotation(size.width, size.height, 0);
            cameraHolder.fireOnCameraChangedAspectRatioEvent(size);
            cameraHolder.StartPreview();
        }

    }

    @Override
    public void DestroyModule() {

    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        if (settingsManager.getGlobal(SettingKeys.PLAY_SHUTTER_SOUND).get())
            soundPlayer.play();
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
        String picFormat = cameraUiWrapper.getParameterHandler().get(SettingKeys.PictureFormat).getStringValue();
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
            cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureLock).setStringValue(FreedApplication.getStringFromRessources(R.string.false_),true);
            cameraUiWrapper.getParameterHandler().get(SettingKeys.ExposureLock).setStringValue(FreedApplication.getStringFromRessources(R.string.true_),true);
        }
        if(settingsManager.get(SettingKeys.needRestartAfterCapture).get())
        {
            MotoPreviewResetLogic();

        }else
            cameraHolder.StartPreview();

    }

    public void MotoPreviewResetLogic()
    {

        if(settingsManager.GetCurrentCamera() == 0) {
            settingsManager.SetCurrentCamera(1);
            CameraThreadHandler.restartCameraAsync();

            settingsManager.SetCurrentCamera(0);
            CameraThreadHandler.restartCameraAsync();
        }else {
            settingsManager.SetCurrentCamera(0);
            CameraThreadHandler.restartCameraAsync();

            settingsManager.SetCurrentCamera(1);
            CameraThreadHandler.restartCameraAsync();
        }
    }

    private void ShutterResetLogic()
    {
        ParameterInterface expotime = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_ExposureTime);
        if(!expotime.getStringValue().contains("/") && !expotime.getStringValue().contains("auto"))
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetZTE_RESET_AE_SETSHUTTER(expotime.getStringValue());
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
        if(settingsManager.isZteAe())
            ShutterResetLogic();

        //fireInternalOnWorkFinish(toSave);
    }

    @Override
    public void internalFireOnWorkDone(BaseHolder file) {
        fireOnWorkFinish(file);
    }

    private String getFileEnding(String picFormat)
    {
        if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.jpeg_)))
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
            return new File(fileListController.getNewFilePathBurst(settingsManager.GetWriteExternal(), fileending, burstcount));
        else
            return new File(fileListController.getNewFilePath(settingsManager.GetWriteExternal(), fileending));
    }

    protected void saveJpeg(byte[] data, File file)
    {
        ImageSaveTask task = new ImageSaveTask(this);
        task.setBytesTosave(data,ImageSaveTask.JPEG);
        task.setFilePath(file, settingsManager.GetWriteExternal());
        imageManager.putImageSaveTask(task);
    }

    protected void saveDng(byte[] data, File file)
    {
        ImageSaveTask task = new ImageSaveTask(this);
        task.setFnum(((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFnumber());
        task.setFocal(((ParametersHandler)cameraUiWrapper.getParameterHandler()).getFocal());
        float exposuretime = cameraUiWrapper.getParameterHandler().getCurrentExposuretime();
        if (exposuretime == 0 && startcapturetime != 0)
        {
            exposuretime = new Date().getTime() - startcapturetime;
        }
        task.setExposureTime(exposuretime);
        try {
            task.setFlash((int)((ParametersHandler) cameraUiWrapper.getParameterHandler()).getFlash());
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }

        task.setIso(cameraUiWrapper.getParameterHandler().getCurrentIso());
        String wb = null;

        ParameterInterface wbct = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Whitebalance);
        if (wbct != null && wbct.getViewState() == AbstractParameter.ViewState.Visible)
        {
            wb = wbct.getStringValue();
            if (wb.equals(FreedApplication.getStringFromRessources(R.string.auto_)))
                wb = null;
            Log.d(this.TAG,"Set Manual WhiteBalance:"+ wb);
            task.setWhiteBalance(wb);
        }
        DngProfile dngProfile = settingsManager.getDngProfilesMap().get((long)data.length);
        String cmat = settingsManager.get(SettingKeys.MATRIX_SET).get();
        if (cmat != null && !TextUtils.isEmpty(cmat)&&!cmat.equals("off")) {
            dngProfile.matrixes = settingsManager.getMatrixesMap().get(cmat);
        }
        task.setDngProfile(dngProfile);
        Log.d(TAG, "found dngProfile:" + (dngProfile != null));
        if (settingsManager.getIsFrontCamera())
            task.setOrientation(orientationManager.getCurrentOrientation()+180);
        else
            task.setOrientation(orientationManager.getCurrentOrientation());
        task.setFilePath(file, settingsManager.GetWriteExternal());
        task.setBytesTosave(data,ImageSaveTask.RAW10);
        if (!settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.off_)))
            task.setLocation(locationManager.getCurrentLocation());
        imageManager.putImageSaveTask(task);
    }
}
