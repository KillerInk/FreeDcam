package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.troop.androiddng.RawToDng;
import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by troop on 12.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class PictureModuleApi2 extends AbstractModuleApi2
{
    private static String TAG = StringUtils.TAG +PictureModuleApi2.class.getSimpleName();
    BaseCameraHolderApi2 cameraHolder;
    int mState;
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

    int imagecount = 0;

    public PictureModuleApi2(BaseCameraHolderApi2 cameraHandler, AppSettingsManager Settings, ModuleEventHandler eventHandler) {
        super(cameraHandler, Settings, eventHandler);
        this.cameraHolder = (BaseCameraHolderApi2)cameraHandler;
        this.Settings = Settings;
        this.name = AbstractModuleHandler.MODULE_PICTURE;
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
        if (!cameraHolder.isWorking)
        {
            /*get pic size*/
            workstarted();
            TakePicture();
        }

    }

    public void TakePicture()
    {
        isWorking = true;
        Log.d(TAG, Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT));
        Log.d(TAG, "dng:"+ Boolean.toString(ParameterHandler.IsDngActive()));

        cameraHolder.mImageReader.setOnImageAvailableListener(mOnRawImageAvailableListener, null);

        captureStillPicture();
        //lockFocus();
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */
    private void captureStillPicture() {
        try {
            Log.d(TAG, "StartStillCapture");
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = cameraHolder.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);

            captureBuilder.addTarget(cameraHolder.mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_MODE));
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_MODE));
            captureBuilder.set(CaptureRequest.FLASH_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.FLASH_MODE));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_MODE));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_GAINS));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_TRANSFORM, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_TRANSFORM));
            captureBuilder.set(CaptureRequest.TONEMAP_CURVE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.TONEMAP_CURVE));
            int awb = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AWB_MODE);
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awb );
            captureBuilder.set(CaptureRequest.EDGE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.EDGE_MODE));
            captureBuilder.set(CaptureRequest.HOT_PIXEL_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.HOT_PIXEL_MODE));
            captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.NOISE_REDUCTION_MODE));
            captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION));
            long val = (long)(StringUtils.getMilliSecondStringFromShutterString(cameraHolder.ParameterHandler.ManualShutter.getStringValues()[cameraHolder.ParameterHandler.ManualShutter.GetValue()]) * 1000f);
            Log.d(TAG, "Set ExposureTime for Capture to:" + val);
            captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
            captureBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE));
            captureBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_SCENE_MODE));
            captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE));

            List<CaptureRequest> captureList = new ArrayList<CaptureRequest>();
            for (int i=0; i< ParameterHandler.Burst.GetValue()+1; i++)
            {
                captureList.add(captureBuilder.build());
            }
            imagecount = 0;
            cameraHolder.mCaptureSession.stopRepeating();
            captureBuilder.removeTarget(cameraHolder.previewsurface);
            mDngResult = null;
            cameraHolder.mCaptureSession.captureBurst(captureList, CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback()
    {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result)
        {
            mDngResult = result;
            Log.d(TAG, "CaptureResult Recieved");
            Log.d(TAG, "ColorCorrectionGains" + mDngResult.get(CaptureResult.COLOR_CORRECTION_GAINS));
            Log.d(TAG, "ColorCorrectionTransform" + mDngResult.get(CaptureResult.COLOR_CORRECTION_TRANSFORM));
            Log.d(TAG, "ToneMapCurve" + mDngResult.get(CaptureResult.TONEMAP_CURVE));
            Log.d(TAG, "Sensor Sensitivity" + mDngResult.get(CaptureResult.SENSOR_SENSITIVITY));
            Log.d(TAG, "Sensor ExposureTime" + mDngResult.get(CaptureResult.SENSOR_EXPOSURE_TIME));
            Log.d(TAG, "Sensor FrameDuration" + mDngResult.get(CaptureResult.SENSOR_FRAME_DURATION));
            Log.d(TAG, "Sensor GreenSplit" + mDngResult.get(CaptureResult.SENSOR_GREEN_SPLIT));
            Log.d(TAG, "Sensor NoiseProfile" + mDngResult.get(CaptureResult.SENSOR_NOISE_PROFILE).toString());
            Log.d(TAG, "Sensor NeutralColorPoint" + mDngResult.get(CaptureResult.SENSOR_NEUTRAL_COLOR_POINT).toString());
            //Toast.makeText(getActivity(), "Saved: " + mFile, Toast.LENGTH_SHORT).show();

        }
    };

    private void finishCapture() {
        try {
            Log.d(TAG, "CaptureDone");
            cameraHolder.SetLastUsedParameters(cameraHolder.mPreviewRequestBuilder);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;

            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        isWorking = false;
    }

    public void checkFileExists(File fileName) {
        if(!fileName.getParentFile().exists())
            fileName.getParentFile().mkdirs();
        if (!fileName.exists())
            try {
                fileName.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener = new ImageReader.OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(final ImageReader reader)
        {
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    int burstcount = ParameterHandler.Burst.GetValue()+1;
                    File file = null;
                    Handler handler = new Handler(Looper.getMainLooper());
                    imagecount++;
                    if (reader.getImageFormat() == ImageFormat.JPEG)
                    {
                        Log.d(TAG, "Create JPEG");
                        if (burstcount > 1)
                            file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), "_"+ imagecount +".jpg"));
                        else
                            file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), ".jpg"));
                        checkFileExists(file);
                        Image image = reader.acquireNextImage();
                        while (image == null) {
                            image = reader.acquireNextImage();

                        }
                        new ImageSaver(image, file).run();
                    }
                    else if (reader.getImageFormat() == ImageFormat.RAW_SENSOR /*&& cameraHolder.ParameterHandler.IsDngActive()*/)
                    {
                        Log.d(TAG, "Create DNG");
                        if (burstcount > 1)
                            file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), "_"+ imagecount +".dng"));
                        else
                            file = new File(StringUtils.getFilePath(Settings.GetWriteExternal(), ".dng"));
                        checkFileExists(file);
                        Image image = reader.acquireNextImage();
                        while (image == null) {
                            image = reader.acquireNextImage();
                        }
                        if(!DeviceUtils.isMoto_MSM8982_8994()) {
                            while (mDngResult == null)
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            DngCreator dngCreator = new DngCreator(cameraHolder.characteristics, mDngResult);

                            try {
                                dngCreator.writeImage(new FileOutputStream(file), image);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            image.close();
                        }
                        else
                        {
                            final RawToDng dngConverter = RawToDng.GetInstance();
                            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);
                            dngConverter.SetBayerData(bytes, file.getAbsolutePath());
                            float fnum, focal = 0;
                            fnum = 2.0f;
                            focal = 4.7f;

                            dngConverter.setExifData(0, 0, 0, fnum, focal, "0", "0", 0);

                            dngConverter.WriteDNG(null);
                            dngConverter.RELEASE();
                            image.close();
                            bytes = null;
                        }
                    }

                    isWorking = false;
                    MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
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
            }).start();
        }
    };

    /**
     * Saves a JPEG {@link android.media.Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

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
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();

                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
