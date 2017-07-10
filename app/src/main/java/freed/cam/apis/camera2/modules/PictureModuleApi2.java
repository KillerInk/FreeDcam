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
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.ImageReader;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Size;
import android.view.Surface;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.CameraHolderApi2.CompareSizesByArea;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.apis.camera2.parameters.AeHandler;
import freed.utils.Log;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2 implements ImageHolder.RdyToSaveImg
{


    private final String TAG = PictureModuleApi2.class.getSimpleName();
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
    LinkedBlockingQueue<ImageHolder> imageSaveQueue;

    private final int MAX_IMAGES = 5;


    public PictureModuleApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        imageSaveQueue = new LinkedBlockingQueue<>(MAX_IMAGES);
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

    @Override
    public void DoWork()
    {
        Log.d(TAG, "startWork: start new progress");
        if(!isWorking)
            mBackgroundHandler.post(TakePicture);
        else if (isWorking)
        {
            cameraHolder.captureSessionHandler.cancelCapture();
            finishCapture();
            changeCaptureState(CaptureStates.image_capture_stop);
        }
    }

    @Override
    public void startPreview() {


        Log.d(TAG, "Start Preview");
        setOutputSizes();

        int sensorOrientation = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        int orientationToSet = (360 +cameraUiWrapper.getActivityInterface().getOrientation() + sensorOrientation)%360;
        if (appSettingsManager.orientationhack.getBoolean())
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
            parameterHandler.Burst.fireStringValueChanged(parameterHandler.Burst.GetStringValue());
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
            parameterHandler.PictureFormat.fireStringValueChanged(picFormat);

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
        mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.JPEG, MAX_IMAGES);
        Log.d(TAG, "ImageReader JPEG");



        if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng16)))
        {
            Log.d(TAG, "ImageReader RAW_SENOSR");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
            mrawImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW_SENSOR, MAX_IMAGES);
        }
        else if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng10)))
        {
            Log.d(TAG, "ImageReader RAW10");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new CompareSizesByArea());
            mrawImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW10, MAX_IMAGES);

        }
        else if (picFormat.equals(appSettingsManager.getResString(R.string.pictureformat_dng12)))
        {
            Log.d(TAG, "ImageReader RAW12");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW12)), new CompareSizesByArea());
            mImageReader = ImageReader.newInstance(largestImageSize.getWidth(), largestImageSize.getHeight(), ImageFormat.RAW12, MAX_IMAGES);

        }
        else
            mrawImageReader = null;
    }


    @Override
    public void stopPreview()
    {
        DestroyModule();
    }

    private Runnable TakePicture = new Runnable()
    {
        @Override
        public void run() {
            isWorking = true;
            Log.d(TAG, appSettingsManager.pictureFormat.get());
            Log.d(TAG, "dng:" + Boolean.toString(parameterHandler.IsDngActive()));
            imagecount = 0;


            onStartTakePicture();

            if (appSettingsManager.hasCamera2Features() && cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) != CaptureRequest.CONTROL_AE_MODE_OFF) {
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

        ImageHolder currentCaptureHolder = new ImageHolder(cameraHolder.characteristics, mrawImageReader !=null, cameraUiWrapper.getActivityInterface(),this,this, this);
        currentCaptureHolder.setFilePath(getFileString(), appSettingsManager.GetWriteExternal());
        currentCaptureHolder.setForceRawToDng(appSettingsManager.isForceRawToDng());
        currentCaptureHolder.setToneMapProfile(((ToneMapChooser)cameraUiWrapper.getParameterHandler().tonemapChooser).getToneMap());

        Log.d(TAG, "captureStillPicture ImgCount:"+ imagecount +  " ImageHolder Path:" + currentCaptureHolder.filepath);

        if (cameraUiWrapper.getParameterHandler().locationParameter.GetStringValue().equals(appSettingsManager.getResString(R.string.on_)))
        {
            currentCaptureHolder.setLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
            cameraHolder.captureSessionHandler.SetParameter(CaptureRequest.JPEG_GPS_LOCATION,cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
        }

        String cmat = appSettingsManager.matrixset.get();
        if (cmat != null && !cmat.equals("") &&!cmat.equals("off")) {
            currentCaptureHolder.setCustomMatrix(appSettingsManager.getMatrixesMap().get(cmat));
        }

        while (imageSaveQueue.size() == MAX_IMAGES) {
            try {
                Log.d(TAG,"Wait for free Queue");
                Thread.sleep(5);
                Log.d(TAG,"Wait for free Queue poll end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mImageReader.setOnImageAvailableListener(currentCaptureHolder.mOnRawImageAvailableListener,mBackgroundHandler);
        if (mrawImageReader != null)
        {
            mrawImageReader.setOnImageAvailableListener(currentCaptureHolder.mOnRawImageAvailableListener,mBackgroundHandler);
        }
                /*try {
                    Log.d(TAG, "ImageQueue limit reached drop image");
                    ImageHolder toremove = imageSaveQueue.take();
                    toremove.CLEAR();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/


        Log.d(TAG, "Queue free add new Img");
        synchronized (imageSaveQueue) {
            imageSaveQueue.add(currentCaptureHolder);
        }


        prepareCaptureBuilder(imagecount);
        Log.d(TAG, "CancelRepeatingCaptureSessoion set imageRdyCallback");
        ImageCaptureRDYEvent imageCaptureRdyCallback = new ImageCaptureRDYEvent(currentCaptureHolder.imageCaptureMetaCallback);
        if (!cameraHolder.captureSessionHandler.IsCaptureSessionRDY())
            cameraHolder.captureSessionHandler.StopRepeatingCaptureSession(imageCaptureRdyCallback);
        else
            imageCaptureRdyCallback.onRdy();


    }

    private class  ImageCaptureRDYEvent implements CaptureSessionHandler.CaptureEvent
    {
        private CameraCaptureSession.CaptureCallback captureCallback;
        public ImageCaptureRDYEvent(CameraCaptureSession.CaptureCallback captureCallback)
        {
            this.captureCallback = captureCallback;
        }

        @Override
        public void onRdy() {
            changeCaptureState(CaptureStates.image_capture_start);
            Log.d(TAG, "StartStillCapture");
            cameraHolder.captureSessionHandler.StartImageCapture(captureCallback, mBackgroundHandler);
        }
    }


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

    private String getFileString()
    {
        if (Integer.parseInt(parameterHandler.Burst.GetStringValue()) > 1)
            return cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), "_" + imagecount);
        else
            return cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(),"");
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
            imagecount++;
            Log.d(TAG, "CaptureDone:" + imagecount + " Queue size: " + imageSaveQueue.size());
            if (Integer.parseInt(parameterHandler.Burst.GetStringValue())  > imagecount) {
                captureStillPicture();
            }
            else if (cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AE_MODE) == CaptureRequest.CONTROL_AE_MODE_OFF) {
                cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME, AeHandler.MAX_PREVIEW_EXPOSURETIME);
                cameraHolder.captureSessionHandler.SetPreviewParameter(CaptureRequest.SENSOR_FRAME_DURATION, AeHandler.MAX_PREVIEW_EXPOSURETIME);
                Log.d(TAG, "CancelRepeatingCaptureSessoion set onSessionRdy");
                cameraHolder.captureSessionHandler.CancelRepeatingCaptureSession(onSesssionRdy);
            }
            else {
                onSesssionRdy.onRdy();
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


            Log.d(TAG, "onSessionRdy() Rdy to Start Preview");
            cameraHolder.captureSessionHandler.StartRepeatingCaptureSession();
            if (cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    || cameraHolder.captureSessionHandler.getPreviewParameter(CaptureRequest.CONTROL_AF_MODE) == CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO) {
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_CANCEL);
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,
                        CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            }
        }
    };

    @Override
    public void internalFireOnWorkDone(File file)
    {
        fireOnWorkFinish(file);
    }

    @Override
    public void fireOnWorkFinish(File[] files) {
        super.fireOnWorkFinish(files);
    }

    @Override
    public void onRdyToSaveImg(ImageHolder holder) {
        imageSaveQueue.remove(holder);
        AsyncTask.THREAD_POOL_EXECUTOR.execute(holder.getRunner());
        Log.d(TAG,"onRdyToSaveImg");
        finishCapture();
    }
}
