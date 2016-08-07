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

package freed.cam.apis.camera2;

import android.Manifest.permission;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraConstrainedHighSpeedCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.basecamera.modules.VideoMediaProfile;
import freed.cam.apis.camera2.parameters.ParameterHandler;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;
import freed.utils.StringUtils;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class CameraHolderApi2 extends CameraHolderAbstract
{
    private final String TAG = CameraHolderApi2.class.getSimpleName();
    public static String RAW_SENSOR = "raw_sensor";
    public static String RAW10 = "raw10";
    public static String RAW12 = "raw12";

    public boolean isWorking;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private AutoFitTextureView textureView;

    //this is needed for the previewSize...
    private Builder mPreviewRequestBuilder;

    private CameraCaptureSession mCaptureSession;

    private CameraConstrainedHighSpeedCaptureSession mHighSpeedCaptureSession;

    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;
    public String VideoSize;
    private VideoMediaProfile currentVideoProfile;

    public CaptureSessionHandler CaptureSessionH;

    public boolean flashRequired = false;

    int afState;
    int aeState;

    boolean errorRecieved;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public CameraHolderApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        manager = (CameraManager) cameraUiWrapper.getContext().getSystemService(Context.CAMERA_SERVICE);
        CaptureSessionH = new CaptureSessionHandler();


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
        if (VERSION.SDK_INT >= 23) {
            if (cameraUiWrapper.getContext().checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraUiWrapper.onCameraError("Error: Permission for Camera are not granted!");
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
        Logger.d(TAG, "Blacklevel:" + pattern);
        Logger.d(TAG, "Whitelevel:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL));
        Logger.d(TAG, "SensorCalibration1:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
        Logger.d(TAG, "SensorCalibration2:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
        Logger.d(TAG, "SensorColorMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
        Logger.d(TAG, "SensorColorMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
        Logger.d(TAG, "ForwardMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
        Logger.d(TAG, "ForwardMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
        Logger.d(TAG, "ExposureTImeMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
        Logger.d(TAG, "ExposureTImeMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
        Logger.d(TAG, "FrameDuration:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION));
        Logger.d(TAG, "SensorIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper());
        Logger.d(TAG, "SensorIsoMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower());
        Logger.d(TAG, "SensorAnalogIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY));
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
                        cameraUiWrapper.onCameraClose("");
                    }
                });
            Logger.d(TAG, "camera closed");
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


    public void SetSurface(TextureView surfaceHolder)
    {
        textureView = (AutoFitTextureView) surfaceHolder;
    }

    @Override
    public void StartPreview()
    {
        //unused modules must handel preview start
    }
    @Override
    public void StopPreview()
    {
        //unused modules must handel preview stop
    }

    @Override
    public void StartFocus(FocusEvents autoFocusCallback) {

    }

    @Override
    public void CancelFocus() {

    }

    public <T> void SetParameterRepeating(@NonNull Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartRepeatingCaptureSession();
    }

    public <T> void SetParameter(@NonNull Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartCaptureSession();
    }

    public <T> void SetFocusArea(@NonNull Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Logger.d(TAG, "Set :" + key.getName() + " to " + value);
        SetParameterRepeating(key,value);
        SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
    }

    public <T> T get(Key<T> key)
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
    public void StartFocus() {
        cameraUiWrapper.getFocusHandler().StartFocus();
    }

    @Override
    public void ResetPreviewCallback() {

    }

    public Parameters GetCameraParameters() {
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


    public Builder createCaptureRequest() throws CameraAccessException {
        CameraDevice device = mCameraDevice;
        if (device == null) {
            throw new IllegalStateException("Can't get requests when no camera is open");
        }
        return device.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
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
                    cameraUiWrapper.onCameraOpen("");
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
            if (UIHandler != null)
                UIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        cameraUiWrapper.onCameraClose("");
                    }
                });
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
                    cameraUiWrapper.onCameraError("Error:" + error);
                    cameraUiWrapper.onCameraClose("");
                }
            });

        }
    };

    public CaptureCallback cameraBackroundValuesChangedListner = new CaptureCallback()
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
            if (cameraUiWrapper.GetParameterHandler().ManualShutter != null && cameraUiWrapper.GetParameterHandler().ManualShutter.IsSupported())
            {
                if (result != null && result.getPartialResults().size() > 0)
                {
                    try
                    {
                        if (!cameraUiWrapper.GetParameterHandler().ExposureMode.GetValue().equals("off") && !cameraUiWrapper.GetParameterHandler().ControlMode.equals("off"))
                        {
                            try {
                                long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                if(expores != 0) {
                                    cameraUiWrapper.GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged(getShutterString(expores));
                                }
                                else
                                    cameraUiWrapper.GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged("1/60");
                            }
                            catch (Exception e)
                            {
                                Logger.exception(e);
                            }
                            try {
                                int  iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                cameraUiWrapper.GetParameterHandler().ManualIso.ThrowCurrentValueStringCHanged("" + iso);
                            }
                            catch (NullPointerException ex) {
                                Logger.exception(ex);
                            }
                            try {
                                float  mf = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                cameraUiWrapper.GetParameterHandler().ManualFocus.ThrowCurrentValueStringCHanged(StringUtils.TrimmFloatString4Places(mf + ""));
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
                        SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                        break;
                    case 3:
                        state="ACTIVE_SCAN";
                        break;
                    case 4:
                        state = "FOCUSED_LOCKED";
                        SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER,CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
                        if (cameraUiWrapper.getFocusHandler().focusEvent != null)
                            cameraUiWrapper.getFocusHandler().focusEvent.FocusFinished(true);

                        break;
                    case 5:
                        state = "NOT_FOCUSED_LOCKED";
                        if (cameraUiWrapper.getFocusHandler().focusEvent != null)
                            cameraUiWrapper.getFocusHandler().focusEvent.FocusFinished(false);
                        break;
                    case 6:
                        state ="PASSIVE_UNFOCUSED";
                        SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                        break;
                }
                Logger.d(TAG, "new AF_STATE :"+state);
            }
            if(result.get(CaptureResult.CONTROL_AE_STATE) != null && aeState != result.get(CaptureResult.CONTROL_AE_STATE))
            {

                aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                flashRequired = false;
                switch (aeState)
                {
                    case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                        //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                        Logger.d(TAG, "AESTATE: Converged");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                        flashRequired = true;
                        //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
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
            return 1 + "/" + 10000000 / mili;
        else {
            float t = mili / 10000;
            return String.format("%01.1f", t);
        }
    }


    public Size getSizeForPreviewDependingOnImageSize(Size[] choices, CameraCharacteristics characteristics, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Rect rect = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= 1280 && s.getHeight() <= 720 && (double)s.getWidth()/s.getHeight() == ratio)
                sizes.add(s);

        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CompareSizesByArea());
        } else {
            Logger.e(TAG, "Couldn't find any suitable previewSize size");
            return choices[0];
        }
    }

    public static boolean IsLegacy(Context context)
    {
        boolean legacy = true;
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            CameraCharacteristics characteristics = manager.getCameraCharacteristics("0");
            legacy = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY;
        }
        catch (Throwable ex) {
            Logger.exception(ex);
        }
        return legacy;
    }

    public class CaptureSessionHandler
    {
        private final String TAG = CaptureSessionHandler.class.getSimpleName();
        private final List<Surface> surfaces;
        private final Point displaySize;
        public CaptureSessionHandler()
        {
            surfaces = new ArrayList<>();
            Display display = ((WindowManager) cameraUiWrapper.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            displaySize = new Point();
            display.getRealSize(displaySize);
        }

        public void SetCaptureSession(CameraCaptureSession cameraCaptureSession)
        {
            mCaptureSession = cameraCaptureSession;
        }

        public void SetHighSpeedCaptureSession(CameraCaptureSession cameraCaptureSession)
        {
            mHighSpeedCaptureSession = (CameraConstrainedHighSpeedCaptureSession)cameraCaptureSession;
        }


        public void CreatePreviewRequestBuilder()
        {
            try {
                mPreviewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                ((ParameterHandler) cameraUiWrapper.GetParameterHandler()).Init();
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
            Logger.d(TAG, "AddSurface");
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
            Logger.d(TAG, "RemoveSurface");
            if (surfaces.contains(surface))
                surfaces.remove(surface);
            mPreviewRequestBuilder.removeTarget(surface);

        }

        public void Clear()
        {
            Logger.d(TAG, "Clear");
            if (mPreviewRequestBuilder != null)
                for (Surface s: surfaces)
                    mPreviewRequestBuilder.removeTarget(s);
            surfaces.clear();

        }

        public void CreateCaptureSession()
        {
            if(mCameraDevice == null)
                return;
            Logger.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
            } catch (CameraAccessException | SecurityException e) {
                Logger.exception(e);
            }
        }

        @TargetApi(VERSION_CODES.M)
        public void CreateHighSpeedCaptureSession()
        {
            if(mCameraDevice == null)
                return;
            Logger.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, previewStateCallBackRestart, null);
            } catch (CameraAccessException | SecurityException e) {
                Logger.exception(e);
            }
        }

        public void CreateCaptureSession(StateCallback customCallback)
        {
            Logger.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, customCallback, null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StopRepeatingCaptureSession()
        {
            Logger.d(TAG, "StopRepeatingCaptureSession");
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
            Logger.d(TAG, "StartRepeatingCaptureSession");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StartRepeatingCaptureSession(@Nullable CaptureCallback listener)
        {
            Logger.d(TAG, "StartRepeatingCaptureSession with Custom CaptureCallback");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), listener,
                        null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StopHighspeedCaptureSession()
        {
            if (mHighSpeedCaptureSession != null)
                try {
                    mHighSpeedCaptureSession.stopRepeating();
                } catch (CameraAccessException e) {
                    Logger.exception(e);
                }
                catch (IllegalStateException ex)
                {
                    Logger.exception(ex);
                    mHighSpeedCaptureSession = null;
                }
        }

        @TargetApi(VERSION_CODES.M)
        public void StartHighspeedCaptureSession()
        {
            if (mHighSpeedCaptureSession == null)
                return;
            try {
                List<CaptureRequest> capList = mHighSpeedCaptureSession.createHighSpeedRequestList(mPreviewRequestBuilder.build());

                mHighSpeedCaptureSession.setRepeatingBurst(capList, new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                        super.onCaptureCompleted(session, request, result);

                        Logger.d("Completed", "fps:" + result.getFrameNumber());
                    }
                }, null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StartCaptureSession()
        {
            Logger.d(TAG, "StartCaptureSession");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException e) {
               Logger.exception(e);
            }
        }

        public void StartCaptureSession(@Nullable CaptureCallback listener)
        {
            Logger.d(TAG, "StartCaptureSession with Custom CaptureCallback");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.capture(mPreviewRequestBuilder.build(), listener,
                        null);
            } catch (CameraAccessException e) {
                Logger.exception(e);
            }
        }

        public void StartCapture(@NonNull Builder request,
                                 @Nullable CaptureCallback listener)
        {
            try {
                mCaptureSession.capture(request.build(),listener,null);
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
                matrix.setRectToRect(bufferRect, viewRect, ScaleToFit.FILL);
                if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
                    matrix.preRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.preRotate(orientation, centerX, centerY);
            }
            else
            {
                matrix.setRectToRect(viewRect, viewRect, ScaleToFit.FILL);
                if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals(KEYS.ON))
                    matrix.postRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.postRotate(orientation, centerX, centerY);
            }

            textureView.setTransform(matrix);
            textureView.setAspectRatio(w, h);
        }


    }

    StateCallback previewStateCallBackRestart = new StateCallback()
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
