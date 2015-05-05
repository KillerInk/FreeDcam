package com.troop.freedcam.camera2.modules;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualWbCtApi2;
import com.troop.freedcam.camera2.parameters.manual.ZoomApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ControlModesApi2;
import com.troop.freedcam.camera2.parameters.modes.FlashModeApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        if (Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT).equals("jpeg"))
            cameraHolder.mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
        else
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
            final CaptureRequest.Builder captureBuilder =
                    cameraHolder.mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(cameraHolder.mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AF_MODE));
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AE_MODE));
            captureBuilder.set(CaptureRequest.FLASH_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.FLASH_MODE));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.COLOR_CORRECTION_MODE));
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_GAINS, ((ManualWbCtApi2)cameraHolder.ParameterHandler.CCT).rggbChannelVector);
            int awb = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_AWB_MODE);
            captureBuilder.set(CaptureRequest.CONTROL_AWB_MODE, awb );
            captureBuilder.set(CaptureRequest.EDGE_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.EDGE_MODE));
            captureBuilder.set(CaptureRequest.HOT_PIXEL_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.HOT_PIXEL_MODE));
            captureBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE, cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.NOISE_REDUCTION_MODE));
            //captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);


            cameraHolder.SetLastUsedParameters(captureBuilder);


            // Orientation
            //int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                               TotalCaptureResult result)
                {
                    mDngResult = result;
                    //Toast.makeText(getActivity(), "Saved: " + mFile, Toast.LENGTH_SHORT).show();
                    finishCapture();
                }
            };

            cameraHolder.mCaptureSession.stopRepeating();
            cameraHolder.mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    
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


    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader)
        {
            File file = new File(getStringAddTime() +".jpg");
            new ImageSaver(reader.acquireNextImage(), file).run();
            //mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
            Log.d(TAG, "create Jpeg");
            isWorking = false;
            workfinished(true);
            MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
            //StartPreview();
        }

    };

    private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener = new ImageReader.OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(ImageReader reader) {
            try {
                if (cameraHolder.ParameterHandler.isDngActive)
                {
                    Log.d(TAG, "Create DNG");
                    File file = new File(getStringAddTime() + ".dng");
                    DngCreator dngCreator = new DngCreator(cameraHolder.characteristics, mDngResult);
                    final Image image = reader.acquireNextImage();
                    dngCreator.writeImage(new FileOutputStream(file), image);
                    image.close();
                    isWorking = false;
                    MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
                    eventHandler.WorkFinished(file);
                }
                else
                {
                    Log.d(TAG, "Create RAW");
                    File file = new File(getStringAddTime() +".raw");
                    new ImageSaver(reader.acquireNextImage(), file).run();
                    MediaScannerManager.ScanMedia(Settings.context.getApplicationContext(), file);
                    eventHandler.WorkFinished(file);
                }
            }  catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            workfinished(true);
        }
    };

    protected String getStringAddTime()
    {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/FreeCam/");
        if (!file.exists())
            file.mkdirs();
        Date date = new Date();
        String s = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(date);
        return (new StringBuilder(String.valueOf(file.getPath()))).append(File.separator).append("IMG_").append(s).toString();
    }

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
