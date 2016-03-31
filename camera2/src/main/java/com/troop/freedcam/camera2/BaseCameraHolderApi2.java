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
import android.media.CamcorderProfile;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.renderscript.RenderScript;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera2.modules.I_PreviewWrapper;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;
import com.troop.freedcam.utils.VideoUtils;

import java.io.IOException;
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
    public static String JPEG = "jpeg";
    public static String RAW_SENSOR = "raw_sensor";
    public static String RAW10 = "raw10";

    public boolean isWorking = false;
    private Context context;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    public AutoFitTextureView textureView;

    //this is needed for the previewSize...
    public CaptureRequest.Builder mPreviewRequestBuilder;
    I_Callbacks.PreviewCallback previewCallback;

    public CameraCaptureSession mCaptureSession;
    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;
    public String VideoSize;
    public I_PreviewWrapper ModulePreview;
    RenderScript mRS;
    public ViewfinderProcessor mProcessor;
    public CaptureSessionHandler CaptureSessionH;

    int afState;
    int aeState;

    boolean errorRecieved = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseCameraHolderApi2(Context context,I_CameraChangedListner cameraChangedListner, Handler UIHandler)
    {
        super(cameraChangedListner, UIHandler);
        this.context = context;
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CaptureSessionH = new CaptureSessionHandler();

    }

    //###########################  public camera methods
    //###########################
    //###########################

    @Override
    public boolean OpenCamera(int camera)
    {
        //startBackgroundThread();
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
                mRS = RenderScript.create(AppSettingsManager.APPSETTINGSMANAGER.context);
                mProcessor = new ViewfinderProcessor(mRS);
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
            mProcessor.kill();
            try
            {
                if (null != mCaptureSession)
                {
                    mCaptureSession.close();
                    mCaptureSession = null;
                }
            }
           catch (Exception e) {}

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
            Logger.exception(e);
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

    public void SetParameterToCam(CaptureRequest.Key<Integer> key, int value)
    {
        if (mCaptureSession != null)
        {
            try {

                mPreviewRequestBuilder.set(key, value);
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), mCaptureCallback,
                        null);

            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }
        else if (mPreviewRequestBuilder != null)
        {
            mPreviewRequestBuilder.set(key, value);
        }
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

            Logger.d(TAG, "Camera open");
            if (UIHandler != null)
                UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraChangedListner.onCameraOpen("");
                }
            });
            try {
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
            ((ParameterHandlerApi2)GetParameterHandler()).Init();
            //SetLastUsedParameters(mPreviewRequestBuilder);
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice)
        {
            Logger.d(TAG,"Camera Disconnected");
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice cameraDevice, final int error)
        {
            Logger.d(TAG, "Camera Error" + error);
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
            if (GetParameterHandler().ManualShutter != null && GetParameterHandler().ManualShutter.IsSupported())
            {
                if (result != null && result.getPartialResults().size() > 0)
                {
                    try
                    {
                        if (!GetParameterHandler().ExposureMode.GetValue().equals("off") && !GetParameterHandler().ControlMode.equals("off")) {
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

                            }
                            try {
                                final int  iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                GetParameterHandler().ISOManual.ThrowCurrentValueStringCHanged("" + iso);
                            }
                            catch (NullPointerException ex) {}
                            try {
                                final float  mf = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                GetParameterHandler().ManualFocus.ThrowCurrentValueStringCHanged(StringUtils.TrimmFloatString(mf + ""));
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


    public static Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
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
            Logger.e(TAG, "Couldn't find any suitable previewSize size");
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

    public static boolean IsLegacy(AppSettingsManager appSettingsManager)
    {
        boolean legacy = true;
        Semaphore mCameraOpenCloseLock = new Semaphore(1);
        try
        {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            CameraManager manager = (CameraManager) appSettingsManager.context.getSystemService(Context.CAMERA_SERVICE);
            //manager.openCamera("0", null, null);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
            if (characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) != CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                legacy = false;
            else
                legacy = true;
            manager = null;
            characteristics = null;
        }
        catch (CameraAccessException e) {
            Logger.exception(e);
        }
        catch (InterruptedException e) {
            Logger.exception(e);
        }
        catch (VerifyError ex)
        {
            Logger.exception(ex);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.exception(ex);
        }
        catch (Exception ex)
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
            surfaces = new ArrayList<Surface>();
            Display display = ((WindowManager)AppSettingsManager.APPSETTINGSMANAGER.context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            displaySize = new Point();
            display.getRealSize(displaySize);
        }

        public void AddSurface(Surface surface, boolean addtoPreviewRequestBuilder)
        {
            Logger.d(this.TAG, "AddSurface");
            surfaces.add(surface);
            if (addtoPreviewRequestBuilder)
                mPreviewRequestBuilder.addTarget(surface);
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
            Logger.d(this.TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
            } catch (CameraAccessException e) {
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
                mCaptureSession = null;
            }

        }

        public void CloseCaptureSession()
        {
            StopRepeatingCaptureSession();
            Clear();
            if (mCaptureSession != null)
                mCaptureSession.close();
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
                if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
                    matrix.preRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.preRotate(orientation, centerX, centerY);
            }
            else
            {
                matrix.setRectToRect(viewRect, viewRect, Matrix.ScaleToFit.FILL);
                if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals(StringUtils.ON))
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
                //ParameterHandler.SetAppSettingsToParameters();
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(),
                        mCaptureCallback, null);
            } catch (CameraAccessException e) {
                mCaptureSession =null;
            }
            catch (IllegalStateException ex)
            {
                mCaptureSession = null;
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession)
        {

        }
    };
}
