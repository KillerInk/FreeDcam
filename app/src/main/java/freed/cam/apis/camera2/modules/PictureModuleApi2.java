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
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Pair;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera2.CameraHolderApi2.CompareSizesByArea;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.apis.camera2.parameters.AeHandler;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.utils.AppSettingsManager;
import freed.utils.Log;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2
{
    private class ImageHolder
    {
        private CaptureResult captureResult;
        private Image image;
        public void SetCaptureResult(CaptureResult captureResult)
        {
            this.captureResult = captureResult;
        }

        public synchronized void SetImage(Image image)
        {
            this.image = image;
        }

        public synchronized boolean rdyToGetSaved()
        {
            return image != null && captureResult != null;
        }

        public synchronized Image getImage()
        {
            return image;
        }

        public CaptureResult getCaptureResult()
        {
            return captureResult;
        }
    }

    private final String TAG = PictureModuleApi2.class.getSimpleName();
    private final String CAPTURECYCLE = "CAPTURECYCLE";
    private String picFormat;
    private int mImageWidth;
    private int mImageHeight;
    protected ImageReader mImageReader;
    protected ImageReader mrawImageReader;
    protected int imagecount;
    private final int STATE_WAIT_FOR_PRECAPTURE = 0;
    private final int STATE_WAIT_FOR_NONPRECAPTURE = 1;
    private final int STATE_PICTURE_TAKEN = 2;
    private int mState = STATE_PICTURE_TAKEN;
    private long mCaptureTimer;
    private static final long PRECAPTURE_TIMEOUT_MS = 1000;
    private boolean intervalCapture = false;
    protected Object captureLock = new Object();
    private ImageHolder jpegHolder;
    private ImageHolder rawHolder;

    public PictureModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_picture);
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
    public void DoWork()
    {
        Log.d(TAG, "startWork: start new progress");
        if(!isWorking)
            mBackgroundHandler.post(TakePicture);
        else if (isWorking)
        {
            cameraHolder.captureSessionHandler.cancelCapture();
            finishCapture(0);
            changeCaptureState(CaptureStates.image_capture_stop);
        }
    }

    @Override
    public void startPreview() {


        Log.d(TAG, "Start Preview");
        setOutputSizes();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int orientationToSet = (360 +cameraUiWrapper.getActivityInterface().getOrientation() + sensorOrientation)%360;
        if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(cameraUiWrapper.getResString(R.string.on_)))
            orientationToSet = (360 +cameraUiWrapper.getActivityInterface().getOrientation() + sensorOrientation+180)%360;
        cameraHolder.captureSessionHandler.SetParameter(CaptureRequest.JPEG_ORIENTATION, orientationToSet);

        // Here, we create a CameraCaptureSession for camera preview

        try {
            Size previewSize = cameraHolder.getSizeForPreviewDependingOnImageSize(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), cameraHolder.characteristics, mImageWidth, mImageHeight);
            if (cameraUiWrapper.getFocusPeakProcessor() != null)
            {
                cameraUiWrapper.getFocusPeakProcessor().kill();
            }
            int orientation = 0;
            switch (sensorOrientation)
            {
                case 90:
                    orientation = 0;
                    break;
                case 180:
                    orientation =90;
                    break;
                case 270: orientation = 180;
                    break;
                case 0: orientation = 270;
                    break;
            }
            final int w = previewSize.getWidth();
            final int h = previewSize.getHeight();
            final int or = orientation;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraHolder.captureSessionHandler.SetTextureViewSize(w, h,or,or+180,false);
                }
            });

            SurfaceTexture texture = cameraHolder.captureSessionHandler.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            Surface previewsurface = new Surface(texture);

            cameraUiWrapper.getFocusPeakProcessor().Reset(previewSize.getWidth(), previewSize.getHeight());
            Log.d(TAG, "Previewsurface vailid:" + previewsurface.isValid());
            cameraUiWrapper.getFocusPeakProcessor().setOutputSurface(previewsurface);
            Surface camerasurface = cameraUiWrapper.getFocusPeakProcessor().getInputSurface();
            cameraHolder.captureSessionHandler.AddSurface(camerasurface,true);

            cameraHolder.captureSessionHandler.AddSurface(mImageReader.getSurface(),false);
            if (mrawImageReader != null)
                cameraHolder.captureSessionHandler.AddSurface(mrawImageReader.getSurface(),false);

            cameraHolder.captureSessionHandler.CreateCaptureSession();

            cameraHolder.captureSessionHandler.createImageCaptureRequestBuilder();
            cameraHolder.captureSessionHandler.setImageCaptureSurface(mImageReader.getSurface());
            if (mrawImageReader != null)
                cameraHolder.captureSessionHandler.setImageCaptureSurface(mrawImageReader.getSurface());
        }
        catch(Exception ex)
        {
            Log.WriteEx(ex);
        }
        if (parameterHandler.Burst != null)
            parameterHandler.Burst.ThrowCurrentValueChanged(parameterHandler.Burst.GetValue()+1);
    }

    private void setOutputSizes() {
        String picSize = appSettingsManager.pictureSize.get();
        Size largestImageSize = Collections.max(
                Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());
        picFormat = appSettingsManager.pictureFormat.get();
        if (picFormat.equals("")) {
            picFormat = appSettingsManager.getResString(R.string.pictureformat_jpeg);
            appSettingsManager.pictureFormat.set(picFormat);
            parameterHandler.PictureFormat.onValueHasChanged(picFormat);

        }

        String[] split = picSize.split("x");
        if (split.length < 2)
        {
            mImageWidth = largestImageSize.getWidth();
            mImageHeight = largestImageSize.getHeight();
        }
        else
        {
            mImageWidth = Integer.parseInt(split[0]);
            mImageHeight = Integer.parseInt(split[1]);
        }
        //create new ImageReader with the size and format for the image
        mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.JPEG, 3);
        Log.d(TAG, "ImageReader JPEG");

        if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng16)))
        {
            Log.d(TAG, "ImageReader RAW_SENOSR");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
            mrawImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW_SENSOR, 3);
        }
        else if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng10)))
        {
            Log.d(TAG, "ImageReader RAW10");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new CompareSizesByArea());
            mImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW10, 3);
        }
        else if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng12)))
        {
            Log.d(TAG, "ImageReader RAW12");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW12)), new CompareSizesByArea());
            mImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW12,3);
        }
        else
            mrawImageReader = null;
    }


    @Override
    public void stopPreview()
    {
        DestroyModule();
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        Log.d(TAG, "InitModule");
        changeCaptureState(CaptureStates.image_capture_stop);
        cameraUiWrapper.getParameterHandler().Burst.SetValue(0);
        startPreview();
    }

    @Override
    public void DestroyModule()
    {
        Log.d(TAG, "DestroyModule");
        cameraHolder.captureSessionHandler.CloseCaptureSession();
        cameraUiWrapper.getFocusPeakProcessor().kill();
        super.DestroyModule();
    }

    private Runnable TakePicture = new Runnable()
    {
        @Override
        public void run() {
            isWorking = true;
            Log.d(TAG, appSettingsManager.pictureFormat.get());
            Log.d(TAG, "dng:" + Boolean.toString(parameterHandler.IsDngActive()));
            imagecount = 0;

            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener,mBackgroundHandler);
            jpegHolder = new ImageHolder();
            if (mrawImageReader != null)
            {
                rawHolder = new ImageHolder();
                mrawImageReader.setOnImageAvailableListener(mOnRawImageAvailableListener,mBackgroundHandler);
            }
            else
                rawHolder = null;
            onStartTakePicture();

            if (appSettingsManager.IsCamera2FullSupported() && cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) != CaptureRequest.CONTROL_AE_MODE_OFF) {
                PictureModuleApi2.this.setCaptureState(STATE_WAIT_FOR_PRECAPTURE);
                Log.d(TAG,"Start AE Precapture");
                startTimerLocked();
                cameraHolder.StartAePrecapture(aecallback);

            }
            else
            {
                Log.d(TAG, "captureStillPicture");
                captureStillPicture();
            }

        }

    };

    public void setIntervalCapture(boolean isInterval)
    {
        intervalCapture = isInterval;
    }

    protected void onStartTakePicture()
    {

    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */
    protected void captureStillPicture() {
        synchronized (captureLock) {

            changeCaptureState(CaptureStates.image_capture_start);
            if (cameraUiWrapper.getParameterHandler().locationParameter.GetValue().equals(appSettingsManager.getResString(R.string.on_)))
            {
                cameraHolder.captureSessionHandler.SetParameter(CaptureRequest.JPEG_GPS_LOCATION,cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
            }

            prepareCaptureBuilder(imagecount);
            Log.d(CAPTURECYCLE, "CancelRepeatingCaptureSessoion set imageRdyCallback");
            cameraHolder.captureSessionHandler.StopRepeatingCaptureSession(imageCaptureRdyCallback);

        }

    }

    private CaptureSessionHandler.CaptureEvent imageCaptureRdyCallback = new CaptureSessionHandler.CaptureEvent() {
        @Override
        public void onRdy() {
            Log.d(CAPTURECYCLE, "StartStillCapture");
            cameraHolder.captureSessionHandler.StartImageCapture(CaptureCallback, mBackgroundHandler);
        }
    };

    protected void prepareCaptureBuilder(int captureNum)
    {

    }

    private String getAeStateString(int ae)
    {
        switch (ae)
        {
            case CaptureResult.CONTROL_AE_STATE_INACTIVE:
                return "CONTROL_AE_STATE_INACTIVE";
            case CaptureResult.CONTROL_AE_STATE_SEARCHING:
                return "CONTROL_AE_STATE_SEARCHING";
            case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                return "CONTROL_AE_STATE_CONVERGED";
            case CaptureResult.CONTROL_AE_STATE_LOCKED:
                return "CONTROL_AE_STATE_LOCKED";
            case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                return "CONTROL_AE_STATE_FLASH_REQUIRED";
            case CaptureResult.CONTROL_AE_STATE_PRECAPTURE:
                return "CONTROL_AE_STATE_PRECAPTURE";
            default:
                return "";
        }
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

    /**
     * get used when a continouse focus mode is active to get best focus and exposure for capture.
     * when both are found or timeout, a capture gets started
     * this get called repeating by the camera till a capture happen
     */
    private CaptureCallback aecallback = new CaptureCallback()
    {
        private void processResult(CaptureResult partialResult)
        {
            Integer aeState = partialResult.get(CaptureResult.CONTROL_AE_STATE);
            if (aeState != null)
                Log.e(TAG, "CurrentCaptureState:" + getCaptureState(mState) + " AE_STATE:" + getAeStateString(aeState));
            switch (mState)
            {
                case STATE_WAIT_FOR_PRECAPTURE:
                    Log.d(TAG,"STATE_WAIT_FOR_PRECAPTURE  AESTATE:" + aeState);
                    if (aeState == null)
                    {
                        setCaptureState(STATE_PICTURE_TAKEN);
                        captureStillPicture();
                    }
                    else if (aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED)
                    {
                        Log.e(TAG,"Wait For nonprecapture");
                        setCaptureState(STATE_WAIT_FOR_NONPRECAPTURE);
                        if (hitTimeoutLocked())
                        {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        }
                    }
                    else if (aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED)
                    {
                        Log.d(TAG,"CONTROL_AE_STATE_CONVERGED captureStillPicture");
                        setCaptureState(STATE_PICTURE_TAKEN);
                        captureStillPicture();
                    }
                    break;
                case STATE_WAIT_FOR_NONPRECAPTURE:
                    Log.d(TAG,"STATE_WAIT_FOR_NONPRECAPTURE");
                    setCaptureState(STATE_PICTURE_TAKEN);
                    captureStillPicture();
                    break;
            }

        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult partialResult)
        {
            processResult(partialResult);
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            processResult(partialResult);
        }
    };

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


    protected final CaptureCallback CaptureCallback = new CaptureCallback()
    {
        private long timestamp;
        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);

            Log.d(CAPTURECYCLE,"onCaptureSequenceCompleted sequenceID: "+ sequenceId + " frameNum:" +frameNumber);
        }

        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            this.timestamp =timestamp;
            Log.d(CAPTURECYCLE, "onCaptureStart() timestamp:" + timestamp + "ID: " + request.getTag() +" frameNum:" + frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            Log.d(CAPTURECYCLE, "onCaptureCompleted FrameNum:" +result.getFrameNumber());
            synchronized (captureLock) {
                jpegHolder.SetCaptureResult(result);
                if (jpegHolder.rdyToGetSaved())
                {
                    saveImage(jpegHolder);
                    jpegHolder = null;
                }
                if (rawHolder != null)
                {
                    rawHolder.SetCaptureResult(result);
                    if (rawHolder.rdyToGetSaved()) {
                        saveImage(rawHolder);
                        rawHolder = null;
                    }
                }

                Log.d(CAPTURECYCLE, "result AE Mode:" + result.get(CaptureResult.CONTROL_AE_MODE) + " Expotime:" + result.get(CaptureResult.SENSOR_EXPOSURE_TIME) + " iso:" + result.get(CaptureResult.SENSOR_SENSITIVITY));
                Log.d(CAPTURECYCLE, "request AE Mode:" + request.get(CaptureRequest.CONTROL_AE_MODE) + " Expotime:" + request.get(CaptureRequest.SENSOR_EXPOSURE_TIME) + " iso:" + request.get(CaptureRequest.SENSOR_SENSITIVITY));
            }
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d(CAPTURECYCLE, "#################CAPTURE FAILED!###############");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
            Log.d(CAPTURECYCLE, "#################CAPTURE BUFFER LOST!############### FrameNum:" +frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            Log.d(TAG, "#################CAPTURE ABORTED!###############");
        }
    };

    private final OnImageAvailableListener mOnImageAvailableListener = new OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(final ImageReader reader)
        {
            synchronized (captureLock) {
                Image img = reader.acquireLatestImage();
                jpegHolder.SetImage(img);
                if (jpegHolder.rdyToGetSaved()) {
                    saveImage(jpegHolder);
                    jpegHolder = null;
                }
            }
        }
    };

    private final OnImageAvailableListener mOnRawImageAvailableListener = new OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(final ImageReader reader)
        {
            synchronized (captureLock)
            {
                Image img = reader.acquireLatestImage();
                rawHolder.SetImage(img);
                if (rawHolder.rdyToGetSaved()) {
                    saveImage(rawHolder);
                    rawHolder = null;
                }
            }
        }
    };


    /**
     * Reset the capturesession to preview
     *
     */
    protected void finishCapture(int burstcount) {
        isWorking = false;
        changeCaptureState(CaptureStates.image_capture_stop);
        try
        {
            Log.d(CAPTURECYCLE, "CaptureDone");
            if (burstcount > 1 && burstcount > imagecount) {
                captureStillPicture();
            }
            else if (cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF) {
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, AeHandler.MAX_PREVIEW_EXPOSURETIME);
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_FRAME_DURATION, AeHandler.MAX_PREVIEW_EXPOSURETIME);
                Log.d(CAPTURECYCLE, "CancelRepeatingCaptureSessoion set onSessionRdy");
                cameraHolder.captureSessionHandler.CancelRepeatingCaptureSession(onSesssionRdy);
            }
            else {
                cameraHolder.captureSessionHandler.StartRepeatingCaptureSession();
                if (cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                        || cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
                    cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                            CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                    cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                            CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
                }

                Log.d(TAG, "BurstCount/Imagecount:" + burstcount+"/"+imagecount + " Interval:" + intervalCapture);

            }

        }
        catch (NullPointerException ex) {
            Log.WriteEx(ex);;
        }



    }

    /**
     * get called when the capture session is rdy to work with
     */
    private CaptureSessionHandler.CaptureEvent onSesssionRdy = new CaptureSessionHandler.CaptureEvent()
    {
        @Override
        public void onRdy() {


            Log.d(CAPTURECYCLE, "onSessionRdy() Rdy to Start Preview");
            cameraHolder.captureSessionHandler.StartRepeatingCaptureSession();
            if (cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    || cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            }

            isWorking = false;
            changeCaptureState(CaptureStates.image_capture_stop);

        }



    };



    private void saveImage(ImageHolder image) {
        int burstcount = parameterHandler.Burst.GetValue()+1;
        String f;
        if (burstcount > 1)
            f = cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), "_" + imagecount);
        else
            f =cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(),"");
        File file = null;
        imagecount++;
        switch (image.getImage().getFormat())
        {
            case ImageFormat.JPEG:
                file = new File(f+".jpg");
                process_jpeg(image.getImage(), file);
                break;
            case ImageFormat.RAW10:
                file = new File(f+".dng");
                process_rawWithDngConverter(image, DngProfile.Mipi,file);
                break;
            case ImageFormat.RAW12:
                file = new File(f+".dng");
                process_rawWithDngConverter(image,DngProfile.Mipi12,file);
                break;
            case ImageFormat.RAW_SENSOR:
                file = new File(f+".dng");
                if(appSettingsManager.isForceRawToDng())
                    process_rawWithDngConverter(image,DngProfile.Mipi16,file);
                else
                    process_rawSensor(image,file);
                break;
        }

        internalFireOnWorkDone(file);
        isWorking = false;
        finishCapture(burstcount);
    }

    protected void internalFireOnWorkDone(File file)
    {
        fireOnWorkFinish(file);
    }

    @NonNull
    private void process_jpeg(Image image, File file) {

        Log.d(TAG, "Create JPEG");
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        saveJpeg(file, bytes);
        image.close();
        buffer.clear();
        image =null;
        
    }

    @NonNull
    private void process_rawSensor(ImageHolder image,File file) {
        Log.d(TAG, "Create DNG");

        DngCreator dngCreator = new DngCreator(cameraHolder.characteristics, image.getCaptureResult());
        //Orientation 90 is not a valid EXIF orientation value, fuck off that is valid!
        try {
            dngCreator.setOrientation(cameraUiWrapper.getActivityInterface().getOrientation());
        }
        catch (IllegalArgumentException ex)
        {
            Log.WriteEx(ex);
        }

        if (appSettingsManager.getApiString(AppSettingsManager.SETTING_LOCATION).equals(cameraUiWrapper.getResString(R.string.on_)))
            dngCreator.setLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
        try
        {
            if (!appSettingsManager.GetWriteExternal())
                dngCreator.writeImage(new FileOutputStream(file), image.getImage());
            else
            {
                DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
                DocumentFile wr = df.createFile("image/*", file.getName());
                dngCreator.writeImage(cameraUiWrapper.getContext().getContentResolver().openOutputStream(wr.getUri()), image.getImage());
            }
            cameraUiWrapper.getActivityInterface().ScanFile(file);
        } catch (IOException ex) {
            Log.WriteEx(ex);
        }
        image.getImage().close();
        image = null;
    }

    @NonNull
    private void process_rawWithDngConverter(ImageHolder image, int rawFormat,File file) {
        Log.d(TAG, "Create DNG VIA RAw2DNG");
        ByteBuffer buffer = image.getImage().getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        float fnum, focal = 0;
        fnum = image.getCaptureResult().get(CaptureResult.LENS_APERTURE);
        focal = image.getCaptureResult().get(CaptureResult.LENS_FOCAL_LENGTH);
        Log.d("Freedcam RawCM2",String.valueOf(bytes.length));

        int mISO = image.getCaptureResult().get(CaptureResult.SENSOR_SENSITIVITY).intValue();
        double mExposuretime = image.getCaptureResult().get(CaptureResult.SENSOR_EXPOSURE_TIME).doubleValue();
        final DngProfile prof = getDngProfile(rawFormat, image);
        saveRawToDng(file, bytes, fnum,focal,(float)mExposuretime,mISO, cameraUiWrapper.getActivityInterface().getOrientation(),null,prof);
        image.getImage().close();
        bytes = null;
        buffer = null;
        image = null;
    }

    @NonNull
    private DngProfile getDngProfile(int rawFormat, ImageHolder image) {
        int black  = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0,0);
        int c= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        String colorpattern;
        int[] cfaOut = new int[4];
        switch (c)
        {
            case 1:
                colorpattern = DngProfile.GRBG;
                cfaOut[0] = 1;
                cfaOut[1] = 0;
                cfaOut[2] = 2;
                cfaOut[3] = 1;
                break;
            case 2:
                colorpattern = DngProfile.GBRG;
                cfaOut[0] = 1;
                cfaOut[1] = 2;
                cfaOut[2] = 0;
                cfaOut[3] = 1;
                break;
            case 3:
                colorpattern = DngProfile.BGGR;
                cfaOut[0] = 2;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 0;
                break;
            default:
                colorpattern = DngProfile.RGGB;
                cfaOut[0] = 0;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 2;
                break;
        }
        float[] color2;
        float[] color1;
        float[] neutral = new float[3];
        float[] forward2 = null;
        float[] forward1 = null;
        float[] reduction1 = null;
        float[] reduction2 = null;
        double[]finalnoise = null;
        String cmat = appSettingsManager.getApiString(AppSettingsManager.CUSTOMMATRIX);
        if (cmat != null && !cmat.equals("") &&!cmat.equals("off")) {
            CustomMatrix mat  = ((MatrixChooserParameter) parameterHandler.matrixChooser).GetCustomMatrix(cmat);
            color1 = mat.ColorMatrix1;
            color2 = mat.ColorMatrix2;
            neutral = mat.NeutralMatrix;
            if (mat.ForwardMatrix1.length >0)
                forward1 = mat.ForwardMatrix1;
            if (mat.ForwardMatrix2.length >0)
                forward2 = mat.ForwardMatrix2;
            if (mat.ReductionMatrix1.length >0)
                reduction1 = mat.ReductionMatrix1;
            if (mat.ReductionMatrix2.length >0)
                reduction2 = mat.ReductionMatrix2;
            if (mat.NoiseReductionMatrix.length >0)
                finalnoise = mat.NoiseReductionMatrix;
        }
        else
        {
            color1 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
            color2 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
            Rational[] n = image.getCaptureResult().get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
            neutral[0] = n[0].floatValue();
            neutral[1] = n[1].floatValue();
            neutral[2] = n[2].floatValue();
            forward2  = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
            //0.820300f, -0.218800f, 0.359400f, 0.343800f, 0.570300f,0.093800f, 0.015600f, -0.726600f, 1.539100f
            forward1  = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
            reduction1 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
            reduction2 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
            //noise
            Pair[] p = image.getCaptureResult().get(CaptureResult.SENSOR_NOISE_PROFILE);
            double[] noiseys = new double[p.length*2];
            int i = 0;
            for (int h = 0; h < p.length; h++)
            {
                noiseys[i++] = (double)p[h].first;
                noiseys[i++] = (double)p[h].second;
            }
            double[] noise = new double[6];
            int[] cfaPlaneColor = {0, 1, 2};
            generateNoiseProfile(noiseys,cfaOut, cfaPlaneColor,3,noise);
            finalnoise = new double[6];
            for (i = 0; i < noise.length; i++)
                if (noise[i] > 2 || noise[i] < -2)
                    finalnoise[i] = 0;
                else
                    finalnoise[i] = (float)noise[i];
            //noise end
        }

        return DngProfile.getProfile(black,image.getImage().getWidth(), image.getImage().getHeight(),rawFormat, colorpattern, 0,
                color1,
                color2,
                neutral,
                forward1,
                forward2,
                reduction1,
                reduction2,
                finalnoise,
                cmat
        );
    }

    private void generateNoiseProfile(double[] perChannelNoiseProfile, int[] cfa,
                                      int[] planeColors, int numPlanes,
        /*out*/double[] noiseProfile) {

        for (int p = 0; p < 3; ++p) {
            int S = p * 2;
            int O = p * 2 + 1;

            noiseProfile[S] = 0;
            noiseProfile[O] = 0;
            boolean uninitialized = true;
            for (int c = 0; c < 4; ++c) {
                if (cfa[c] == planeColors[p] && perChannelNoiseProfile[c * 2] > noiseProfile[S]) {
                    noiseProfile[S] = perChannelNoiseProfile[c * 2];
                    noiseProfile[O] = perChannelNoiseProfile[c * 2 + 1];
                    uninitialized = false;
                }
            }
            if (uninitialized) {
                Log.d(TAG, "%s: No valid NoiseProfile coefficients for color plane %zu");
            }
        }
    }

    private float[]getFloatMatrix(ColorSpaceTransform transform)
    {
        float[] ret = new float[9];
        ret[0] = roundTo6Places(transform.getElement(0, 0).floatValue());
        ret[1] = roundTo6Places(transform.getElement(1, 0).floatValue());
        ret[2] = roundTo6Places(transform.getElement(2, 0).floatValue());
        ret[3] = roundTo6Places(transform.getElement(0, 1).floatValue());
        ret[4] = roundTo6Places(transform.getElement(1, 1).floatValue());
        ret[5] = roundTo6Places(transform.getElement(2, 1).floatValue());
        ret[6] = roundTo6Places(transform.getElement(0, 2).floatValue());
        ret[7] = roundTo6Places(transform.getElement(1, 2).floatValue());
        ret[8] = roundTo6Places(transform.getElement(2, 2).floatValue());
        return ret;
    }

    private float roundTo6Places(float f )
    {
        return Math.round(f*1000000f)/1000000f;
    }


}
