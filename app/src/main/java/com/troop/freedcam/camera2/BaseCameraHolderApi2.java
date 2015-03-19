package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.camera2.parameters.manual.ZoomApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ControlModesApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.AutoFitTextureView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BaseCameraHolderApi2 extends AbstractCameraHolder
{
    private static String TAG = "freedcam.BaseCameraHolderApi2";
    public boolean isWorking = false;
    Context context;
    public I_error errorHandler;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    public AutoFitTextureView textureView;

    //this is needed for the preview...
    public CaptureRequest.Builder mPreviewRequestBuilder;
    private TotalCaptureResult mDngResult;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    public CameraCaptureSession mCaptureSession;
    public StreamConfigurationMap map;

    public CaptureRequest mPreviewRequest;

    public int CurrentCamera;
    Size preview;
    public CameraCharacteristics characteristics;
    public Surface surface;
    AppSettingsManager Settings;

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

    /**
     * An {@link android.media.ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context,I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager Settings)
    {
        super(cameraChangedListner, UIHandler);
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.Settings = Settings;
    }

    /*HandlerThread mBackgroundThread;
    public Handler mBackgroundHandler;

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }


    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean OpenCamera(int camera)
    {
        //startBackgroundThread();
        CurrentCamera = camera;
        String cam = camera +"";
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cam, mStateCallback, null);
            characteristics = manager.getCameraCharacteristics(CurrentCamera+"");
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return  false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void CloseCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            /*if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }*/
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
        //stopBackgroundThread();
    }

    @Override
    public int CameraCout() {
        return CameraCountId().length;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String[] CameraCountId()
    {
        try {
            return manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean IsRdy() {
        return super.IsRdy();
    }

    @Override
    public boolean SetCameraParameters(HashMap<String,String> parameters) {
        return  false;
    }

    public boolean SetSurface(TextureView surfaceHolder)
    {
        this.textureView = (AutoFitTextureView) surfaceHolder;
        return true;
    }

    @Override
    public void StartPreview()
    {

        try {
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();


            preview = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    display.getWidth(), display.getHeight(), largest);
            textureView.setAspectRatio(preview.getWidth(), preview.getHeight());
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(preview.getWidth(),preview.getHeight());
            configureTransform(textureView.getWidth(), textureView.getHeight());
            surface = new Surface(texture);

            String picformat = Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
            if (picformat.equals(""))
                picformat="jpeg";
            if (picformat.equals("jpeg"))
            {
                String[] split = Settings.getString(AppSettingsManager.SETTING_PICTURESIZE).split("x");
                int width, height;
                if (split.length < 2)
                {
                    width = largest.getWidth();
                    height = largest.getHeight();
                }
                else
                {
                    width = Integer.parseInt(split[0]);
                    height = Integer.parseInt(split[1]);
                }
                //create new ImageReader with the size and format for the image
                mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 2);
                mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);
            }
            else
            {
                largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.RAW_SENSOR, 2);
                mImageReader.setOnImageAvailableListener(mOnRawImageAvailableListener, null);
            }



        // We set up a CaptureRequest.Builder with the output Surface.

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            if (mImageReader == null)
                mCameraDevice.createCaptureSession(Arrays.asList(surface),previewStateCallBack, null);
            else
                mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), previewStateCallBack, null);


        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
            return;
        }

    }

    CameraCaptureSession.StateCallback previewStateCallBack = new CameraCaptureSession.StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            // The camera is already closed
            if (null == mCameraDevice)
            {
                return;
            }

            // When the session is ready, we start displaying the preview.
            mCaptureSession = cameraCaptureSession;
            try {
                // Auto focus should be continuous for camera preview.
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // Flash is automatically enabled when necessary.
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                // Finally, we start displaying the camera preview.
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };

    public void setIntKeyToCam(CaptureRequest.Key<Integer> key, int value)
    {
        if (mCaptureSession != null)
        {
            //StopPreview();
            try {
                mPreviewRequestBuilder.set(key, value);
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                        null);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        else
        {
            mPreviewRequestBuilder.set(key, value);
        }
    }


    private void configureTransform(int viewWidth, int viewHeight) {

        DisplayManager windowManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
        int rotation = windowManager.getDisplay(0).getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, preview.getHeight(), preview.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / preview.getHeight(),
                    (float) viewWidth / preview.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    public CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result)
        {

        }
    };

    @Override
    public void StopPreview()
    {

        if (mCaptureSession != null)
            mCaptureSession.close();
    }

    @Override
    public void SetLocation(Location loc) {

    }

    public Camera.Parameters GetCameraParameters() {
        return null;
    }


    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;


            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen("");
                }
            });

            ((ParameterHandlerApi2)ParameterHandler).Init();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;

        }
    };

    private static Size chooseOptimalSize(Size[] choices, int width, int height, Size aspectRatio) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<Size>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getHeight() == option.getWidth() * h / w &&
                    option.getWidth() >= width && option.getHeight() >= height) {
                bigEnough.add(option);
            }
        }

        // Pick the smallest of those, assuming we found any
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }



    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), CaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    CameraCaptureSession.CaptureCallback CaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    int afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_WAITING_NON_PRECAPTURE;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
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
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), CaptureCallback,
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

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

            Rect zoom = ZoomApi2.getZoomRect(ParameterHandler.Zoom.GetValue(), textureView.getWidth(), textureView.getHeight());
            captureBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);

            if (ParameterHandler.ManualExposure.IsSupported())
            {
                captureBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ParameterHandler.ManualExposure.GetValue());
            }
            if (ParameterHandler.ExposureMode.IsSupported())
            {
                captureBuilder.set(CaptureRequest.CONTROL_MODE, Enum.valueOf(ControlModesApi2.ControlModes.class, ParameterHandler.ExposureMode.GetValue()).ordinal());
            }
            if (ParameterHandler.ManualShutter.IsSupported())
            {
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long) ParameterHandler.ManualShutter.GetValue());
            }

            if (ParameterHandler.ColorMode.IsSupported()) {
                ColorModeApi2.ColorModes colorModes = Enum.valueOf(ColorModeApi2.ColorModes.class, ParameterHandler.ColorMode.GetValue());
                captureBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, colorModes.ordinal());
            }

            if (ParameterHandler.SceneMode.IsSupported()) {
                SceneModeApi2.SceneModes sceneModes = Enum.valueOf(SceneModeApi2.SceneModes.class, ParameterHandler.SceneMode.GetValue());
                captureBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, sceneModes.ordinal());
            }


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
            Log.d(TAG, "StartCapture");
            mCaptureSession.stopRepeating();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is finished.
     */
    private void unlockFocus() {
        try {
            // Reset the autofucos trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), CaptureCallback,
                    null);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, CaptureCallback,
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
            Log.d(TAG, "Recieved on onImageAvailabel");
            isWorking = false;
            //StartPreview();
        }

    };

    private final ImageReader.OnImageAvailableListener mOnRawImageAvailableListener = new ImageReader.OnImageAvailableListener()
    {
        @Override
        public void onImageAvailable(ImageReader reader) {
            try {
                File file = new File(getStringAddTime() +".dng");
                DngCreator dngCreator = new DngCreator(manager.getCameraCharacteristics("0"), mDngResult);
                dngCreator.writeImage(new FileOutputStream(file), reader.acquireNextImage());
               isWorking = false;
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void TakePicture()
    {
        isWorking = true;
        lockFocus();
    }

}
