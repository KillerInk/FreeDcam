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
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Pair;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import freed.ActivityInterface;
import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.basecamera.parameters.modes.MatrixChooserParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.CameraHolderApi2.CompareSizesByArea;
import freed.dng.CustomMatrix;
import freed.dng.DngProfile;
import freed.jni.RawToDng;
import freed.utils.AppSettingsManager;
import freed.utils.DeviceUtils.Devices;
import freed.utils.FreeDPool;
import freed.utils.Logger;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2
{
    private final static String TAG = PictureModuleApi2.class.getSimpleName();
    //private TotalCaptureResult mDngResult;
    private Size largestImageSize;
    private String picFormat;
    private String picSize;
    private int mImageWidth;
    private int mImageHeight;
    private RefCountedAutoCloseable<ImageReader>  mImageReader;
    private Size previewSize;
    private Surface previewsurface;
    private Surface camerasurface;
    private final Handler handler;
    private int imagecount;
    private Builder captureBuilder;
    private final int STATE_WAIT_FOR_PRECAPTURE = 0;
    private final int STATE_WAIT_FOR_NONPRECAPTURE = 1;
    private final int STATE_PICTURE_TAKEN = 2;
    private int mState = STATE_PICTURE_TAKEN;
    protected final TreeMap<Integer, ImageSaver.ImageSaverBuilder> mJpegResultQueue = new TreeMap<>();

    public PictureModuleApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_PICTURE;
        handler = new Handler(Looper.getMainLooper());

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
    public boolean DoWork()
    {
        if (!isWorking)
        {
            Logger.d(TAG, "DoWork: start new progress");
            TakePicture();
        }
        else
            Logger.d(TAG, "DoWork: work is in progress");
        return true;
    }

    private void TakePicture()
    {
        isWorking = true;
        Logger.d(TAG, appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT));
        Logger.d(TAG, "dng:" + Boolean.toString(parameterHandler.IsDngActive()));

        mImageReader.get().setOnImageAvailableListener(mOnRawImageAvailableListener,cameraHolder.getmBackgroundHandler());

        if (appSettingsManager.IsCamera2FullSupported().equals(KEYS.TRUE) && cameraHolder.get(CaptureRequest.CONTROL_AE_MODE) != CaptureRequest.CONTROL_AE_MODE_OFF) {
            mState = STATE_WAIT_FOR_PRECAPTURE;
            cameraHolder.CaptureSessionH.StartRepeatingCaptureSession(aecallback);
            Logger.d(TAG,"Start AE Precapture");
            cameraHolder.SetParameter(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        }
        else
        {
            Logger.d(TAG, "captureStillPicture");
            captureStillPicture();
        }


    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */
    protected void captureStillPicture() {
        try {
            Logger.d(TAG, "StartStillCapture");
            // This is the CaptureRequest.Builder that we use to take a picture.
            captureBuilder = cameraHolder.createCaptureRequestStillCapture();
            captureBuilder.addTarget(mImageReader.get().getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.setTag(0);
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, cameraHolder.get(CaptureRequest.CONTROL_AF_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, cameraHolder.get(CaptureRequest.CONTROL_AE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.FLASH_MODE, cameraHolder.get(CaptureRequest.FLASH_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, cameraHolder.get(CaptureRequest.COLOR_CORRECTION_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_TRANSFORM, cameraHolder.get(CaptureRequest.COLOR_CORRECTION_TRANSFORM));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, cameraHolder.get(CaptureRequest.COLOR_CORRECTION_GAINS));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.TONEMAP_CURVE, cameraHolder.get(CaptureRequest.TONEMAP_CURVE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                if (Build.VERSION.SDK_INT >= VERSION_CODES.M)
                    captureBuilder.set(CaptureRequest.TONEMAP_GAMMA, cameraHolder.get(CaptureRequest.TONEMAP_GAMMA));
            }
            catch (NullPointerException ex) {Logger.exception(ex);}

            try {
                int awb = cameraHolder.get(CaptureRequest.CONTROL_AWB_MODE);
                captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awb );
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.EDGE_MODE, cameraHolder.get(CaptureRequest.EDGE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.HOT_PIXEL_MODE, cameraHolder.get(CaptureRequest.HOT_PIXEL_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, cameraHolder.get(CaptureRequest.NOISE_REDUCTION_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, cameraHolder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                long val = 0;
                if(!parameterHandler.ManualIso.GetStringValue().equals(KEYS.AUTO))
                    val = (long)(AbstractManualShutter.getMilliSecondStringFromShutterString(parameterHandler.ManualShutter.getStringValues()[parameterHandler.ManualShutter.GetValue()]) * 1000f);
                else
                    val= cameraHolder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
                Logger.d(TAG, "Set ExposureTime for Capture to:" + val);
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, cameraHolder.get(CaptureRequest.SENSOR_SENSITIVITY));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, cameraHolder.get(CaptureRequest.CONTROL_EFFECT_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, cameraHolder.get(CaptureRequest.CONTROL_SCENE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, cameraHolder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, cameraUiWrapper.getActivityInterface().getOrientation());
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, cameraHolder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
            }
            catch (NullPointerException ex)
            {Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, cameraHolder.get(CaptureRequest.SCALER_CROP_REGION));
            }
            catch (NullPointerException ex)
            {Logger.exception(ex);}
            try {
                if (appSettingsManager.getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
                captureBuilder.set(CaptureRequest.JPEG_GPS_LOCATION, cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
            }
            catch (NullPointerException ex)
            {Logger.exception(ex);}

            prepareCaptureBuilder(captureBuilder);
            imagecount = 0;
            if (parameterHandler.Burst != null && parameterHandler.Burst.GetValue() > 0) {
                initBurstCapture(captureBuilder, CaptureCallback);
            }
            else
            {
                ImageSaver.ImageSaverBuilder jpegBuilder = new ImageSaver.ImageSaverBuilder(cameraUiWrapper.getContext(), cameraUiWrapper)
                        .setCharacteristics(cameraHolder.characteristics);
                mJpegResultQueue.put(0, jpegBuilder);
                if (cameraHolder.get(CaptureRequest.SENSOR_EXPOSURE_TIME) > 500000*1000)
                    cameraHolder.CaptureSessionH.StopRepeatingCaptureSession();
                changeCaptureState(CaptureStates.image_capture_start);
                cameraHolder.CaptureSessionH.StartImageCapture(captureBuilder, CaptureCallback);
            }


        } catch (CameraAccessException e) {
            Logger.exception(e);
        }
    }

    protected void prepareCaptureBuilder(Builder captureBuilder)
    {

    }

    protected void initBurstCapture(Builder captureBuilder, CaptureCallback captureCallback)
    {
        List<CaptureRequest> captureList = new ArrayList<>();
        for (int i = 0; i < parameterHandler.Burst.GetValue()+1; i++)
        {
            captureBuilder.setTag(i);
            ImageSaver.ImageSaverBuilder jpegBuilder = new ImageSaver.ImageSaverBuilder(cameraUiWrapper.getContext(), cameraUiWrapper)
                    .setCharacteristics(cameraHolder.characteristics);
            mJpegResultQueue.put(i, jpegBuilder);
            captureList.add(captureBuilder.build());
        }
        cameraHolder.CaptureSessionH.StopRepeatingCaptureSession();
        changeCaptureState(CaptureStates.image_capture_start);
        cameraHolder.CaptureSessionH.StartCaptureBurst(captureList, captureCallback);
    }

    private CaptureCallback aecallback = new CaptureCallback()
    {
        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult)
        {
            Integer aeState = partialResult.get(CaptureResult.CONTROL_AE_STATE);
            Logger.d(TAG, "CurrentCaptureState:" + mState + " AE_STATE:" + aeState);
            switch (mState)
            {
                case STATE_WAIT_FOR_PRECAPTURE:
                    Logger.d(TAG,"STATE_WAIT_FOR_PRECAPTURE  AESTATE:" + aeState);
                    if (aeState == null)
                    {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    else if (aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED)
                    {
                        Logger.d(TAG,"Wait For nonprecapture");
                        mState = STATE_WAIT_FOR_NONPRECAPTURE;
                    }
                    else if (aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED)
                    {
                        Logger.d(TAG,"CONTROL_AE_STATE_CONVERGED captureStillPicture");
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                case STATE_WAIT_FOR_NONPRECAPTURE:
                    Logger.d(TAG,"STATE_WAIT_FOR_NONPRECAPTURE");
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE)
                    {
                        cameraHolder.SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
            }
        }
    };

    private final CaptureCallback CaptureCallback
            = new CaptureCallback()
    {
        @Override
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request,
                                     long timestamp, long frameNumber) {
            String currentDateTime = cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(),"");
            int requestId = (int) request.getTag();
            if (parameterHandler.Burst.GetValue()+1>1)
                currentDateTime = currentDateTime+"_"+requestId;
            switch (mImageReader.get().getImageFormat())
            {
                case ImageFormat.JPEG:
                    currentDateTime = currentDateTime+".jpg";
                    break;
                default:
                    currentDateTime = currentDateTime +".dng";
            }
            File file = new File(currentDateTime);

            // Look up the ImageSaverBuilder for this request and update it with the file name
            // based on the capture start time.
            ImageSaver.ImageSaverBuilder imageBuilder;
                imageBuilder = mJpegResultQueue.get(requestId);

            if (imageBuilder != null) imageBuilder.setFile(file);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result)
        {
            int requestId = (int) request.getTag();
            ImageSaver.ImageSaverBuilder imageSaverBuilder = mJpegResultQueue.get(requestId);
            handleCompletionLocked(requestId, imageSaverBuilder, mJpegResultQueue);

            imageSaverBuilder.setResult(result);
            //mDngResult = result;
            try {
                Logger.d(TAG, "CaptureResult Recieved");
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ColorCorrectionGains" + result.get(CaptureResult.COLOR_CORRECTION_GAINS));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ColorCorrectionTransform" + result.get(CaptureResult.COLOR_CORRECTION_TRANSFORM));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ToneMapCurve" + result.get(CaptureResult.TONEMAP_CURVE));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor Sensitivity" + result.get(CaptureResult.SENSOR_SENSITIVITY));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor ExposureTime" + result.get(CaptureResult.SENSOR_EXPOSURE_TIME));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor FrameDuration" + result.get(CaptureResult.SENSOR_FRAME_DURATION));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor GreenSplit" + result.get(CaptureResult.SENSOR_GREEN_SPLIT));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor NoiseProfile" + Arrays.toString(result.get(CaptureResult.SENSOR_NOISE_PROFILE)));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor NeutralColorPoint" + Arrays.toString(result.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT)));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Orientation" + result.get(CaptureResult.JPEG_ORIENTATION));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "FOCUS POS: " + result.get(CaptureResult.LENS_FOCUS_DISTANCE));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            isWorking = false;
            changeCaptureState(CaptureStates.image_capture_stop);
            if (request.get(CaptureRequest.SENSOR_EXPOSURE_TIME) > 500000*1000)
                cameraHolder.CaptureSessionH.StartRepeatingCaptureSession();
        }
    };

    private void handleCompletionLocked(int requestId, ImageSaver.ImageSaverBuilder builder,
                                        TreeMap<Integer, ImageSaver.ImageSaverBuilder> queue) {
        if (builder == null) return;
        ImageSaver saver = builder.buildIfComplete();
        if (saver != null) {
            queue.remove(requestId);
            AsyncTask.THREAD_POOL_EXECUTOR.execute(saver);
        }
    }

    protected void finishCapture(Builder captureBuilder) {
        try
        {
            Logger.d(TAG, "CaptureDone");
            cameraHolder.CaptureSessionH.StartRepeatingCaptureSession();
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_LOCK,true);
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_LOCK,false);
        }
        catch (NullPointerException ex) {
            Logger.exception(ex);
        }

        isWorking = false;
    }

    private final OnImageAvailableListener mOnRawImageAvailableListener = new OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(final ImageReader reader)
        {
            dequeueAndSaveImage(mJpegResultQueue, mImageReader);
            /*int burstcount = parameterHandler.Burst.GetValue()+1;
            File file = null;
            imagecount++;
            switch (reader.getImageFormat())
            {
                case ImageFormat.JPEG:
                    file = process_jpeg(burstcount, reader);
                    break;
                case ImageFormat.RAW10:
                    file = process_rawWithDngConverter(burstcount, reader, DngProfile.Mipi);
                    break;
                case ImageFormat.RAW12:
                    file = process_rawWithDngConverter(burstcount, reader,DngProfile.Mipi12);
                    break;
                case ImageFormat.RAW_SENSOR:
                    if(appSettingsManager.getDevice() == Devices.Moto_X2k14 || appSettingsManager.getDevice() == Devices.OnePlusTwo)
                        file = process_rawWithDngConverter(burstcount, reader,DngProfile.Mipi16);
                    else
                        file = process_rawSensor(burstcount, reader);

            }

            isWorking = false;
            scanAndFinishFile(file);
            changeCaptureState(CaptureStates.image_capture_stop);
            if (burstcount == imagecount) {
                finishCapture(captureBuilder);

            }*/
        }
    };

    private void dequeueAndSaveImage(TreeMap<Integer, ImageSaver.ImageSaverBuilder> pendingQueue,
                                     RefCountedAutoCloseable<ImageReader> reader) {

            Map.Entry<Integer, ImageSaver.ImageSaverBuilder> entry =
                    pendingQueue.firstEntry();
            ImageSaver.ImageSaverBuilder builder = entry.getValue();

            // Increment reference count to prevent ImageReader from being closed while we
            // are saving its Images in a background thread (otherwise their resources may
            // be freed while we are writing to a file).
            if (reader == null || reader.getAndRetain() == null) {
                Logger.e(TAG, "Paused the activity before we could save the image," +
                        " ImageReader already closed.");
                pendingQueue.remove(entry.getKey());
                return;
            }

            Image image;
            try {
                image = reader.get().acquireNextImage();
            } catch (IllegalStateException e) {
                Logger.e(TAG, "Too many images queued for saving, dropping image for request: " +
                        entry.getKey());
                pendingQueue.remove(entry.getKey());
                return;
            }

            builder.setRefCountedReader(reader).setImage(image);

            handleCompletionLocked(entry.getKey(), builder, pendingQueue);

    }

    @NonNull
    private static void process_rawSensor(File file, Image image, CaptureResult result, CameraWrapperInterface cameraWrapperInterface, CameraCharacteristics characteristics) {
        Logger.d(TAG, "Create DNG");
        DngCreator dngCreator = new DngCreator(characteristics, result);
        dngCreator.setOrientation(cameraWrapperInterface.getActivityInterface().getOrientation());
        if (cameraWrapperInterface.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
            dngCreator.setLocation(cameraWrapperInterface.getActivityInterface().getLocationHandler().getCurrentLocation());
        try
        {
            if (!cameraWrapperInterface.GetAppSettingsManager().GetWriteExternal())
                dngCreator.writeImage(new FileOutputStream(file), image);
            else
            {
                DocumentFile df = cameraWrapperInterface.getActivityInterface().getFreeDcamDocumentFolder();
                DocumentFile wr = df.createFile("image/*", file.getName());
                dngCreator.writeImage(cameraWrapperInterface.getContext().getContentResolver().openOutputStream(wr.getUri()), image);
            }
        } catch (IOException e) {
            Logger.exception(e);
        }
        image.close();

    }

    @NonNull
    private static void process_rawWithDngConverter(File file,CaptureResult result,
                                                    Image image, int rawFormat,
                                                    CameraWrapperInterface cameraWrapperInterface,CameraCharacteristics characteristics) {
        Logger.d(TAG, "Create DNG VIA RAw2DNG");
        RawToDng dngConverter = RawToDng.GetInstance();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        ParcelFileDescriptor pfd = null;
        if (!cameraWrapperInterface.GetAppSettingsManager().GetWriteExternal())
            dngConverter.SetBayerData(bytes, file.getAbsolutePath());
        else
        {
            DocumentFile df = cameraWrapperInterface.getActivityInterface().getFreeDcamDocumentFolder();
            DocumentFile wr = df.createFile("image/*", file.getName());
            try {

                pfd = cameraWrapperInterface.getActivityInterface().getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                if (pfd != null)
                    dngConverter.SetBayerDataFD(bytes, pfd, file.getName());
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
        }
        float fnum, focal = 0;
        fnum = result.get(CaptureResult.LENS_APERTURE);
        focal = result.get(CaptureResult.LENS_FOCAL_LENGTH);
        Logger.d("Freedcam RawCM2",String.valueOf(bytes.length));

        int mISO = result.get(CaptureResult.SENSOR_SENSITIVITY).intValue();
        double mExposuretime = result.get(CaptureResult.SENSOR_EXPOSURE_TIME).doubleValue();
        int mFlash = result.get(CaptureResult.FLASH_STATE).intValue();
        double exposurecompensation= result.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION).doubleValue();

        dngConverter.setExifData(mISO, mExposuretime, mFlash, fnum, focal, "0", cameraWrapperInterface.getActivityInterface().getOrientation()+"", exposurecompensation);

        double Altitude = 0;
        double Latitude = 0;
        double Longitude = 0;
        String Provider = "ASCII";
        long gpsTime = 0;
        if (cameraWrapperInterface.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
        {
            if (cameraWrapperInterface.getActivityInterface().getLocationHandler().getCurrentLocation() != null)
            {
                Location location = cameraWrapperInterface.getActivityInterface().getLocationHandler().getCurrentLocation();
                Logger.d(TAG, "Has GPS");
                Altitude = location.getAltitude();
                Latitude = location.getLatitude();
                Longitude = location.getLongitude();
                Provider = location.getProvider();
                gpsTime = location.getTime();
                dngConverter.SetGPSData(Altitude, Latitude, Longitude, Provider, gpsTime);
            }
        }

        int black  = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0,0);
        int c= characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
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
        float[] forward2;
        float[] forward1;
        float[] reduction1;
        float[] reduction2;
        float[]finalnoise;
        String cmat = cameraWrapperInterface.GetAppSettingsManager().getString(AppSettingsManager.SETTTING_CUSTOMMATRIX);
        if (cmat != null && !cmat.equals("") &&!cmat.equals("off")) {
            CustomMatrix mat  = ((MatrixChooserParameter) cameraWrapperInterface.GetParameterHandler().matrixChooser).GetCustomMatrix(cmat);
            color1 = mat.ColorMatrix1;
            color2 = mat.ColorMatrix2;
            neutral = mat.NeutralMatrix;
            forward1 = mat.ForwardMatrix1;
            forward2 = mat.ForwardMatrix2;
            reduction1 = mat.ReductionMatrix1;
            reduction2 = mat.ReductionMatrix2;
            finalnoise = mat.NoiseReductionMatrix;
        }
        else
        {
            color1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
            color2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
            Rational[] n = result.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
            neutral[0] = n[0].floatValue();
            neutral[1] = n[1].floatValue();
            neutral[2] = n[2].floatValue();
            forward2  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
            //0.820300f, -0.218800f, 0.359400f, 0.343800f, 0.570300f,0.093800f, 0.015600f, -0.726600f, 1.539100f
            forward1  = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
            reduction1 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
            reduction2 = getFloatMatrix(characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
            //noise
            Pair[] p = result.get(CaptureResult.SENSOR_NOISE_PROFILE);
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
            finalnoise = new float[6];
            for (i = 0; i < noise.length; i++)
                if (noise[i] > 2 || noise[i] < -2)
                    finalnoise[i] = 0;
                else
                    finalnoise[i] = (float)noise[i];
            //noise end
        }

        DngProfile prof = DngProfile.getProfile(black,image.getWidth(), image.getHeight(),rawFormat, colorpattern, 0,
                color1,
                color2,
                neutral,
                forward1,
                forward2,
                reduction1,
                reduction2,
                finalnoise
        );

        dngConverter.WriteDngWithProfile(prof);
        dngConverter.RELEASE();
        image.close();
        bytes = null;
        if (pfd != null) {
            try {
                pfd.close();
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
    }

    private static void generateNoiseProfile(double[] perChannelNoiseProfile, int[] cfa,
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
                Logger.d(TAG, "%s: No valid NoiseProfile coefficients for color plane %zu");
            }
        }
    }

    private static float[]getFloatMatrix(ColorSpaceTransform transform)
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

    private static float roundTo6Places(float f )
    {
        return Math.round(f*1000000f)/1000000f;
    }

    /**
     * PREVIEW STUFF
     */



    @Override
    public void startPreview() {

        picSize = appSettingsManager.getString(AppSettingsManager.SETTING_PICTURESIZE);
        Logger.d(TAG, "Start Preview");
        largestImageSize = Collections.max(
                Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.JPEG)),
                new CompareSizesByArea());
        picFormat = appSettingsManager.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
        if (picFormat.equals("")) {
            picFormat = KEYS.JPEG;
            appSettingsManager.setString(AppSettingsManager.SETTING_PICTUREFORMAT, KEYS.JPEG);
            parameterHandler.PictureFormat.BackgroundValueHasChanged(KEYS.JPEG);

        }

        if (picFormat.equals(KEYS.JPEG))
        {
            String[] split = picSize.split("x");
            int width, height;
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
            Logger.d(TAG, "ImageReader JPEG");
        }
        else if (picFormat.equals(CameraHolderApi2.RAW_SENSOR))
        {
            Logger.d(TAG, "ImageReader RAW_SENSOR");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
            mImageWidth = largestImageSize.getWidth();
            mImageHeight = largestImageSize.getHeight();
        }
        else if (picFormat.equals(CameraHolderApi2.RAW10))
        {
            Logger.d(TAG, "ImageReader RAW10");
            largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new CompareSizesByArea());
            mImageWidth = largestImageSize.getWidth();
            mImageHeight = largestImageSize.getHeight();
        }


        //OrientationHACK
        if(appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
            cameraHolder.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, 180);
        else
            cameraHolder.SetParameterRepeating(CaptureRequest.JPEG_ORIENTATION, 0);

        // Here, we create a CameraCaptureSession for camera preview
        if (parameterHandler.Burst == null)
            SetBurst(1);
        else
            SetBurst(parameterHandler.Burst.GetValue());


    }

    @Override
    public void stopPreview()
    {
        DestroyModule();
    }

    private void SetBurst(int burst)
    {
        try {
            Logger.d(TAG, "Set Burst to:" + burst);
            previewSize = cameraHolder.getSizeForPreviewDependingOnImageSize(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), cameraHolder.characteristics, mImageWidth, mImageHeight);
            if (cameraUiWrapper.getFocusPeakProcessor() != null)
            {
                cameraUiWrapper.getFocusPeakProcessor().kill();
            }

            cameraHolder.CaptureSessionH.SetTextureViewSize(previewSize.getWidth(), previewSize.getHeight(),0,180,false);
            SurfaceTexture texture = cameraHolder.CaptureSessionH.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewsurface = new Surface(texture);

            cameraUiWrapper.getFocusPeakProcessor().Reset(previewSize.getWidth(), previewSize.getHeight());
            Logger.d(TAG, "Previewsurface vailid:" + previewsurface.isValid());
            cameraUiWrapper.getFocusPeakProcessor().setOutputSurface(previewsurface);
            camerasurface = cameraUiWrapper.getFocusPeakProcessor().getInputSurface();
            cameraHolder.CaptureSessionH.AddSurface(camerasurface,true);

            if (picFormat.equals(KEYS.JPEG))
                mImageReader = new RefCountedAutoCloseable<>(ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.JPEG, burst+1));
            else if (picFormat.equals(CameraHolderApi2.RAW10))
                mImageReader = new RefCountedAutoCloseable<>(ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW10, burst+1));
            else if (picFormat.equals(CameraHolderApi2.RAW_SENSOR))
                mImageReader = new RefCountedAutoCloseable<>(ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW_SENSOR, burst+1));
            else if (picFormat.equals(CameraHolderApi2.RAW12))
                mImageReader = new RefCountedAutoCloseable<>(ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW12,burst+1));
            cameraHolder.CaptureSessionH.AddSurface(mImageReader.get().getSurface(),false);
            cameraHolder.CaptureSessionH.CreateCaptureSession();
        }
        catch(Exception ex)
        {
            Logger.exception(ex);
        }
        if (parameterHandler.Burst != null)
            parameterHandler.Burst.ThrowCurrentValueChanged(parameterHandler.Burst.GetValue());
    }

    @Override
    public void InitModule()
    {
        super.InitModule();
        Logger.d(TAG, "InitModule");
        startPreview();
    }

    @Override
    public void DestroyModule()
    {
        Logger.d(TAG, "DestroyModule");
        cameraHolder.CaptureSessionH.CloseCaptureSession();
        cameraUiWrapper.getFocusPeakProcessor().kill();
    }


    protected static class ImageSaver implements Runnable {

        /**
         * The image to save.
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        /**
         * The CaptureResult for this image capture.
         */
        private final CaptureResult mCaptureResult;

        /**
         * The CameraCharacteristics for this camera device.
         */
        private final CameraCharacteristics mCharacteristics;

        /**
         * The Context to use when updating MediaStore with the saved images.
         */
        private final Context mContext;

        /**
         * A reference counted wrapper for the ImageReader that owns the given image.
         */
        private final RefCountedAutoCloseable<ImageReader> mReader;

        private final CameraWrapperInterface cameraWrapperInterface;

        private ImageSaver(Image image, File file, CaptureResult result,
                           CameraCharacteristics characteristics, Context context,
                           RefCountedAutoCloseable<ImageReader> reader, CameraWrapperInterface cameraWrapperInterface) {
            mImage = image;
            mFile = file;
            mCaptureResult = result;
            mCharacteristics = characteristics;
            mContext = context;
            mReader = reader;
            this.cameraWrapperInterface = cameraWrapperInterface;

        }

        @Override
        public void run() {
            boolean success = false;
            int format = mImage.getFormat();
            switch (format) {
                case ImageFormat.JPEG: {
                    ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    OutputStream output = null;
                    ParcelFileDescriptor pfd = null;
                    try {
                        if (!cameraWrapperInterface.GetAppSettingsManager().GetWriteExternal())
                            output = new FileOutputStream(mFile);
                        else
                        {

                            DocumentFile df = cameraWrapperInterface.getActivityInterface().getFreeDcamDocumentFolder();
                            DocumentFile wr = df.createFile("*/*", mFile.getName());
                            output = mContext.getContentResolver().openOutputStream(wr.getUri(),"rw");
                        }
                        output.write(bytes);
                    } catch (IOException e) {
                        Logger.exception(e);
                    } finally {
                        mImage.close();
                        if (null != output) {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Logger.exception(e);
                            }
                        }
                    }
                    break;
                }
                case ImageFormat.RAW10:
                    process_rawWithDngConverter(mFile, mCaptureResult, mImage, DngProfile.Mipi,cameraWrapperInterface,mCharacteristics);
                    break;
                case ImageFormat.RAW12:
                    process_rawWithDngConverter(mFile, mCaptureResult, mImage,DngProfile.Mipi12,cameraWrapperInterface,mCharacteristics);
                    break;
                case ImageFormat.RAW_SENSOR:
                    if(cameraWrapperInterface.GetAppSettingsManager().getDevice() == Devices.Moto_X2k14 || cameraWrapperInterface.GetAppSettingsManager().getDevice() == Devices.OnePlusTwo)
                        process_rawWithDngConverter(mFile, mCaptureResult, mImage,DngProfile.Mipi16,cameraWrapperInterface,mCharacteristics);
                    else
                        process_rawSensor(mFile, mImage,mCaptureResult,cameraWrapperInterface,mCharacteristics);
            }

            // Decrement reference count to allow ImageReader to be closed to free up resources.
            mReader.close();

            // If saving the file succeeded, update MediaStore.

            scanAndFinishFile(mFile,cameraWrapperInterface);
        }

        /**
         * Builder class for constructing {@link ImageSaver}s.
         * <p/>
         * This class is thread safe.
         */
        public static class ImageSaverBuilder {
            private Image mImage;
            private File mFile;
            private CaptureResult mCaptureResult;
            private CameraCharacteristics mCharacteristics;
            private Context mContext;
            private RefCountedAutoCloseable<ImageReader> mReader;
            private CameraWrapperInterface cameraWrapperInterface;

            /**
             * Construct a new ImageSaverBuilder using the given {@link Context}.
             *
             * @param context a {@link Context} to for accessing the
             *                {@link android.provider.MediaStore}.
             */
            public ImageSaverBuilder(final Context context,final CameraWrapperInterface cameraWrapperInterface) {
                mContext = context;
                this.cameraWrapperInterface = cameraWrapperInterface;
            }

            public synchronized ImageSaverBuilder setRefCountedReader(
                    RefCountedAutoCloseable<ImageReader> reader) {
                if (reader == null) throw new NullPointerException();

                mReader = reader;
                return this;
            }

            public synchronized ImageSaverBuilder setImage(final Image image) {
                if (image == null) throw new NullPointerException();
                mImage = image;
                return this;
            }

            public synchronized ImageSaverBuilder setFile(final File file) {
                if (file == null) throw new NullPointerException();
                mFile = file;
                return this;
            }

            public synchronized ImageSaverBuilder setResult(final CaptureResult result) {
                if (result == null) throw new NullPointerException();
                mCaptureResult = result;
                return this;
            }

            public synchronized ImageSaverBuilder setCharacteristics(
                    final CameraCharacteristics characteristics) {
                if (characteristics == null) throw new NullPointerException();
                mCharacteristics = characteristics;
                return this;
            }

            public synchronized ImageSaver buildIfComplete() {
                if (!isComplete()) {
                    return null;
                }
                return new ImageSaver(mImage, mFile, mCaptureResult, mCharacteristics, mContext,
                        mReader,cameraWrapperInterface);
            }

            public synchronized String getSaveLocation() {
                return (mFile == null) ? "Unknown" : mFile.toString();
            }

            private boolean isComplete() {
                return mImage != null && mFile != null && mCaptureResult != null
                        && mCharacteristics != null;
            }
        }
    }
    /**
     * A wrapper for an {@link AutoCloseable} object that implements reference counting to allow
     * for resource management.
     */
    public static class RefCountedAutoCloseable<T extends AutoCloseable> implements AutoCloseable {
        private T mObject;
        private long mRefCount = 0;

        /**
         * Wrap the given object.
         *
         * @param object an object to wrap.
         */
        public RefCountedAutoCloseable(T object) {
            if (object == null) throw new NullPointerException();
            mObject = object;
        }

        /**
         * Increment the reference count and return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        public synchronized T getAndRetain() {
            if (mRefCount < 0) {
                return null;
            }
            mRefCount++;
            return mObject;
        }

        /**
         * Return the wrapped object.
         *
         * @return the wrapped object, or null if the object has been released.
         */
        public synchronized T get() {
            return mObject;
        }

        /**
         * Decrement the reference count and release the wrapped object if there are no other
         * users retaining this object.
         */
        @Override
        public synchronized void close() {
            if (mRefCount >= 0) {
                mRefCount--;
                if (mRefCount < 0) {
                    try {
                        mObject.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    } finally {
                        mObject = null;
                    }
                }
            }
        }
    }
}
