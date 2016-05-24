package com.freedcam.apis.camera2.camera;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.renderscript.RenderScript;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.camera2.camera.modules.I_PreviewWrapper;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandlerApi2;
import com.freedcam.apis.camera2.camera.renderscript.FocuspeakProcessorApi2;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.RenderScriptHandler;
import com.freedcam.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraHolderApi2 extends AbstractCameraHolder
{
    private static String TAG = CameraHolderApi2.class.getSimpleName();
    public static String JPEG = "jpeg";
    public static String RAW_SENSOR = "raw_sensor";
    public static String RAW10 = "raw10";
    public static String RAW12 = "raw12";

    public boolean isWorking = false;
    private Context context;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private AutoFitTextureView textureView;

    //this is needed for the previewSize...
    private CaptureRequest.Builder mPreviewRequestBuilder;
    I_Callbacks.PreviewCallback previewCallback;

    private CameraCaptureSession mCaptureSession;
    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;
    public String VideoSize;
    public I_PreviewWrapper ModulePreview;
    public FocuspeakProcessorApi2 mProcessor;
    public CaptureSessionHandler CaptureSessionH;
    private RenderScriptHandler renderScriptHandler;

    int afState;
    int aeState;

    boolean errorRecieved = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CameraHolderApi2(Context context, I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager appSettingsManager, RenderScriptHandler renderScriptHandler)
    {
        super(cameraChangedListner, UIHandler,appSettingsManager);
        this.context = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CaptureSessionH = new CaptureSessionHandler();
        this.renderScriptHandler = renderScriptHandler;

    }

    //###########################  public camera methods
    //###########################
    //###########################

    @Override
    public boolean OpenCamera(int camera)
    {
        Logger.d(TAG, "Open Camera");
        CurrentCamera = camera;
        String cam = camera +"";
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraChangedListner.onCameraError("Error: Permission for Camera are not granted!");
                return false;
            }
        }
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(cam, mStateCallback, null);
            characteristics = manager.getCameraCharacteristics(CurrentCamera + "");
            if (!isLegacyDevice())
            {
                mProcessor = new FocuspeakProcessorApi2(renderScriptHandler);
                //printCharacteristics();
            }
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        } catch (CameraAccessException e) {
            Logger.exception(e);
            return  false;
        } catch (InterruptedException e) {
            Logger.exception(e);
        }
        return true;
    }


    private void printCharacteristics()
    {
        BlackLevelPattern pattern = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN);
        Logger.d(TAG, "Blacklevel:" + pattern.toString());
        Logger.d(TAG, "Whitelevel:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL).toString());
        Logger.d(TAG, "SensorCalibration1:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1).toString());
        Logger.d(TAG, "SensorCalibration2:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2).toString());
        Logger.d(TAG, "SensorColorMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1).toString());
        Logger.d(TAG, "SensorColorMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2).toString());
        Logger.d(TAG, "ForwardMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1).toString());
        Logger.d(TAG, "ForwardMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2).toString());
        Logger.d(TAG, "ExposureTImeMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper().toString());
        Logger.d(TAG, "ExposureTImeMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower().toString());
        Logger.d(TAG, "FrameDuration:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION).toString());
        Logger.d(TAG, "SensorIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper().toString());
        Logger.d(TAG, "SensorIsoMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower().toString());
        Logger.d(TAG, "SensorAnalogIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY).toString());
    }

    @Override
    public void CloseCamera()
    {
        try {
            Logger.d(TAG,"Close Camera");
            mCameraOpenCloseLock.acquire();
            try
            {
                if (null != mCaptureSession)
                {
                    mProcessor.kill();
                    mCaptureSession.stopRepeating();
                    mCaptureSession.abortCaptures();
                    mCaptureSession.close();
                    CaptureSessionH.Clear();
                    mCaptureSession = null;

                }
            }
           catch (Exception e)
           {
               Logger.exception(e);
           }

            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        catch (Exception e) {
            //throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        }
        finally
        {
            mCameraOpenCloseLock.release();
            if (UIHandler != null)
                UIHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        cameraChangedListner.onCameraClose("");
                    }
                });
            Logger.d(TAG, "camera closed");
        }
        super.CloseCamera();
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
            Logger.exception(e);
        }
        return null;
    }

    @Override
    public boolean IsRdy() {
        return super.IsRdy();
    }


    public boolean SetSurface(TextureView surfaceHolder)
    {
        this.textureView = (AutoFitTextureView) surfaceHolder;
        return true;
    }

    @Override
    public void StartPreview()
    {
        if (textureView == null || ModulePreview == null)
            return;
        ModulePreview.startPreview();
    }
    @Override
    public void StopPreview()
    {
        if (ModulePreview != null)
            ModulePreview.stopPreview();
    }

    public <T> void SetParameterRepeating(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value.toString());
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartRepeatingCaptureSession();
    }

    public <T> void SetParameter(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value.toString());
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartCaptureSession();
    }

    public <T> void SetFocusArea(@NonNull CaptureRequest.Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value.toString());
        mPreviewRequestBuilder.set(key,value);
        SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
    }

    public <T> T get(CaptureRequest.Key<T> key)
    {
        if (mPreviewRequestBuilder == null)
            return null;
        return mPreviewRequestBuilder.get(key);
    }

    @Override
    public void SetLocation(Location loc)
    {

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
    public static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }


    public CaptureRequest.Builder createCaptureRequest() throws CameraAccessException {
        CameraDevice device = mCameraDevice;
        if (device == null) {
            throw new IllegalStateException("Can't get requests when no camera is open");
        }
        return device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
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
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera previewSize here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;

            Logger.d(TAG, "Camera open");
            if (UIHandler != null)
                UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen("");
                }
            });

            CaptureSessionH.CreatePreviewRequestBuilder();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            Logger.d(TAG,"Camera Disconnected");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, final int error)
        {
            Logger.d(TAG, "Camera Error" + error);
            mCameraOpenCloseLock.release();
            errorRecieved = true;
            UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraError("Error:" + error);
                }
            });

        }
    };

    public CameraCaptureSession.CaptureCallback cameraBackroundValuesChangedListner = new CameraCaptureSession.CaptureCallback()
    {
        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
        {
            //Logger.d(TAG,result.get(TotalCaptureResult.SENSOR_SENSITIVITY).toString() + " / " + request.get(CaptureRequest.SENSOR_SENSITIVITY).toString());
            //Logger.d(TAG,result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME).toString() + " / " + request.get(CaptureRequest.SENSOR_EXPOSURE_TIME));
            if (GetParameterHandler().ManualShutter != null && GetParameterHandler().ManualShutter.IsSupported())
            {
                if (result != null && result.getPartialResults().size() > 0)
                {
                    try
                    {
                        if (!GetParameterHandler().ExposureMode.GetValue().equals("off") && !GetParameterHandler().ControlMode.equals("off"))
                        {
                            try {
                                final long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                if(expores != 0) {
                                    GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged(getShutterString(expores));
                                }
                                else
                                    GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged("1/60");
                            }
                            catch (Exception e)
                            {
                                Logger.exception(e);
                            }
                            try {
                                final int  iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                GetParameterHandler().ISOManual.ThrowCurrentValueStringCHanged("" + iso);
                            }
                            catch (NullPointerException ex) {
                                Logger.exception(ex);
                            }
                            try {
                                final float  mf = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                GetParameterHandler().ManualFocus.ThrowCurrentValueStringCHanged(StringUtils.TrimmFloatString4Places(mf + ""));
                            }
                            catch (NullPointerException ex) {Logger.exception(ex);}
                        }
                    }
                    catch (NullPointerException ex)
                    {
                        Logger.exception(ex);
                    }
                }
            }

            if (result.get(CaptureResult.CONTROL_AF_STATE) != null && afState != result.get(CaptureResult.CONTROL_AF_STATE))
            {
                afState =  result.get(CaptureResult.CONTROL_AF_STATE);
                String state = "";
                switch (afState)
                {
                    case 0:
                        state ="INACTIVE";
                        break;
                    case 1:
                        state = "PASSIVE_SCAN";
                        break;
                    case 2:
                        state = "PASSIVE_FOCUSED";
                        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,
                                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                        break;
                    case 3:
                        state="ACTIVE_SCAN";
                        break;
                    case 4:
                        state = "FOCUSED_LOCKED";
                        if (Focus.focusEvent != null)
                            Focus.focusEvent.FocusFinished(true);

                        break;
                    case 5:
                        state = "NOT_FOCUSED_LOCKED";
                        if (Focus.focusEvent != null)
                            Focus.focusEvent.FocusFinished(false);
                        break;
                    case 6:
                        state ="PASSIVE_UNFOCUSED";
                        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,
                                CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
                        break;
                }
                Logger.d(TAG, "new AF_STATE :"+state);
            }
            if(result.get(CaptureResult.CONTROL_AE_STATE) != null && aeState != result.get(CaptureResult.CONTROL_AE_STATE))
            {

                aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                switch (aeState)
                {
                    case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                        Logger.d(TAG, "AESTATE: Converged");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                        Logger.d(TAG, "AESTATE: FLASH_REQUIRED");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_INACTIVE:
                        Logger.d(TAG, "AESTATE: INACTIVE");

                        break;
                    case CaptureResult.CONTROL_AE_STATE_LOCKED:
                        Logger.d(TAG, "AESTATE: LOCKED");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_PRECAPTURE:
                        Logger.d(TAG, "AESTATE: PRECAPTURE");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_SEARCHING:
                        Logger.d(TAG, "AESTATE: SEARCHING");
                        break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }
    };



    //###########################  private helper methods
    //###########################
    //###########################

    private String getShutterString(long val)
    {
        int mili = (int) val / 10000;
        //double sec =  mili / 1000;
        if (mili < 80000)
            return 1 + "/" + (10000000 / mili);
        else {
            float t = mili / 10000;
            return String.format("%01.1f", t);
        }
    }


    public static Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
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
            Logger.e(TAG, "Couldn't find any suitable previewSize size");
            return choices[0];
        }
    }

    public boolean isLegacyDevice()
    {
        return characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
    }

    public static boolean IsLegacy(AppSettingsManager appSettingsManager,Context context)
    {
        boolean legacy = true;
        Semaphore mCameraOpenCloseLock = new Semaphore(1);
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
            legacy = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
            manager = null;
            characteristics = null;
        }
        catch (CameraAccessException | VerifyError | InterruptedException e) {
            Logger.exception(e);
        } catch (Exception ex)
        {
            Logger.exception(ex);
        }
        finally
        {

            mCameraOpenCloseLock.release();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Logger.exception(e);
            }
        }
        return  legacy;
    }

    public class CaptureSessionHandler
    {
        private String TAG = CaptureSessionHandler.class.getSimpleName();
        private List<Surface> surfaces;
        private Point displaySize;
        public CaptureSessionHandler()
        {
            surfaces = new ArrayList<>();
            Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            displaySize = new Point();
            display.getRealSize(displaySize);
        }

        public void SetCaptureSession(CameraCaptureSession cameraCaptureSession)
        {
            mCaptureSession = cameraCaptureSession;
        }

        public void CreatePreviewRequestBuilder()
        {
            try {
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                ((ParameterHandlerApi2)GetParameterHandler()).Init();
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public CameraCaptureSession GetActiveCameraCaptureSession()
        {
            return mCaptureSession;
        }

        public SurfaceTexture getSurfaceTexture()
        {
            return textureView.getSurfaceTexture();
        }

        public void AddSurface(Surface surface, boolean addtoPreviewRequestBuilder)
        {
            Logger.d(this.TAG, "AddSurface");
            if (surfaces.contains(surface))
                return;
            surfaces.add(surface);
            if (addtoPreviewRequestBuilder)
            {
                mPreviewRequestBuilder.addTarget(surface);
            }
        }

        public void RemoveSurface(Surface surface)
        {
            Logger.d(this.TAG, "RemoveSurface");
            if (surfaces.contains(surface))
                surfaces.remove(surface);
            mPreviewRequestBuilder.removeTarget(surface);

        }

        public void Clear()
        {
            Logger.d(this.TAG, "Clear");
            if (mPreviewRequestBuilder != null)
                for (Surface s: surfaces)
                    mPreviewRequestBuilder.removeTarget(s);
            surfaces.clear();

        }

        public void CreateCaptureSession()
        {
            if(mCameraDevice == null)
                return;
            Logger.d(this.TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
            } catch (CameraAccessException | SecurityException e) {
                Logger.exception(e);
            }
        }

        public void CreateCaptureSession(CameraCaptureSession.StateCallback customCallback)
        {
            Logger.d(this.TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, customCallback, null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StopRepeatingCaptureSession()
        {
            if (mCaptureSession != null)
            try {
                mCaptureSession.stopRepeating();
            } catch (CameraAccessException e) {
                Logger.exception(e);

            }
            catch (IllegalStateException ex)
            {
                Logger.exception(ex);
                mCaptureSession = null;
            }
        }

        public void StartRepeatingCaptureSession()
        {
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StartCaptureSession()
        {
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException e) {
               Logger.exception(e);
            }
        }

        public void StartCapture(@NonNull CaptureRequest.Builder request,
                                 @Nullable CameraCaptureSession.CaptureCallback listener, @Nullable Handler handler)
        {
            try {
                mCaptureSession.capture(request.build(),listener,handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }


        public void CloseCaptureSession()
        {
            StopRepeatingCaptureSession();
            Clear();
            if (mCaptureSession != null)
            {
                try {
                    mCaptureSession.abortCaptures();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                mCaptureSession.close();

            }
            mCaptureSession = null;

        }

        public void SetTextureViewSize(int w, int h, int orientation, int orientationWithHack,boolean video)
        {
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, displaySize.x, displaySize.y);
            Logger.d(TAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);
            RectF bufferRect;
            if (video)
                bufferRect = new RectF(0, 0, h, w);
            else
                bufferRect = new RectF(0, 0, w, h);
            Logger.d(TAG, "PreviewSize:" + w +"x"+ h);
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            if (video)
            {
                matrix.setRectToRect(bufferRect, viewRect, Matrix.ScaleToFit.FILL);
                if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
                    matrix.preRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.preRotate(orientation, centerX, centerY);
            }
            else
            {
                matrix.setRectToRect(viewRect, viewRect, Matrix.ScaleToFit.FILL);
                if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
                    matrix.postRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.postRotate(orientation, centerX, centerY);
            }

            textureView.setTransform(matrix);
            textureView.setAspectRatio(w, h);
        }


    }

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
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                        cameraBackroundValuesChangedListner, null);
            } catch (CameraAccessException | IllegalStateException e) {
                mCaptureSession =null;
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };


}
