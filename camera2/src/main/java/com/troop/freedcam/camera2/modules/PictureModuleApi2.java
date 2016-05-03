package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.v4.provider.DocumentFile;
import android.util.Pair;
import android.util.Rational;
import android.util.Size;
import android.view.Surface;

import com.troop.androiddng.CustomMatrix;
import com.troop.androiddng.DngSupportedDevices;
import com.troop.androiddng.RawToDng;
import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.FreeDPool;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

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


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = StringUtils.TAG +PictureModuleApi2.class.getSimpleName();
    private BaseCameraHolderApi2 cameraHolder;
    private int mState;
    /**
     * Camera state: Showing camera preview.
     */
    public static final int STATE_PREVIEW = 0;
    /**
     * Camera state: Waiting for the focus to be locked.
     */
    public static final int STATE_WAITING_LOCK = 1;
    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    public static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    public static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * Camera state: Picture was taken.
     */
    public static final int STATE_PICTURE_TAKEN = 4;
    private TotalCaptureResult mDngResult;

    private Size largestImageSize;
    private String picFormat;
    private String picSize;
    private int mImageWidth;
    private int mImageHeight;
    private ImageReader mImageReader;
    private Size previewSize;

    private Surface previewsurface;
    private Surface camerasurface;

    private Handler handler;
    private int imagecount = 0;

    public PictureModuleApi2(BaseCameraHolderApi2 cameraHandler, ModuleEventHandler eventHandler ) {
        super(cameraHandler, eventHandler);
        this.cameraHolder = (BaseCameraHolderApi2)cameraHandler;
        this.name = AbstractModuleHandler.MODULE_PICTURE;
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
        if (!cameraHolder.isWorking && !isWorking)
        {
            /*get pic size*/
            workstarted();
            TakePicture();
        }
        return true;
    }

    private void TakePicture()
    {
        isWorking = true;
        Logger.d(TAG, AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_PICTUREFORMAT));
        Logger.d(TAG, "dng:" + Boolean.toString(ParameterHandler.IsDngActive()));

        mImageReader.setOnImageAvailableListener(mOnRawImageAvailableListener,null);

        FreeDPool.Execute(new Runnable() {
            @Override
            public void run() {
                captureStillPicture();
            }
        });

        //lockFocus();
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */
    private void captureStillPicture() {
        try {
            Logger.d(TAG, "StartStillCapture");
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = cameraHolder.createCaptureRequest();
            captureBuilder.addTarget(mImageReader.getSurface());
            // Use the same AE and AF modes as the preview.
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.FLASH_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.FLASH_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_TRANSFORM, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_TRANSFORM));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_GAINS));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.TONEMAP_CURVE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.TONEMAP_CURVE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                int awb = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AWB_MODE);
                captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awb );
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.EDGE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.EDGE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.HOT_PIXEL_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.HOT_PIXEL_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.NOISE_REDUCTION_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                long val = 0;
                if(!ParameterHandler.ManualShutter.GetStringValue().equals("Auto"))
                    val = (long)(StringUtils.getMilliSecondStringFromShutterString(ParameterHandler.ManualShutter.getStringValues()[ParameterHandler.ManualShutter.GetValue()]) * 1000f);
                else
                    val= cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
                Logger.d(TAG, "Set ExposureTime for Capture to:" + val);
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.SENSOR_SENSITIVITY));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_SCENE_MODE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                    captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.JPEG_ORIENTATION));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                captureBuilder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE));
            }
            catch (NullPointerException ex)
            {Logger.exception(ex);}
            List<CaptureRequest> captureList = new ArrayList<>();
            for (int i=0; i< ParameterHandler.Burst.GetValue()+1; i++)
            {
                captureList.add(captureBuilder.build());
            }
            imagecount = 0;
            cameraHolder.mCaptureSession.stopRepeating();
            //captureBuilder.removeTarget(cameraHolder.previewsurface);
            mDngResult = null;

            //cameraHolder.mCaptureSession.captureBurst(captureList, CaptureCallback, backgroundHandler);
            cameraHolder.mCaptureSession.capture(captureBuilder.build(),CaptureCallback,handler);
        } catch (CameraAccessException e) {
            Logger.exception(e);
        }
    }

    private CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback()
    {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result)
        {
            mDngResult = result;
            try {
                Logger.d(TAG, "CaptureResult Recieved");
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ColorCorrectionGains" + mDngResult.get(CaptureResult.COLOR_CORRECTION_GAINS));
            }catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ColorCorrectionTransform" + mDngResult.get(CaptureResult.COLOR_CORRECTION_TRANSFORM));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "ToneMapCurve" + mDngResult.get(CaptureResult.TONEMAP_CURVE));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor Sensitivity" + mDngResult.get(CaptureResult.SENSOR_SENSITIVITY));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor ExposureTime" + mDngResult.get(CaptureResult.SENSOR_EXPOSURE_TIME));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor FrameDuration" + mDngResult.get(CaptureResult.SENSOR_FRAME_DURATION));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor GreenSplit" + mDngResult.get(CaptureResult.SENSOR_GREEN_SPLIT));
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor NoiseProfile" + mDngResult.get(CaptureResult.SENSOR_NOISE_PROFILE).toString());
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Sensor NeutralColorPoint" + mDngResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT).toString());
            }
            catch (NullPointerException ex){Logger.exception(ex);}
            try {
                Logger.d(TAG, "Orientation" + mDngResult.get(CaptureResult.JPEG_ORIENTATION).toString());
            }
            catch (NullPointerException ex){Logger.exception(ex);}

        }
    };

    private void finishCapture() {
        try
        {
            Logger.d(TAG, "CaptureDone");
            //cameraHolder.SetLastUsedParameters(cameraHolder.mPreviewRequestBuilder);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            //cameraHolder.mCaptureSession.abortCaptures();
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                        null);
            }
            catch (CameraAccessException ex)
            {
                cameraHolder.CloseCamera();
                cameraHolder.OpenCamera(AppSettingsManager.APPSETTINGSMANAGER.GetCurrentCamera());
            }

        }
        catch (NullPointerException ex) {
            Logger.exception(ex);
        }

        isWorking = false;
    }

    private void checkFileExists(File fileName) {
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists() && fileName.canWrite())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                Logger.exception(e);
            }
    }

    private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener = new ImageReader.OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(final ImageReader reader)
        {
            FreeDPool.Execute(new Runnable() {
                @Override
                public void run() {
                    while (mDngResult == null)
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Logger.exception(e);
                        }
                    int burstcount = ParameterHandler.Burst.GetValue();
                    File file = null;
                    Handler handler = new Handler(Looper.getMainLooper());
                    imagecount++;
                    if (reader.getImageFormat() == ImageFormat.JPEG) {
                        file = process_jpeg(burstcount, reader);
                    } else if (reader.getImageFormat() == ImageFormat.RAW10) {
                        file = process_raw10(burstcount, reader);

                    } else if (reader.getImageFormat() == ImageFormat.RAW_SENSOR /*&& cameraHolder.ParameterHandler.IsDngActive()*/) {
                        file = process_rawSensor(burstcount, reader);
                    }


                    isWorking = false;
                    MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context, file);
                    eventHandler.WorkFinished(file);
                    if (burstcount == imagecount) {
                        workfinished(true);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                finishCapture();
                            }
                        });
                    }
                }
            });
        }
    };

    @NonNull
    private File process_jpeg(int burstcount, ImageReader reader) {
        File file;
        Logger.d(TAG, "Create JPEG");
        if (burstcount > 1)
            file = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), "_" + imagecount + ".jpg"));
        else
            file = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), ".jpg"));
        checkFileExists(file);
        Image image = reader.acquireNextImage();
        while (image == null) {
            image = reader.acquireNextImage();

        }
        new ImageSaver(image, file).run();
        return file;
    }

    @NonNull
    private File process_rawSensor(int burstcount, ImageReader reader) {
        File file;
        Logger.d(TAG, "Create DNG");
        /*if (burstcount > 1)
            file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), "_" + imagecount + ".dng"));
        else*/
            file = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), ".dng"));
        //checkFileExists(file);
        Image image = reader.acquireNextImage();
        while (image == null) {
            image = reader.acquireNextImage();
        }


        if(DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8974) || DeviceUtils.IS(DeviceUtils.Devices.OnePlusTwo))
        {
            final RawToDng dngConverter = RawToDng.GetInstance();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            ParcelFileDescriptor pfd = null;
            if (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal())
                dngConverter.SetBayerData(bytes, file.getAbsolutePath());
            else
            {
                Uri uri = Uri.parse(AppSettingsManager.APPSETTINGSMANAGER.GetBaseFolder());
                DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
                DocumentFile wr = df.createFile("image/dng", file.getName());
                try {

                    pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                    if (pfd != null)
                        dngConverter.SetBayerDataFD(bytes, pfd, file.getName());
                } catch (FileNotFoundException | IllegalArgumentException e) {
                    Logger.exception(e);
                }
            }
            float fnum, focal = 0;
            fnum = 2.0f;
            focal = 4.7f;
            Logger.d("Freedcam RawCM2",String.valueOf(bytes.length));

            //  int mISO = mDngResult.get(CaptureResult.SENSOR_SENSITIVITY));
            double mExposuretime;
            int mFlash;


            dngConverter.setExifData(0, 0, 0, fnum, focal, "0", cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.JPEG_ORIENTATION).toString(), 0);

            dngConverter.WriteDNG(DeviceUtils.DEVICE());
            dngConverter.RELEASE();
            image.close();
            bytes = null;
            if (pfd != null)
                try {
                    pfd.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        else
        {
            DngCreator dngCreator = new DngCreator(cameraHolder.characteristics, mDngResult);
            dngCreator.setOrientation(mDngResult.get(CaptureResult.JPEG_ORIENTATION));
            try
            {
                if (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal())
                    dngCreator.writeImage(new FileOutputStream(file), image);
                else
                {
                    DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
                    DocumentFile wr = df.createFile("image/*", file.getName());
                    dngCreator.writeImage(AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri()), image);
                }
            } catch (IOException e) {
                Logger.exception(e);
            }
            image.close();
        }
        return file;
    }

    @NonNull
    private File process_raw10(int burstcount, ImageReader reader) {
        File file;
        Logger.d(TAG, "Create DNG VIA RAw2DNG");
        if (burstcount > 1)
            file = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), "_" + imagecount + ".dng"));
        else
            file = new File(StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), ".dng"));
        checkFileExists(file);
        Image image = reader.acquireNextImage();
        while (image == null) {
            image = reader.acquireNextImage();
        }
        final RawToDng dngConverter = RawToDng.GetInstance();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        ParcelFileDescriptor pfd = null;
        if (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal())
            dngConverter.SetBayerData(bytes, file.getAbsolutePath());
        else
        {
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
            DocumentFile wr = df.createFile("image/*", file.getName());
            try {

                pfd = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                if (pfd != null)
                    dngConverter.SetBayerDataFD(bytes, pfd, file.getName());
            } catch (FileNotFoundException | IllegalArgumentException e) {
                Logger.exception(e);
            }
        }
        float fnum, focal = 0;
        fnum = mDngResult.get(CaptureResult.LENS_APERTURE);
        focal = mDngResult.get(CaptureResult.LENS_FOCAL_LENGTH);
        Logger.d("Freedcam RawCM2",String.valueOf(bytes.length));

        int mISO = mDngResult.get(CaptureResult.SENSOR_SENSITIVITY).intValue();
        double mExposuretime = mDngResult.get(CaptureResult.SENSOR_EXPOSURE_TIME).doubleValue();
        int mFlash = mDngResult.get(CaptureResult.FLASH_STATE).intValue();
        double exposurecompensation= mDngResult.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION).doubleValue();

        dngConverter.setExifData(mISO, mExposuretime, mFlash, fnum, focal, "0", cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.JPEG_ORIENTATION).toString(), exposurecompensation);

        int black  = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN).getOffsetForIndex(0,0);
        int c= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        String colorpattern;
        int[] cfaOut = new int[4];
        switch (c)
        {
            case 1:
                colorpattern = DngSupportedDevices.GRBG;
                cfaOut[0] = 1;
                cfaOut[1] = 0;
                cfaOut[2] = 2;
                cfaOut[3] = 1;
                break;
            case 2:
                colorpattern = DngSupportedDevices.GBRG;
                cfaOut[0] = 1;
                cfaOut[1] = 2;
                cfaOut[2] = 0;
                cfaOut[3] = 1;
                break;
            case 3:
                colorpattern = DngSupportedDevices.BGGR;
                cfaOut[0] = 2;
                cfaOut[1] = 1;
                cfaOut[2] = 1;
                cfaOut[3] = 0;
                break;
            default:
                colorpattern = DngSupportedDevices.RGGB;
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
        String cmat = AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTTING_CUSTOMMATRIX);
        if (cmat != null && !cmat.equals("") &&!cmat.equals("off")) {
            CustomMatrix mat  = new CustomMatrix();
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
            color1 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
            color2 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
            Rational[] n =  mDngResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT);
            neutral[0] = n[0].floatValue();
            neutral[1] = n[1].floatValue();
            neutral[2] = n[2].floatValue();
            forward2  = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
            //0.820300f, -0.218800f, 0.359400f, 0.343800f, 0.570300f,0.093800f, 0.015600f, -0.726600f, 1.539100f
            forward1  = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
            reduction1 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
            reduction2 = getFloatMatrix(cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
            //noise
            Pair[] p = mDngResult.get(CaptureResult.SENSOR_NOISE_PROFILE);
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

        DngSupportedDevices d = new DngSupportedDevices();
        DngSupportedDevices.DngProfile prof = d.getProfile(black,image.getWidth(), image.getHeight(), colorpattern, 0,
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
        return file;
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
                Logger.d(TAG, "%s: No valid NoiseProfile coefficients for color plane %zu");
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

    /**
     * PREVIEW STUFF
     */



    @Override
    public void startPreview() {

        picSize = AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_PICTURESIZE);
        Logger.d(TAG, "Start Preview");
        largestImageSize = Collections.max(
                Arrays.asList(baseCameraHolder.map.getOutputSizes(ImageFormat.JPEG)),
                new BaseCameraHolderApi2.CompareSizesByArea());
        picFormat = AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
        if (picFormat.equals("")) {
            picFormat = BaseCameraHolderApi2.JPEG;
            AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_PICTUREFORMAT, BaseCameraHolderApi2.JPEG);
            ParameterHandler.PictureFormat.BackgroundValueHasChanged(BaseCameraHolderApi2.JPEG);

        }

        if (picFormat.equals(BaseCameraHolderApi2.JPEG))
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
        else if (picFormat.equals(BaseCameraHolderApi2.RAW_SENSOR))
        {
            Logger.d(TAG, "ImageReader RAW_SENOSR");
            largestImageSize = Collections.max(Arrays.asList(baseCameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new BaseCameraHolderApi2.CompareSizesByArea());
            mImageWidth = largestImageSize.getWidth();
            mImageHeight = largestImageSize.getHeight();
        }
        else if (picFormat.equals(BaseCameraHolderApi2.RAW10))
        {
            Logger.d(TAG, "ImageReader RAW_SENOSR");
            largestImageSize = Collections.max(Arrays.asList(baseCameraHolder.map.getOutputSizes(ImageFormat.RAW10)), new BaseCameraHolderApi2.CompareSizesByArea());
            mImageWidth = largestImageSize.getWidth();
            mImageHeight = largestImageSize.getHeight();
        }

        //OrientationHACK
        if(AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
            baseCameraHolder.mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 180);
        else
            baseCameraHolder.mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);

        // Here, we create a CameraCaptureSession for camera previewSize.
        if (ParameterHandler.Burst == null)
            SetBurst(1);
        else
            SetBurst(ParameterHandler.Burst.GetValue());


    }

    @Override
    public void stopPreview()
    {
        UnloadNeededParameters();
    }

    private void SetBurst(int burst)
    {
        try {
            Logger.d(TAG, "Set Burst to:" + burst);
            previewSize = BaseCameraHolderApi2.getSizeForPreviewDependingOnImageSize(baseCameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888), cameraHolder.characteristics, mImageWidth, mImageHeight);
            if (baseCameraHolder.mProcessor != null)
            {
                baseCameraHolder.mProcessor.kill();
            }
            baseCameraHolder.mProcessor.setRenderScriptErrorListner(rsErrorHandler);
            baseCameraHolder.CaptureSessionH.SetTextureViewSize(previewSize.getWidth(),previewSize.getHeight(),0,180,false);
            SurfaceTexture texture = baseCameraHolder.textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewsurface = new Surface(texture);

            baseCameraHolder.mProcessor.Reset(previewSize.getWidth(), previewSize.getHeight());
            Logger.d(TAG, "Previewsurface vailid:" + previewsurface.isValid());
            baseCameraHolder.mProcessor.setOutputSurface(previewsurface);
            camerasurface = baseCameraHolder.mProcessor.getInputSurface();
            baseCameraHolder.CaptureSessionH.AddSurface(camerasurface,true);

            if (picFormat.equals(BaseCameraHolderApi2.JPEG))
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.JPEG, burst);
            else if (picFormat.equals(BaseCameraHolderApi2.RAW10))
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW10, burst);
            else
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW_SENSOR, burst);
            baseCameraHolder.CaptureSessionH.AddSurface(mImageReader.getSurface(),false);
            baseCameraHolder.CaptureSessionH.CreateCaptureSession();
        }
        catch(Exception ex){Logger.exception(ex);}
        if (ParameterHandler.Burst != null)
            ParameterHandler.Burst.ThrowCurrentValueChanged(ParameterHandler.Burst.GetValue());
    }

    /**
     * Saves a JPEG {@link android.media.Image} into the specified {@link File}.
     */
    private class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        public ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            OutputStream output = null;
            ParcelFileDescriptor pfd = null;
            try {
                if (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal())
                    output = new FileOutputStream(mFile);
                else
                {

                    DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
                    DocumentFile wr = df.createFile("*/*", mFile.getName());
                    output = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openOutputStream(wr.getUri(),"rw");
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
        }
    }

    @Override
    public void LoadNeededParameters()
    {
        Logger.d(TAG, "LoadNeededParameters");
        cameraHolder.ModulePreview = this;
        cameraHolder.StartPreview();
    }

    @Override
    public void UnloadNeededParameters()
    {
        Logger.d(TAG, "UnloadNeededParameters");
        cameraHolder.CaptureSessionH.CloseCaptureSession();
        cameraHolder.mProcessor.kill();
        previewsurface = null;
        camerasurface = null;
    }

    private RenderScript.RSErrorHandler rsErrorHandler = new RenderScript.RSErrorHandler()
    {
        @Override
        public void run() {
            super.run();
            eventHandler.RunOnUiThread(new Runnable() {
                @Override
                public void run() {
                    UnloadNeededParameters();
                    LoadNeededParameters();
                }
            });

        }
    };

}
