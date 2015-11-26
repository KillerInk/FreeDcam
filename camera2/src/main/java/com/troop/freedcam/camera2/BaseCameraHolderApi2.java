package com.troop.freedcam.camera2;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.location.Location;
import android.media.ImageReader;
import android.os.Build;
import android.os.DeadObjectException;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.camera2.parameters.modes.ColorModeApi2;
import com.troop.freedcam.camera2.parameters.modes.FocusModeApi2;
import com.troop.freedcam.camera2.parameters.modes.SceneModeApi2;
import com.troop.freedcam.camera2.parameters.modes.WhiteBalanceApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import troop.com.imageconverter.ViewfinderProcessor;

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

    //this is needed for the previewSize...
    public CaptureRequest.Builder mPreviewRequestBuilder;
    I_Callbacks.PreviewCallback previewCallback;

    public static String JPEG = "jpeg";
    public static String RAW_SENSOR = "raw_sensor";
    public static String RAW10 = "raw10";
    /**
     * A {@link CameraCaptureSession } for camera previewSize.
     */
    public CameraCaptureSession mCaptureSession;
    public StreamConfigurationMap map;

    public CaptureRequest mPreviewRequest;

    public int CurrentCamera;

    public CameraCharacteristics characteristics;
    public Surface previewsurface;
    Surface camerasurface;
    AppSettingsManager Settings;

    public String picFormat;
    public String picSize;
    Size previewSize;
    private Size largestImageSize;
    private Point displaySize;
    int mImageWidth, mImageHeight;

    RenderScript mRS;
    ViewfinderProcessor mProcessor;

    int afState;
    int aeState;
    int awbState;
    int lastAwbState;
    private boolean focuspeakEnable = false;

    Handler backgroundHandler;
    boolean errorRecieved = false;
    /**
     * An {@link android.media.ImageReader} that handles still image capture.
     */
    public ImageReader mImageReader;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context,I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager Settings, Handler backgroundHandler)
    {
        super(cameraChangedListner, UIHandler);
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        this.Settings = Settings;
        this.backgroundHandler = backgroundHandler;

    }


    //###########################  public camera methods
    //###########################
    //###########################

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean OpenCamera(int camera)
    {
        //startBackgroundThread();
        Log.d(TAG,"Open Camera");
        CurrentCamera = camera;
        String cam = camera +"";
        if (context.checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            errorHandler.OnError("Error: Permission for Camera are not granted!");
            return false;
        }

        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cam, mStateCallback,backgroundHandler);
            characteristics = manager.getCameraCharacteristics(CurrentCamera + "");
            if (!isLegacyDevice())
            {
                mRS = RenderScript.create(Settings.context);
                mProcessor = new ViewfinderProcessor(mRS);
                //printCharacteristics();
            }
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            return  false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void printCharacteristics()
    {
        BlackLevelPattern pattern = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN);
        Log.d(TAG, "Blacklevel:" + pattern.toString());
        Log.d(TAG, "Whitelevel:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL).toString());
        Log.d(TAG, "SensorCalibration1:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1).toString());
        Log.d(TAG, "SensorCalibration2:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2).toString());
        Log.d(TAG, "SensorColorMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1).toString());
        Log.d(TAG, "SensorColorMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2).toString());
        Log.d(TAG, "ForwardMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1).toString());
        Log.d(TAG, "ForwardMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2).toString());
        Log.d(TAG, "ExposureTImeMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper().toString());
        Log.d(TAG, "ExposureTImeMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower().toString());
        Log.d(TAG, "FrameDuration:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION).toString());
        Log.d(TAG, "SensorIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper().toString());
        Log.d(TAG, "SensorIsoMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower().toString());
        Log.d(TAG, "SensorAnalogIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY).toString());
    }

    @Override
    public void CloseCamera() {
        try {
            Log.d(TAG,"Close Camera");
            mCameraOpenCloseLock.acquire();
            mProcessor.kill();
            try {
                if (null != mCaptureSession) {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
            }
           catch (Exception e) {}

            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        catch (Exception e) {
            //throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        }
        finally {
            mCameraOpenCloseLock.release();
        }
    }

    @Override
    public int CameraCout() {
        return CameraCountId().length;
    }


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
            Log.d(TAG,"Start Preview");
            largestImageSize = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            displaySize = new Point();
            display.getRealSize(displaySize);


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
                    mImageWidth = largestImageSize.getWidth();
                    mImageHeight = largestImageSize.getHeight();
                }
                else
                {
                    mImageWidth = Integer.parseInt(split[0]);
                    mImageHeight = Integer.parseInt(split[1]);
                }
                //create new ImageReader with the size and format for the image
                Log.d(TAG, "ImageReader JPEG");
            }
            else if (picFormat.equals(RAW_SENSOR))
            {
                Log.d(TAG, "ImageReader RAW_SENOSR");
                largestImageSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CompareSizesByArea());
                mImageWidth = largestImageSize.getWidth();
                mImageHeight = largestImageSize.getHeight();
            }
            else if (picFormat.equals(RAW10))
            {
                Log.d(TAG, "ImageReader RAW_SENOSR");
                largestImageSize = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.RAW10)), new CompareSizesByArea());
                mImageWidth = largestImageSize.getWidth();
                mImageHeight = largestImageSize.getHeight();
            }


            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            //OrientationHACK
            if(Settings.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
                mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 180);
            else
                mPreviewRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, 0);

            // Here, we create a CameraCaptureSession for camera previewSize.
            if (ParameterHandler.Burst == null)
                SetBurst(1);
            else
                SetBurst(ParameterHandler.Burst.GetValue()+1);

        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
            return;
        }

    }

    public void SetBurst(int burst)
    {
        try {
            Log.d(TAG,"Set Burst to:" + burst);
            previewSize = getSizeForPreviewDependingOnImageSize(map.getOutputSizes(ImageFormat.YUV_420_888));

            SurfaceTexture texture = textureView.getSurfaceTexture();
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
            previewsurface = new Surface(texture);
            if (!isLegacyDevice())
            {
                if (mProcessor != null) {
                    mProcessor.kill();
                }
                mProcessor.Reset(previewSize.getWidth(), previewSize.getHeight());

                mProcessor.setOutputSurface(previewsurface);
                camerasurface = mProcessor.getInputSurface();
                mPreviewRequestBuilder.addTarget(camerasurface);
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                Matrix matrix = new Matrix();
                RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);
                matrix.setRectToRect(viewRect, viewRect, Matrix.ScaleToFit.FILL);
                if (Settings.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
                    matrix.postRotate(180, viewRect.centerX(), viewRect.centerY());
                else
                    matrix.postRotate(0, viewRect.centerX(), viewRect.centerY());
                textureView.setTransform(matrix);
            }
            else
            {
                mPreviewRequestBuilder.addTarget(previewsurface);
                configureTransform();
                //textureView.setAspectRatio(mImageWidth,mImageHeight);
            }

            if (picFormat.equals(JPEG))
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.JPEG, burst);
            else if (picFormat.equals(RAW10))
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW10, burst);
            else
                mImageReader = ImageReader.newInstance(mImageWidth, mImageHeight, ImageFormat.RAW_SENSOR, burst);

            if (isLegacyDevice())
                createPreviewCaptureSession(previewsurface);
            else
                createPreviewCaptureSession(camerasurface);
            
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (ParameterHandler.Burst != null)
            ParameterHandler.Burst.currentValueChanged(ParameterHandler.Burst.GetValue());
    }

    public void setIntKeyToCam(CaptureRequest.Key<Integer> key, int value)
    {
       /* if (errorRecieved)
        {
            errorRecieved = false;
            //StopPreview();
            //StartPreview();
            return;
        }*/
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

    private void configureTransform() {

        DisplayManager windowManager = (DisplayManager)context.getSystemService(Context.DISPLAY_SERVICE);
        int rotation = windowManager.getDisplay(0).getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);
        RectF bufferRect = new RectF(0, 0, previewSize.getWidth(), previewSize.getHeight());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        //rotation = 1;
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);

            float scalex =(float) displaySize.x / displaySize.y;
            float scaley = (float) previewSize.getWidth() / previewSize.getHeight();
            float xy = scalex -scaley +2;
            matrix.postScale(xy - 1, xy, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
        //textureView.setAspectRatio(previewSize.getWidth(),previewSize.getHeight());
    }

    @Override
    public void StopPreview()
    {
        Log.d(TAG,"Stop Preview");
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
        ParameterHandler.SetAppSettingsToParameters();
        Log.d(TAG, "set last used parameters");
    }

    public CaptureRequest.Builder createCaptureRequest(int template) throws CameraAccessException {
        CameraDevice device = mCameraDevice;
        if (device == null) {
            throw new IllegalStateException("Can't get requests when no camera is open");
        }
        return device.createCaptureRequest(template);
    }

    public void FocusPeakEnable(boolean enable)
    {
        mProcessor.peak = enable;
    }

    public boolean isFocuspeakEnable()
    {
        return mProcessor.peak;
    }


    //###########################  CALLBACKS
    //###########################
    //###########################

    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera previewSize here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            Log.d(TAG,"Camera open");

            if (UIHandler != null)
                UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen("");
                }
            });
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice)
        {
            Log.d(TAG,"Camera Disconnected");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, final int error)
        {
            Log.d(TAG, "Camera Error" + error);
            mCameraOpenCloseLock.release();
            /*cameraDevice.close();
            mCameraDevice = null;*/
            errorRecieved = true;
            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraError("Error:" + error);
                }
            });

        }
    };

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
                            try {
                                final long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                if(expores != 0) {
                                    ParameterHandler.ManualShutter.currentValueStringCHanged(getShutterString(expores));
                                }
                                else
                                    ParameterHandler.ManualShutter.currentValueStringCHanged("1/60");
                            }
                            catch (Exception e)
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
                        }
                    }
                    catch (NullPointerException ex)
                    {

                    }


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
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }

        private void process(CaptureResult result)
        {
        }
    };



    CameraCaptureSession.StateCallback previewStateCallBackFirstStart = new CameraCaptureSession.StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            // The camera is already closed
            if (null == mCameraDevice)
            {
                return;
            }

            // When the session is ready, we start displaying the previewSize.
            BaseCameraHolderApi2.this.mCaptureSession = cameraCaptureSession;
            try {
                ((ParameterHandlerApi2)ParameterHandler).Init();
                // Finally, we start displaying the camera previewSize.
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, null);
                SetLastUsedParameters(mPreviewRequestBuilder);

            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };

    CameraCaptureSession.StateCallback previewStateCallBackRestart = new CameraCaptureSession.StateCallback()
    {

        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession)
        {
            // The camera is already closed
            if (null == mCameraDevice)
            {
                return;
            }

            // When the session is ready, we start displaying the previewSize.
            mCaptureSession = cameraCaptureSession;
            try {
                // Finally, we start displaying the camera previewSize.
                mPreviewRequest = mPreviewRequestBuilder.build();
                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                        mCaptureCallback, null);
                SetLastUsedParameters(mPreviewRequestBuilder);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };

    //###########################  private helper methods
    //###########################
    //###########################

    private String getShutterString(long val)
    {
        try {
            int mili = (int) val / 10000;
            //double sec =  mili / 1000;
            if (mili < 80000)
                return 1 + "/" + (10000000 / mili);
            else {
                float t = mili / 10000;
                return String.format("%01.1f", t);
            }
        }
        catch (Exception ex)
        {
            return "1/60";
        }
    }


    private Size getSizeForPreviewDependingOnImageSize(Size[] choices)
    {
        List<Size> sizes = new ArrayList<Size>();
        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= 1280 && s.getHeight() <= 720 && ((double)s.getWidth()/s.getHeight()) == ratio)
                sizes.add(s);

        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            return choices[0];
        }
    }

    public boolean isLegacyDevice()
    {
        if (characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
            return false;
        else
            return true;
    }

    private void createPreviewCaptureSession(Surface surface) throws CameraAccessException {
        if (ParameterHandler.Burst == null)
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), previewStateCallBackFirstStart, null);
        else
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), previewStateCallBackRestart, null);
    }
}
