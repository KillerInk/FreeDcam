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
import android.location.Location;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.SystemClock;
import android.renderscript.RenderScript.RSErrorHandler;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.Camera2Fragment;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.CameraValuesChangedCaptureCallback;
import freed.cam.apis.camera2.modules.capture.AbstractImageCapture;
import freed.cam.apis.camera2.modules.capture.ByteImageCapture;
import freed.cam.apis.camera2.modules.capture.CaptureController;
import freed.cam.apis.camera2.modules.capture.JpegCapture;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.modules.capture.StillImageCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.helper.RdyToSaveImg;
import freed.cam.apis.camera2.parameters.ae.AeManagerCamera2;
import freed.cam.events.CameraStateEvents;
import freed.file.holder.BaseHolder;
import freed.renderscript.RenderScriptProcessor;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.OrientationUtil;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2 implements RdyToSaveImg
{
    private final String TAG = PictureModuleApi2.class.getSimpleName();
    private String picFormat;
    protected Output output;
    //protected ImageReader jpegReader;
    //protected ImageReader rawReader;
    private final int STATE_WAIT_FOR_PRECAPTURE = 0;
    private final int STATE_WAIT_FOR_NONPRECAPTURE = 1;
    private final int STATE_PICTURE_TAKEN = 2;
    private final int STATE_WAITING_LOCK = 3;
    private int mState = STATE_PICTURE_TAKEN;
    private long mCaptureTimer;
    private static final long PRECAPTURE_TIMEOUT_MS = 1000;
    //private ImageCaptureHolder currentCaptureHolder;
    private final int MAX_IMAGES = 8;
    protected List<BaseHolder> filesSaved;

    private boolean isBurstCapture = false;

    private final int max_images = 5;

    /*private boolean captureDng = false;
    private boolean captureJpeg = false;*/
    protected CaptureType captureType;
    protected Camera2Fragment cameraUiWrapper;
    protected CaptureController captureController;

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

    public PictureModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        this.cameraUiWrapper = (Camera2Fragment)cameraUiWrapper;
        name = FreedApplication.getStringFromRessources(R.string.module_picture);
        filesSaved = new ArrayList<>();

    }

    @Override
    public String LongName() {
        return "Picture";
    }

    @Override
    public String ShortName() {
        return "Pic";
    }

    public CaptureController getCaptureController()
    {
        return new CaptureController(this::onRdyToSaveImg);
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        ((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(new MyRSErrorHandler());
        Log.d(TAG, "InitModule");
        changeCaptureState(CaptureStates.image_capture_stop);
        captureController = getCaptureController();
        cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst).SetValue(0, true);
        startPreview();
    }

    @Override
    public void DestroyModule()
    {
        captureController.clear();
        Log.d(TAG, "DestroyModule");
        cameraUiWrapper.captureSessionHandler.CloseCaptureSession();
        cameraUiWrapper.getFocusPeakProcessor().kill();
        ((RenderScriptProcessor)cameraUiWrapper.getFocusPeakProcessor()).setRenderScriptErrorListner(null);
    }

    @Override
    public void DoWork()
    {
        Log.d(TAG, "startWork: start new progress");
        if(!isWorking)
            mBackgroundHandler.post(()-> takePicture());
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
        setOutputSizesAndCreateImageReader();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        Log.d(TAG, "sensorOrientation:" + sensorOrientation);
        int orientationToSet = (360 + sensorOrientation)%360;
        Log.d(TAG, "orientation to set :" +orientationToSet);

        // Here, we create a CameraCaptureSession for camera preview

        Size previewSize = cameraUiWrapper.getSizeForPreviewDependingOnImageSize(ImageFormat.YUV_420_888, output.jpeg_width, output.jpeg_height);

        preparePreviewTextureView(orientationToSet, previewSize);

        for (AbstractImageCapture s : captureController.getImageCaptures())
            cameraUiWrapper.captureSessionHandler.AddSurface(s.getSurface(),false);

        cameraUiWrapper.captureSessionHandler.CreateCaptureSession();

        cameraUiWrapper.captureSessionHandler.createImageCaptureRequestBuilder();
        for (AbstractImageCapture s : captureController.getImageCaptures())
            cameraUiWrapper.captureSessionHandler.setImageCaptureSurface(s.getSurface());
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).fireStringValueChanged(parameterHandler.get(SettingKeys.M_Burst).GetStringValue());
        CameraStateEvents.firePreviewOpenEvent();

    }

    private void preparePreviewTextureView(int orientationToSet, Size previewSize) {
        SurfaceTexture texture = cameraUiWrapper.getTexturView().getSurfaceTexture();
        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
       /* if (cameraUiWrapper.getPreviewSurface() != null)
            cameraUiWrapper.getPreviewSurface().release();*/
        Surface previewsurface = new Surface(cameraUiWrapper.getTexturView().getSurfaceTexture());
        int w = previewSize.getWidth();
        int h = previewSize.getHeight();


        Log.d(TAG, "Preview size to set : " + w + "x" +h);

        if (SettingsManager.getGlobal(SettingKeys.EnableRenderScript).get()) {
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
            final int or = OrientationUtil.getOrientation(rotation);
            if (!SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
                if (or == 90 || or == 270) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            } else
            {
                if (or == 0 || or == 180) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            Log.d(TAG, "rotation to set : " + or);
            int finalW = w;
            int finalH = h;
            mainHandler.post(() -> cameraUiWrapper.captureSessionHandler.SetTextureViewSize(finalW, finalH,or,true));

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
                    rotation =0;
                    break;
                case 270: rotation = 270;
                    break;
                case 0: rotation = 180;
                    break;
            }

            final int or = OrientationUtil.getOrientation(rotation);;
            Log.d(TAG, "rotation to set : " + or);
            if (!SettingsManager.get(SettingKeys.SWITCH_ASPECT_RATIO).get()) {
                if (or == 0 || or == 180) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            else
            {
                if (or == 90 || or == 270) {
                    w = previewSize.getHeight();
                    h = previewSize.getWidth();
                }
            }
            int finalW1 = w;
            int finalH1 = h;
            mainHandler.post(() -> cameraUiWrapper.captureSessionHandler.SetTextureViewSize(finalW1, finalH1, or,false));
            cameraUiWrapper.captureSessionHandler.AddSurface(previewsurface, true);
        }
    }

    private void setOutputSizesAndCreateImageReader() {

        picFormat = SettingsManager.get(SettingKeys.PictureFormat).get();
        if (TextUtils.isEmpty(picFormat)) {
            picFormat = FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg);
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


        Log.d(TAG, "ImageReader JPEG");
        getCaptureType();
        //create new ImageReader with the size and format for the image, its needed for p9 else dual or single cam ignores expotime on a dng only capture....
        //if (captureType == CaptureType.Jpeg)
        createImageCaptureListners();
    }

    protected void createImageCaptureListners() {
        ByteImageCapture byteImageCapture;
        if (captureType == CaptureType.Jpeg || captureType == CaptureType.JpegDng16 || captureType == CaptureType.JpegDng10)
            byteImageCapture = new JpegCapture(new Size(output.jpeg_width,output.jpeg_height),false,cameraUiWrapper.getActivityInterface(),this,".jpg",max_images);
        else
        {
            Size smallestImageSize = Collections.min(
                    Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.JPEG)),
                    new CameraHolderApi2.CompareSizesByArea());
            byteImageCapture = new JpegCapture(smallestImageSize,false,cameraUiWrapper.getActivityInterface(),this,".jpg",max_images);
        }
        captureController.add(byteImageCapture);
        if (captureType == CaptureType.Yuv) {
            String yuvsize = SettingsManager.get(SettingKeys.YuvSize).get();
            String[] split = yuvsize.split("x");
            Size s = new Size(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
            ByteImageCapture yuvimgcapture = new ByteImageCapture(s, ImageFormat.YUV_420_888,false,cameraUiWrapper.getActivityInterface(),this,".yuv",max_images);
            captureController.add(yuvimgcapture);
        }

        if (output.raw_format != 0 && isDngCapture())
        {
            RawImageCapture rawImageCapture = new RawImageCapture(new Size(output.raw_width,output.raw_height),output.raw_format,false,cameraUiWrapper.getActivityInterface(),this,".dng",max_images);
            captureController.add(rawImageCapture);
            //rawReader = ImageReader.newInstance(output.raw_width, output.raw_height, output.raw_format, MAX_IMAGES);
        }
        else if (output.raw_format != 0 &&  isBayerCapture())
        {
            ByteImageCapture byteImageCapture1 = new ByteImageCapture(new Size(output.raw_width,output.raw_height),output.raw_format,false,cameraUiWrapper.getActivityInterface(),this,".bayer",max_images);
            captureController.add(byteImageCapture1);
        }
    }

    private boolean isDngCapture()
    {
        return captureType == CaptureType.JpegDng10  || captureType == CaptureType.JpegDng16 || captureType == CaptureType.Dng16 || captureType == CaptureType.Dng10;
    }

    private boolean isBayerCapture()
    {
        return captureType == CaptureType.Bayer10 || captureType == CaptureType.Bayer16;
    }

    private void getCaptureType() {
        if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpeg))) {
            captureType = CaptureType.Jpeg;
        }
        if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_yuv))) {
            captureType = CaptureType.Yuv;
        }

        if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16)) || picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_jpg_p_dng))) {
            Log.d(TAG, "ImageReader RAW_SENSOR");
            if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng16))) {
                captureType = CaptureType.Dng16;
            } else {
                captureType = CaptureType.JpegDng16;
            }
        }
        else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng10))) {
            Log.d(TAG, "ImageReader RAW10");
            captureType = CaptureType.Dng10;
        }
        else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_dng12))) {
            Log.d(TAG, "ImageReader RAW12");
            captureType= CaptureType.Dng12;
        }
        else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer))) {
            Log.d(TAG, "ImageReader BAYER16");
            captureType = CaptureType.Bayer16;
        }
        else if (picFormat.equals(FreedApplication.getStringFromRessources(R.string.pictureformat_bayer10)))
        {
            Log.d(TAG, "ImageReader BAYER10");
            captureType = CaptureType.Bayer10;
        }

    }


    @Override
    public void stopPreview()
    {
        DestroyModule();
    }

    protected void takePicture()
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
            public void on_Ae_Af_Lock(CameraValuesChangedCaptureCallback.AeAfLocker aeAfLocker) {
                Log.d(TAG, "ae locked: " + aeAfLocker.getAeLock() +" af locked: " + aeAfLocker.getAfLock());
                if (mState == STATE_WAIT_FOR_PRECAPTURE) {
                    if (isContAutoFocus()) {
                        if ((aeAfLocker.getAfLock() && aeAfLocker.getAeLock()) || hitTimeoutLocked()) {
                            cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                            setCaptureState(STATE_PICTURE_TAKEN);
                            captureStillPicture();
                        }
                    } else if (aeAfLocker.getAeLock() || hitTimeoutLocked()) {
                        cameraUiWrapper.cameraBackroundValuesChangedListner.setWaitForAe_af_lock(null);
                        setCaptureState(STATE_PICTURE_TAKEN);
                        captureStillPicture();
                    }
                }
            }
        });
        Log.d(TAG,"Start AE Precapture");
        startTimerLocked();

        if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) != CaptureRequest.CONTROL_AF_MODE_OFF)
            cameraUiWrapper.captureSessionHandler.StartAePrecapture();
        if (isContAutoFocus())
            cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_START,false);
        cameraUiWrapper.captureSessionHandler.capture();
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

        Location currentLocation = null;
        if (cameraUiWrapper.getParameterHandler().get(SettingKeys.LOCATION_MODE).GetStringValue().equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            currentLocation = cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation();
            Log.d(TAG,"currentLocation null:" +(currentLocation == null));
            if (currentLocation != null)
                cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.JPEG_GPS_LOCATION,currentLocation);
        }

        for (int i = 0; i< captureController.getImageCaptures().size();i++)
        {
            AbstractImageCapture currentCaptureHolder = captureController.getImageCaptures().get(i);
            if (currentCaptureHolder instanceof StillImageCapture) {
                StillImageCapture stillImageCapture = (StillImageCapture) currentCaptureHolder;
                stillImageCapture.setFilePath(getFileString(), SettingsManager.getInstance().GetWriteExternal());
                stillImageCapture.setForceRawToDng(SettingsManager.get(SettingKeys.forceRawToDng).get());
                stillImageCapture.setToneMapProfile(((ToneMapChooser) cameraUiWrapper.getParameterHandler().get(SettingKeys.TONEMAP_SET)).getToneMap());
                stillImageCapture.setSupport12bitRaw(SettingsManager.get(SettingKeys.support12bitRaw).get());
                stillImageCapture.setOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
                stillImageCapture.setCharacteristics(cameraUiWrapper.getCameraHolder().characteristics);
                stillImageCapture.setCaptureType(captureType);
                if (currentLocation != null)
                    stillImageCapture.setLocation(currentLocation);
                String cmat = SettingsManager.get(SettingKeys.MATRIX_SET).get();
                if (cmat != null && !TextUtils.isEmpty(cmat) && !cmat.equals("off")) {
                    stillImageCapture.setCustomMatrix(SettingsManager.getInstance().getMatrixesMap().get(cmat));
                }
            }
        }


        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.JPEG_ORIENTATION, cameraUiWrapper.getActivityInterface().getOrientation());

        //cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        //cameraUiWrapper.captureSessionHandler.CancelRepeatingCaptureSession();
        prepareCaptureBuilder(BurstCounter.getImageCaptured());
        changeCaptureState(CaptureStates.image_capture_start);
        Log.d(TAG, "StartStillCapture");
        cameraUiWrapper.captureSessionHandler.StopRepeatingCaptureSession();
        cameraUiWrapper.captureSessionHandler.StartImageCapture(captureController, mBackgroundHandler);
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
            return cameraUiWrapper.getActivityInterface().getFileListController().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), "_" + BurstCounter.getImageCaptured());
        else
            return cameraUiWrapper.getActivityInterface().getFileListController().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(),"");
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
            else if (cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) != null &&
                    cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF &&
                    cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME)> AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME) {
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME,true);
                cameraUiWrapper.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_FRAME_DURATION, AeManagerCamera2.MAX_PREVIEW_EXPOSURETIME,true);
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

    private void onSesssionRdy()
    {
        Log.d(TAG, "onSessionRdy() ######################### Rdy to Start Preview, CAPTURE CYCLE DONE #####################");

        if (isContAutoFocus()) {
            cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,
                    CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
        }
        cameraUiWrapper.captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,
                CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
        cameraUiWrapper.captureSessionHandler.StartRepeatingCaptureSession();
    }


    @Override
    public void internalFireOnWorkDone(BaseHolder file)
    {
        Log.d(TAG, "internalFireOnWorkDone BurstCount:" + BurstCounter.getBurstCount() + " imageCount:" + BurstCounter.getImageCaptured());
        if (isBurstCapture && BurstCounter.getBurstCount() >= BurstCounter.getImageCaptured()) {
            filesSaved.add(file);
            Log.d(TAG, "internalFireOnWorkDone Burst addFile");
        }
        if (isBurstCapture && BurstCounter.getBurstCount() == BurstCounter.getImageCaptured()) {
            Log.d(TAG, "internalFireOnWorkDone Burst done");
            fireOnWorkFinish(filesSaved.toArray(new BaseHolder[filesSaved.size()]));
            filesSaved.clear();
        } else if (!isBurstCapture)
            fireOnWorkFinish(file);
    }

    @Override
    public void fireOnWorkFinish(BaseHolder file) {
            super.fireOnWorkFinish(file);
    }

    @Override
    public void fireOnWorkFinish(BaseHolder[] files) {
        Log.d(TAG,"fireOnWorkFinish[]");
        super.fireOnWorkFinish(files);
    }

    @Override
    public void onRdyToSaveImg() {
        Log.d(TAG,"onRdyToSaveImg");
        finishCapture();
    }


}
