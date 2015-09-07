package com.troop.freedcam.camera2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
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
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.ColorSpaceTransform;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.RggbChannelVector;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualExposureTimeApi2;
import com.troop.freedcam.camera2.parameters.manual.ManualWbCtApi2;
import com.troop.freedcam.camera2.parameters.manual.ZoomApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.ControlModesApi2;
import com.troop.freedcam.camera2.parameters.modes.FlashModeApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    I_Callbacks.PreviewCallback previewCallback;

    public static String JPEG = "jpeg";
    public static String RAW_SENSOR = "raw_sensor";
    public static String RAW10 = "raw10";


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
    public ColorSpaceTransform colorSpaceTransform;

    public String picFormat;
    public String picSize;

    /**
     * An {@link android.media.ImageReader} that handles still image capture.
     */
    public ImageReader mImageReader;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context,I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager Settings)
    {
        super(cameraChangedListner, UIHandler);
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.Settings = Settings;
    }


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
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
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
        if (textureView == null)
            return;
        try {
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getRealSize(size);


            preview = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                    size.x, size.y, largest);
            textureView.setAspectRatio(size.x, size.y);
            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(preview.getWidth(),preview.getHeight());
            configureTransform(textureView.getWidth(), textureView.getHeight());
            surface = new Surface(texture);

            picFormat = Settings.getString(AppSettingsManager.SETTING_PICTUREFORMAT);
            if (picFormat.equals("")) {
                picFormat = JPEG;
                Settings.setString(AppSettingsManager.SETTING_PICTUREFORMAT, JPEG);

            }
            picSize = Settings.getString(AppSettingsManager.SETTING_PICTURESIZE);
            if (picFormat.equals(JPEG))
            {

                String[] split = picSize.split("x");
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
                Log.d(TAG, "ImageReader JPEG");
                mImageReader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            }
            else if (picFormat.equals(RAW_SENSOR))
            {
                Log.d(TAG, "ImageReader RAW_SENOSR");
                largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.RAW_SENSOR, 1);

            }
            else if (picFormat.equals(RAW10))
            {
                Log.d(TAG, "ImageReader RAW10");
                largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW10)), new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.RAW10, 1);
            }



        // We set up a CaptureRequest.Builder with the output Surface.

            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            //if (mImageReader == null)
            //    mCameraDevice.createCaptureSession(Arrays.asList(surface),previewStateCallBack, null);
            //else
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
                ((ParameterHandlerApi2)ParameterHandler).Init();
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
        else if (mPreviewRequestBuilder != null)
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


    int afState;
    int aeState;
    int awbState;
    int lastAwbState;
    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    public CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
        {
            boolean setTOCam = false;
            if (ParameterHandler.ManualShutter != null && ParameterHandler.ManualShutter.IsSupported())
            {
                if (result != null && result.getPartialResults().size() > 0)
                {
                    try
                    {
                        if (!ParameterHandler.ExposureMode.GetValue().equals("off") && !ParameterHandler.ControlMode.equals("off")) {
                            final long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                            String sec = ((ManualExposureTimeApi2) ParameterHandler.ManualShutter).getSECONDSasString(expores);
                            ParameterHandler.ManualShutter.currentValueStringCHanged(StringUtils.TrimmFloatString(sec));
                        }
                    }
                    catch (NullPointerException ex)
                    {

                    }
                    try {
                        final int  iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                        ParameterHandler.ISOManual.currentValueStringCHanged(""+iso);
                    }
                    catch (NullPointerException ex) {}
                    try {
                        final float  mf = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                        ParameterHandler.ManualFocus.currentValueStringCHanged(StringUtils.TrimmFloatString(mf + ""));
                    }
                    catch (NullPointerException ex) {}
                    try {
                        final ColorSpaceTransform res = result.get(TotalCaptureResult.COLOR_CORRECTION_TRANSFORM);
                        ((ManualWbCtApi2)ParameterHandler.CCT).colorSpaceTransform = res;
                    }
                    catch (NullPointerException ex) {}


                }
            }

            if (result.get(CaptureResult.CONTROL_AF_STATE) != null && afState != result.get(CaptureResult.CONTROL_AF_STATE))
            {
                afState =  result.get(CaptureResult.CONTROL_AF_STATE);
                if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState) {
                    if (Focus.focusEvent != null)
                        Focus.focusEvent.FocusFinished(true);

                } else if (CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                    if (Focus.focusEvent != null)
                        Focus.focusEvent.FocusFinished(false);
                }
                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                setTOCam = true;


            }
            if(result.get(CaptureResult.CONTROL_AE_STATE) != null && aeState != result.get(CaptureResult.CONTROL_AE_STATE))
            {
                aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                if (aeState == CaptureResult.CONTROL_AE_STATE_LOCKED || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED )
                {
                    mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                            CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_IDLE);
                    setTOCam = true;
                }
            }

            if (result.get(CaptureResult.CONTROL_AWB_STATE)!= null && awbState != result.get(CaptureResult.CONTROL_AWB_STATE))
            {
                awbState = result.get(CaptureResult.CONTROL_AWB_STATE);
                if (awbState == CaptureResult.CONTROL_AWB_STATE_LOCKED)
                {
                    //colorSpaceTransform = result.get(CaptureResult.COLOR_CORRECTION_TRANSFORM);
                    //RggbChannelVector vector = result.get(CaptureResult.COLOR_CORRECTION_GAINS);
                    //Log.d(TAG, "AWB LOCKED");
                }
                if (awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED)
                {
                    //colorSpaceTransform = result.get(CaptureResult.COLOR_CORRECTION_TRANSFORM);
                    //RggbChannelVector vector = result.get(CaptureResult.COLOR_CORRECTION_GAINS);
                    //Log.d(TAG, "AWB LOCKED");
                }

                /*if (awbState == CaptureResult.CONTROL_AWB_STATE_INACTIVE)
                    Log.d(TAG, "AWB INACTIVE");
                if (awbState == CaptureResult.CONTROL_AWB_STATE_SEARCHING)
                    Log.d(TAG, "AWB SEARCHING");*/
                if (awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED)
                {
                    /*mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AWB_LOCK, true);
                    try {
                        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                                null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                }
            }


            if (result.get(CaptureResult.CONTROL_AWB_MODE) != null && lastAwbState != result.get(CaptureResult.CONTROL_AWB_MODE))
            {
                lastAwbState = result.get(CaptureResult.CONTROL_AWB_MODE);
                if (lastAwbState == CaptureResult.CONTROL_AWB_MODE_OFF)
                {
                    /*mPreviewRequestBuilder.set(CaptureRequest.COLOR_CORRECTION_TRANSFORM,colorSpaceTransform);
                    try {
                        if (mCaptureSession == null)
                            return;
                        mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                                null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }*/
                }
            }
            if (setTOCam && mCaptureSession != null && mPreviewRequestBuilder != null)
            {
                try
                {

                    mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                            null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        private void process(CaptureResult result)
        {
            if (ParameterHandler.ManualShutter != null && ParameterHandler.ManualShutter.IsSupported())
            {
                //String sec = ((ManualExposureTimeApi2)ParameterHandler.ManualShutter).getSECONDSasString(result.get(CaptureResult.SENSOR_EXPOSURE_TIME)) +"";
                //ParameterHandler.ManualShutter.currentValueStringCHanged(sec);
            }
        }
    };



    @Override
    public void StopPreview()
    {

        if (mCaptureSession != null)
            mCaptureSession.close();
        mCaptureSession = null;
    }

    @Override
    public void SetLocation(Location loc) {

    }

    @Override
    public void SetPreviewCallback(I_Callbacks.PreviewCallback previewCallback) {
        this.previewCallback = previewCallback;
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


    public void SetLastUsedParameters(CaptureRequest.Builder builder)
    {
        Log.d(TAG, "set last used parameters");
        if (ParameterHandler.ManualExposure.IsSupported())
        {
            builder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, ParameterHandler.ManualExposure.GetValue());
        }
        /*if (ParameterHandler.ExposureMode.IsSupported())
        {
            builder.set(CaptureRequest.CONTROL_MODE, Enum.valueOf(ControlModesApi2.ControlModes.class, ParameterHandler.ExposureMode.GetValue()).ordinal());
        }*/
        if (ParameterHandler.ManualShutter.IsSupported())
        {
            builder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, (long) ParameterHandler.ManualShutter.GetValue());
        }

        if (ParameterHandler.ColorMode.IsSupported())
        {
            final String set = Settings.getString(AppSettingsManager.SETTING_COLORMODE);
            if (set.equals(""))
            {
                Settings.setString(AppSettingsManager.SETTING_COLORMODE, Enum.valueOf(ColorModeApi2.ColorModes.class, ParameterHandler.ColorMode.GetValue()).toString());
            }
            ColorModeApi2.ColorModes colorModes = Enum.valueOf(ColorModeApi2.ColorModes.class, set);
            builder.set(CaptureRequest.CONTROL_EFFECT_MODE, colorModes.ordinal());
        }
        if (ParameterHandler.SceneMode.IsSupported())
        {
            try
            {
                final String scene = Settings.getString(AppSettingsManager.SETTING_SCENEMODE);
                if (scene.equals(""))
                {
                    Settings.setString(AppSettingsManager.SETTING_SCENEMODE, Enum.valueOf(SceneModeApi2.SceneModes.class, ParameterHandler.SceneMode.GetValue()).toString());
                }
                SceneModeApi2.SceneModes sceneModes = Enum.valueOf(SceneModeApi2.SceneModes.class, scene);
                builder.set(CaptureRequest.CONTROL_SCENE_MODE, sceneModes.ordinal());
            }
            catch (Exception ex)
            {

            }
        }
        /*if (ParameterHandler.FlashMode.IsSupported())
        {
            String flash = Settings.getString(AppSettingsManager.SETTING_FLASHMODE);
            if (flash.equals(""))
            {
                Settings.setString(AppSettingsManager.SETTING_FLASHMODE, FlashModeApi2.OFF);
                flash = FlashModeApi2.OFF;
            }
            ((FlashModeApi2)ParameterHandler.FlashMode).SetToBuilder(builder, flash);
        }*/
        /*if (ParameterHandler.ExposureMode.IsSupported())
        {
            final String controls = Settings.getString(AppSettingsManager.SETTING_EXPOSUREMODE);
            if (controls.equals(""))
            {
                Settings.setString(AppSettingsManager.SETTING_EXPOSUREMODE, Enum.valueOf(ControlModesApi2.ControlModes.class, ParameterHandler.ExposureMode.GetValue()).toString());
            }
            ControlModesApi2.ControlModes controlModes = Enum.valueOf(ControlModesApi2.ControlModes.class, controls);
            builder.set(CaptureRequest.FLASH_MODE, controlModes.ordinal());
        }*/
    }



}
