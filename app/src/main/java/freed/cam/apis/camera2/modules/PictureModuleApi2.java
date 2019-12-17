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

package freed.cam.apis.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.RenderScript.RSErrorHandler;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraValuesChangedCaptureCallback;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.ImageCaptureHolder;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.renderscript.RenderScriptProcessor;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2 implements ImageCaptureHolder.RdyToSaveImg
{
    private final String TAG = PictureModuleApi2.class.getSimpleName();
    private String picFormat;
    protected Output output;
    protected ImageReader jpegReader;
    protected ImageReader rawReader;
    private final int STATE_WAIT_FOR_PRECAPTURE = 0;
    private final int STATE_WAIT_FOR_NONPRECAPTURE = 1;
    private final int STATE_PICTURE_TAKEN = 2;
    private final int STATE_WAITING_LOCK = 3;
    private int mState = STATE_PICTURE_TAKEN;
    private long mCaptureTimer;
    private static final long PRECAPTURE_TIMEOUT_MS = 1000;
    protected ImageCaptureHolder currentCaptureHolder;
    private final int MAX_IMAGES = 6;
    protected List<File> filesSaved;

    private boolean isBurstCapture = false;


    /*private boolean captureDng = false;
    private boolean captureJpeg = false;*/
    private CaptureType captureType;
    protected Camera2Fragment cameraUiWrapper;
    private boolean renderScriptError5 = false;

    protected static class BurstCounter
    {
        private static int burstCount = 1;
        private static int imageCaptured = 0;

        public static synchronized void setBurstCount(int burstCount1)
        {
            burstCount = burstCount1;
        }

        public static synchronized int getBurstCount()
        {
            return burstCount;
        }

        public static synchronized void increase()
        {
            imageCaptured++;
        }

        public static synchronized void resetImagesCaptured()
        {
            imageCaptured = 0;
        }

        public static synchronized int getImageCaptured()
        {
            return imageCaptured;
        }

    }


    //use to workaround the problem with activated renderscript when switching back from a non renderscript session
    private class MyRSErrorHandler extends RSErrorHandler
    {
        @Override
        public void run() {
            super.run();
            Log.e(MyRSErrorHandler.class.getSimpleName(), mErrorNum +":"+ mErrorMessage);
            if (mErrorNum == 5) // Error:5 setting IO output buffer usage.
            {
                renderScriptError5 = true;
                if (renderScriptError5)
                {
                    renderScriptError5 = false;
                    //clear the error else it trigger over and over....
                    mErrorNum = 0;
                    mErrorMessage = null;
                    //Restart the module
                    mBackgroundHandler.post(() -> {
                        Log.e(TAG, "RS5 ERROR; RELOAD MODULE");
                        cameraUiWrapper.getModuleHandler().setModule(cameraUiWrapper.getModuleHandler().getCurrentModule().ModuleName());
                    });
                }
            }
        }
    }

    public PictureModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        this.cameraUiWrapper = (Camera2Fragment)cameraUiWrapper;
        name = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_picture);
        filesSaved = new ArrayList<>();
        ((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(new MyRSErrorHandler());
    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        Log.d(TAG, "InitModule");
        changeCaptureState(CaptureStates.image_capture_stop);

        cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst).SetValue(0, true);
        startPreview();
    }

    @Override
    public void DestroyModule()
    {
        Log.d(TAG, "DestroyModule");
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        cameraUiWrapper.getFocusPeakProcessor().kill();
        if (rawReader != null){
            rawReader.close();
            rawReader = null;
        }
        if (jpegReader != null){
            jpegReader.close();
            jpegReader = null;
        }
    }

    @Override
    public void DoWork()
    {
        Log.d(TAG, "startWork: start new progress");
        if(!isWorking)
            mBackgroundHandler.post(()->TakePicture());
        else if (isWorking)
        {
            mBackgroundHandler.post(()->{
                Log.d(TAG, "cancel capture");
                cameraUiWrapper.captureSessionHandler.cancelCapture();
                finishCapture();
                changeCaptureState(CaptureStates.image_capture_stop);
            });

        }
    }

    @Override
    public void startPreview() {


        Log.d(TAG, "Start Preview");
        setOutputSizes();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d(TAG, "sensorOrientation:" + sensorOrientation);
        int orientationToSet = (360 + sensorOrientation)%360;
        if (SettingsManager.get(SettingKeys.orientationHack).get())
            orientationToSet = (360 + sensorOrientation+180)%360;
        Log.d(TAG, "orientation to set :" +orientationToSet);
        cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.JPEG_ORIENTATION, orientationToSet);

        // Here, we create a CameraCaptureSession for camera preview

        Size previewSize = cameraUiWrapper.getSizeForPreviewDependingOnImageSize(ImageFormat.YUV_420_888, output.jpeg_width, output.jpeg_height);

        SurfaceTexture texture = cameraUiWrapper.getTexturView().getSurfaceTexture();
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

        Surface previewsurface = new Surface(texture);
        final int w = previewSize.getWidth();
        final int h = previewSize.getHeight();

        Log.d(TAG, "Preview size to set : " + w + "x" +h);

        if (SettingsManager.get(SettingKeys.EnableRenderScript).get()) {
            Log.d(TAG, "RenderScriptPreview");
            int rotation = 0;
            switch (orientationToSet)
            {
                case 90:
                    rotation = 0;
                    break;
                case 180:
                    rotation =90;
                    break;
                case 270: rotation = 180;
                    break;
                case 0: rotation = 270;
                    break;
            }

            final int or = rotation;
            Log.d(TAG, "rotation to set : " + or);
            mainHandler.post(() -> cameraUiWrapper.captureSessionHandler.SetTextureViewSize(w, h,or,or+180,true));

            cameraUiWrapper.getFocusPeakProcessor().Reset(previewSize.getWidth(), previewSize.getHeight(),previewsurface);

            Surface camerasurface = cameraUiWrapper.getFocusPeakProcessor().getInputSurface();
            cameraUiWrapper.captureSessionHandler.AddSurface(camerasurface, true);
            cameraUiWrapper.getFocusPeakProcessor().start();
        }
        else
        {
            Log.d(TAG, "Normal Preview");
            int rotation = 0;
            switch (orientationToSet)
            {
                case 90:
                    rotation = 270;
                    break;
                case 180:
                    rotation =180;
                    break;
                case 270: rotation = 270;
                    break;
                case 0: rotation = 180;
                    break;
            }
            final int or = rotation;
            Log.d(TAG, "rotation to set : " + or);
            cameraUiWrapper.captureSessionHandler.AddSurface(previewsurface, true);
            mainHandler.post(() -> cameraUiWrapper.captureSessionHandler.SetTextureViewSize(w, h, or,or+180,false));
        }


        if (jpegReader != null)
            cameraUiWrapper.captureSessionHandler.AddSurface(jpegReader.getSurface(),false);
        if (rawReader != null)
            cameraUiWrapper.captureSessionHandler.AddSurface(rawReader.getSurface(),false);

        cameraUiWrapper.captureSessionHandler.CreateCaptureSession();

        cameraUiWrapper.captureSessionHandler.createImageCaptureRequestBuilder();
        if (jpegReader != null)
            cameraUiWrapper.captureSessionHandler.setImageCaptureSurface(jpegReader.getSurface());
        if (rawReader != null)
            cameraUiWrapper.captureSessionHandler.setImageCaptureSurface(rawReader.getSurface());
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).fireStringValueChanged(parameterHandler.get(SettingKeys.M_Burst).GetStringValue());
        cameraUiWrapper.firePreviewOpen();

    }

    private void setOutputSizes() {

        picFormat = SettingsManager.get(SettingKeys.PictureFormat).get();
        if (TextUtils.isEmpty(picFormat)) {
            picFormat = SettingsManager.getInstance().getResString(R.string.pictureformat_jpeg);
            SettingsManager.get(SettingKeys.PictureFormat).set(picFormat);
            parameterHandler.get(SettingKeys.PictureFormat).fireStringValueChanged(picFormat);

        }
        FindOutputHelper findOutputHelper = new FindOutputHelper();
        if (SettingsManager.getInstance().getFrameWork() == Frameworks.HuaweiCamera2Ex)
        {
            output = findOutputHelper.getHuaweiOutput(cameraHolder);
        }
        else {
            output = findOutputHelper.getStockOutput(cameraHolder);
        }

        //create new ImageReader with the size and format for the image, its needed for p9 else dual or single cam ignores expotime on a dng only capture....
        jpegReader = ImageReader.newInstance(output.jpeg_width, output.jpeg_height, ImageFormat.JPEG, MAX_IMAGES);
        Log.d(TAG, "ImageReader JPEG");
        if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_jpeg))) {
            captureType = CaptureType.Jpeg;
        }

        if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16)) || picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_jpg_p_dng))) {
            Log.d(TAG, "ImageReader RAW_SENSOR");
            if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng16))) {
                captureType = CaptureType.Dng16;
            } else {
                captureType = CaptureType.JpegDng16;
            }
        }
        else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng10))) {
            Log.d(TAG, "ImageReader RAW10");
            captureType = CaptureType.Dng10;
        }
        else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_dng12))) {
            Log.d(TAG, "ImageReader RAW12");
            captureType= CaptureType.Dng12;
        }
        else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer))) {
            Log.d(TAG, "ImageReader BAYER16");
            captureType = CaptureType.Bayer16;
        }
        else if (picFormat.equals(SettingsManager.getInstance().getResString(R.string.pictureformat_bayer10)))
        {
            Log.d(TAG, "ImageReader BAYER10");
            captureType = CaptureType.Bayer10;
        }

        if (output.raw_format != 0)
        {
            rawReader = ImageReader.newInstance(output.raw_width, output.raw_height, output.raw_format, MAX_IMAGES);
        }
        else if (rawReader != null)
        {
            cameraUiWrapper.captureSessionHandler.RemoveSurface(rawReader.getSurface());
            rawReader.close();
            rawReader = null;
        }
    }


    @Override
    public void stopPreview()
    {
        DestroyModule();
    }

    protected void TakePicture()
    {
        isWorking = true;
            Log.d(TAG, SettingsManager.get(SettingKeys.PictureFormat).get());
        BurstCounter.resetImagesCaptured();
        BurstCounter.setBurstCount(Integer.parseInt(parameterHandler.get(SettingKeys.M_Burst).GetStringValue()));
        if (BurstCounter.getBurstCount() > 1)
            isBurstCapture = true;
        else
            isBurstCapture =false;
        onStartTakePicture();

        if (SettingsManager.getInstance().hasCamera2Features() && (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) != CaptureRequest.CONTROL_AE_MODE_OFF)
                || cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) != CaptureRequest.CONTROL_AF_MODE_OFF) {
            startPreCapture();
        }
        else
        {
            Log.d(TAG, "captureStillPicture");
            captureStillPicture();
        }
    }

    private boolean isContAutoFocus()
    {
        return cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE ||
                cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO;
    }

    private void startPreCapture() {
        PictureModuleApi2.this.setCaptureState(STATE_WAIT_FOR_PRECAPTURE);
        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(new CameraValuesChangedCaptureCallback.WaitForAe_Af_Lock() {
            @Override
            public void on_Ae_Af_Lock(boolean af_locked, boolean ae_locked) {
                Log.d(TAG, "ae locked: " + ae_locked +" af locked: " + af_locked);
                if (isContAutoFocus()) {
                    if (af_locked && ae_locked || hitTimeoutLocked()) {
                        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                        setCaptureState(STATE_PICTURE_TAKEN);
                        captureStillPicture();
                    }
                }
                else if (ae_locked || hitTimeoutLocked()) {
                    cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                    setCaptureState(STATE_PICTURE_TAKEN);
                    captureStillPicture();
                }
            }
        });
        Log.d(TAG,"Start AE Precapture");
        startTimerLocked();

        if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) != CaptureRequest.CONTROL_AF_MODE_OFF)
            cameraUiWrapper.captureSessionHandler.StartAePrecapture(cameraUiWrapper.cameraBackroundValuesChangedListner);
        if (isContAutoFocus())
            cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START);
    }

    protected void onStartTakePicture()
    {

    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */
    protected void captureStillPicture() {

        Log.d(TAG,"########### captureStillPicture ###########");
        currentCaptureHolder = new ImageCaptureHolder(cameraHolder.characteristics, captureType, cameraUiWrapper.getActivityInterface(),this,this, this);
        currentCaptureHolder.setFilePath(getFileString(), SettingsManager.getInstance().GetWriteExternal());
        currentCaptureHolder.setForceRawToDng(SettingsManager.get(SettingKeys.forceRawToDng).get());
        currentCaptureHolder.setToneMapProfile(((ToneMapChooser)cameraUiWrapper.getParameterHandler().get(SettingKeys.TONEMAP_SET)).getToneMap());
        currentCaptureHolder.setSupport12bitRaw(SettingsManager.get(SettingKeys.support12bitRaw).get());
        currentCaptureHolder.setOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
        Log.d(TAG, "Capture Type: " + captureType);
        Log.d(TAG, "captureStillPicture ImgCount:"+ BurstCounter.getImageCaptured() +  " ImageCaptureHolder Path:" + currentCaptureHolder.getFilepath());

        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.LOCATION_MODE).GetStringValue().equals(SettingsManager.getInstance().getResString(R.string.on_)))
        {
            currentCaptureHolder.setLocation(cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation());
            cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.JPEG_GPS_LOCATION,cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation());
        }
        cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.JPEG_ORIENTATION, cameraUiWrapper.getActivityInterface().getOrientation());

        String cmat = SettingsManager.get(SettingKeys.MATRIX_SET).get();
        if (cmat != null && !TextUtils.isEmpty(cmat) &&!cmat.equals("off")) {
            currentCaptureHolder.setCustomMatrix(SettingsManager.getInstance().getMatrixesMap().get(cmat));
        }
        if (jpegReader != null)
            jpegReader.setOnImageAvailableListener(currentCaptureHolder,mBackgroundHandler);
        if (rawReader != null)
        {
            rawReader.setOnImageAvailableListener(currentCaptureHolder,mBackgroundHandler);
        }

        //cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        //cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        cameraUiWrapper.captureSessionHandler.StartImageCapture(currentCaptureHolder, mBackgroundHandler);
    }

    protected void prepareCaptureBuilder(int captureNum)
    {

    }

    private String getCaptureState(int state)
    {
        switch (state)
        {
            case STATE_WAIT_FOR_PRECAPTURE:
                return "STATE_WAIT_FOR_PRECAPTURE";
            case STATE_PICTURE_TAKEN:
                return "STATE_PICTURE_TAKEN";
            case STATE_WAIT_FOR_NONPRECAPTURE:
                return "STATE_WAIT_FOR_NONPRECAPTURE";
            default:
                return "";
        }
    }

    private void setCaptureState(int state)
    {
        mState = state;
        Log.d(TAG, "mState:" + getCaptureState(state));
    }

    private void startTimerLocked() {
        mCaptureTimer = SystemClock.elapsedRealtime();
    }

    private boolean hitTimeoutLocked() {
        return (SystemClock.elapsedRealtime() - mCaptureTimer) > PRECAPTURE_TIMEOUT_MS;
    }

    protected String getFileString()
    {
        if (BurstCounter.getBurstCount() > 1)
            return cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), "_" + BurstCounter.getImageCaptured());
        else
            return cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(),"");
    }

    /**
     * Reset the capturesession to preview
     *
     */
    protected void finishCapture() {
        isWorking = false;
        changeCaptureState(CaptureStates.image_capture_stop);
        try
        {
            BurstCounter.increase();
            Log.d(TAG,"finished Capture:" + BurstCounter.getImageCaptured() + "isBurst:" + isBurstCapture);
            if (BurstCounter.getBurstCount()  > BurstCounter.getImageCaptured()) {
                captureStillPicture();
            }
            else if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF &&
                    cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME)> AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME) {
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_FRAME_DURATION, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME);
                Log.d(TAG, "CancelRepeatingCaptureSessoion set onSessionRdy");
                cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
                onSesssionRdy();
            }
            else {
                onSesssionRdy();
            }
        }
        catch (NullPointerException ex) {
            Log.WriteEx(ex);
        }
    }

    protected void onSesssionRdy()
    {
        Log.d(TAG, "onSessionRdy() ######################### Rdy to Start Preview, CAPTURE CYCLE DONE #####################");
        cameraUiWrapper.captureSessionHandler.StartRepeatingCaptureSession();
        if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                || cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
            cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        }
        cameraUiWrapper.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_IDLE,true);
    }


    @Override
    public void internalFireOnWorkDone(File file)
    {
        Log.d(TAG, "internalFireOnWorkDone isBurst" + isBurstCapture + " burstCount/imagecount:" + BurstCounter.getBurstCount() + "/" +BurstCounter.getImageCaptured());
        if (workFinishEventsListner != null)
            workFinishEventsListner.internalFireOnWorkDone(file);
        else {
            Log.d(TAG, "internalFireOnWorkDone BurstCount:" + BurstCounter.getBurstCount() + " imageCount:" + BurstCounter.getImageCaptured());
            if (isBurstCapture && BurstCounter.getBurstCount() >= BurstCounter.getImageCaptured()) {
                filesSaved.add(file);
                Log.d(TAG, "internalFireOnWorkDone Burst addFile");
            }
            if (isBurstCapture && BurstCounter.getBurstCount() == BurstCounter.getImageCaptured()) {
                Log.d(TAG, "internalFireOnWorkDone Burst done");
                fireOnWorkFinish(filesSaved.toArray(new File[filesSaved.size()]));
                filesSaved.clear();
            } else if (!isBurstCapture)
                fireOnWorkFinish(file);
        }
    }

    @Override
    public void fireOnWorkFinish(File file) {
        if (workFinishEventsListner != null)
        {
            workFinishEventsListner.fireOnWorkFinish(file);
        }
        else
            super.fireOnWorkFinish(file);
    }

    @Override
    public void fireOnWorkFinish(File[] files) {
        Log.d(TAG,"fireOnWorkFinish");
        if (workFinishEventsListner != null)
            workFinishEventsListner.fireOnWorkFinish(files);
        else {
            super.fireOnWorkFinish(files);
        }
    }

    @Override
    public void onRdyToSaveImg(ImageCaptureHolder holder) {
        //holder.getRunner().run();

        Log.d(TAG,"onRdyToSaveImg");
        finishCapture();
    }


}
