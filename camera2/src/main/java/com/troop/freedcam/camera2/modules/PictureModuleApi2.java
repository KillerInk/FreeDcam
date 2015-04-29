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
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;
    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;
    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;
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
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try
        {

            // This is how to tell the camera to lock focus.
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW:
                {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK:
                {
                    Log.d(TAG, "STATE WAITING LOCK");
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED || aeState == CaptureResult.CONTROL_AE_STATE_LOCKED)
                        {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE:
                {
                    Log.d(TAG, "STATE WAITING PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE:
                {
                    Log.d(TAG, "STATE WAITING NON PRECAPTURE");
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request,
                                        CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                       TotalCaptureResult result) {
            process(result);
            mDngResult = result;

        }


    };

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when we
     * get a response in {@link #CaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            Log.d(TAG, "Run Precapture");
            // This is how to tell the camera to trigger.
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #} from both {@link #lockFocus()}.
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
                    cameraHolder.mPreviewRequest.get(CaptureRequest.CONTROL_AF_MODE));
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, cameraHolder.mPreviewRequest.get(CaptureRequest.CONTROL_AE_MODE));
            captureBuilder.set(CaptureRequest.FLASH_MODE, cameraHolder.mPreviewRequest.get(CaptureRequest.FLASH_MODE));
            //captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);


            cameraHolder.SetLastUsedParameters(captureBuilder);


            // Orientation
            //int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            //captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request,
                                               TotalCaptureResult result) {
                    //Toast.makeText(getActivity(), "Saved: " + mFile, Toast.LENGTH_SHORT).show();
                    unlockFocus();
                }
            };

            cameraHolder.mCaptureSession.stopRepeating();
            cameraHolder.mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is finished.
     */
    private void unlockFocus() {
        try {
            Log.d(TAG, "CaptureDone Unlock Focus");
            // Reset the autofucos trigger
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            //cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON);

            cameraHolder.SetLastUsedParameters(cameraHolder.mPreviewRequestBuilder);
            //cameraHolder.mCaptureSession.capture(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback, null);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), CaptureCallback,
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
                    DngCreator dngCreator = new DngCreator(cameraHolder.manager.getCameraCharacteristics("0"), mDngResult);
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
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
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
