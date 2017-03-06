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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.utils.AppSettingsManager;
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

    public interface AeCompensationListner
    {
        void onAeCompensationChanged(int aecompensation);
    }

    public boolean isWorking;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    private final Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private AutoFitTextureView textureView;
    private Builder mPreviewRequestBuilder;
    private CameraCaptureSession mCaptureSession;
    private CameraConstrainedHighSpeedCaptureSession mHighSpeedCaptureSession;
    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;
    public String VideoSize;
    public CaptureSessionHandler CaptureSessionH;
    public boolean flashRequired = false;
    int afState;
    int aeState;

    private Pair<Float,Float> focusRanges;
    private float focus_distance;

    boolean errorRecieved;

    public void SetAeCompensationListner(AeCompensationListner aeCompensationListner)
    {
        this.aeCompensationListner = aeCompensationListner;
    }
    private AeCompensationListner aeCompensationListner;



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
        Log.d(TAG, "Open Camera");
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

        } catch (CameraAccessException ex) {
            ex.printStackTrace();
            return  false;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }


    private void printCharacteristics()
    {
        BlackLevelPattern pattern = characteristics.get(CameraCharacteristics.SENSOR_BLACK_LEVEL_PATTERN);
        Log.d(TAG, "Blacklevel:" + pattern);
        Log.d(TAG, "Whitelevel:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_WHITE_LEVEL));
        Log.d(TAG, "SensorCalibration1:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM1));
        Log.d(TAG, "SensorCalibration2:" + characteristics.get(CameraCharacteristics.SENSOR_CALIBRATION_TRANSFORM2));
        Log.d(TAG, "SensorColorMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM1));
        Log.d(TAG, "SensorColorMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_COLOR_TRANSFORM2));
        Log.d(TAG, "ForwardMatrix1:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX1));
        Log.d(TAG, "ForwardMatrix2:" + characteristics.get(CameraCharacteristics.SENSOR_FORWARD_MATRIX2));
        Log.d(TAG, "ExposureTImeMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
        Log.d(TAG, "ExposureTImeMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
        Log.d(TAG, "FrameDuration:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_MAX_FRAME_DURATION));
        Log.d(TAG, "SensorIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper());
        Log.d(TAG, "SensorIsoMin:" + characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getLower());
        Log.d(TAG, "SensorAnalogIsoMax:" + characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY));
    }

    @Override
    public void CloseCamera()
    {
        try {
            Log.d(TAG,"Close Camera");
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
           catch (Exception ex)
           {
               ex.printStackTrace();
           }

            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
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
            Log.d(TAG, "camera closed");
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
        } catch (CameraAccessException ex) {
            ex.printStackTrace();
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

    public float[] GetFocusRange()
    {
        float ar[] = new float[3];
        if (focusRanges != null)
        {
            ar[0] = 1/focusRanges.first.floatValue();
            ar[2] = 1/focusRanges.second.floatValue();
            ar[1] = 1/focus_distance;
        }
        return ar;
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
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartRepeatingCaptureSession();
    }


    public <T> void SetParameterRepeating(@NonNull Key<T> key, T value, CaptureCallback captureCallback)
    {
        if (mPreviewRequestBuilder == null )
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        CaptureSessionH.StartRepeatingCaptureSession(captureCallback);
    }

    public <T> void SetParameter(@NonNull Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null|| mCaptureSession == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        mPreviewRequestBuilder.set(key,value);
        try {
            mCaptureSession.capture(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                    null);
        } catch (CameraAccessException ex) {
            ex.printStackTrace();
        }
    }

    public void StartAePrecapture(CaptureCallback listener)
    {
        if (mPreviewRequestBuilder == null)
            return;

        Log.d(TAG,"Start AE Precapture");
        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        try {
            mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), listener,
                    null);
        } catch (CameraAccessException ex) {
            ex.printStackTrace();
        }
    }

    public <T> void SetFocusArea(@NonNull Key<T> key, T value)
    {
        if (mPreviewRequestBuilder == null)
            return;
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        SetParameter(key,value);
        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
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


    public Builder createCaptureRequestStillCapture() throws CameraAccessException {
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

            Log.d(TAG, "Camera open");
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
            Log.d(TAG,"Camera Disconnected");
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
            Log.d(TAG, "Camera Error" + error);
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
        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
        }

        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
        {
            if (result == null || result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME) == null)
                return;
            if (cameraUiWrapper.GetParameterHandler().ManualShutter != null && cameraUiWrapper.GetParameterHandler().ManualShutter.IsSupported())
            {
                if (result != null && result.getKeys().size() > 0)
                {
                    try
                    {
                        if (!cameraUiWrapper.GetParameterHandler().ExposureMode.GetValue().equals(cameraUiWrapper.getContext().getString(R.string.off)) && !cameraUiWrapper.GetParameterHandler().ControlMode.equals(cameraUiWrapper.getContext().getString(R.string.off)))
                        {
                            try {
                                long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                if(expores != 0) {
                                    cameraUiWrapper.GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged(getShutterString(expores));
                                }
                                else
                                    cameraUiWrapper.GetParameterHandler().ManualShutter.ThrowCurrentValueStringCHanged("1/60");

                                Log.v(TAG, "ExposureTime: " + result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME));
                            }
                            catch (Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            try {
                                int  iso = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                mPreviewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, iso);
                                cameraUiWrapper.GetParameterHandler().ManualIso.ThrowCurrentValueStringCHanged("" + iso);
                                Log.v(TAG, "Iso: " + result.get(TotalCaptureResult.SENSOR_SENSITIVITY));
                            }
                            catch (NullPointerException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                focus_distance = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                cameraUiWrapper.GetParameterHandler().ManualFocus.ThrowCurrentValueStringCHanged(StringUtils.TrimmFloatString4Places(focus_distance + ""));
                            }
                            catch (NullPointerException ex) {ex.printStackTrace();}
                        }
                    }
                    catch (NullPointerException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }

            if (result.get(CaptureResult.LENS_FOCUS_RANGE) != null)
                focusRanges = result.get(CaptureResult.LENS_FOCUS_RANGE);
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
                        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                        break;
                    case 3:
                        state="ACTIVE_SCAN";
                        break;
                    case 4:
                        state = "FOCUSED_LOCKED";
                        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
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
                        SetParameter(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
                        break;
                }
                Log.d(TAG, "new AF_STATE :"+state);
            }
            if(result.get(CaptureResult.CONTROL_AE_STATE) != null && aeState != result.get(CaptureResult.CONTROL_AE_STATE))
            {

                aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                flashRequired = false;
                switch (aeState)
                {
                    case CaptureResult.CONTROL_AE_STATE_CONVERGED:
                        //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                        Log.v(TAG, "AESTATE: Converged");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED:
                        flashRequired = true;
                        //SetParameter(CaptureRequest.CONTROL_AE_LOCK, true);
                        Log.v(TAG, "AESTATE: FLASH_REQUIRED");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_INACTIVE:
                        Log.v(TAG, "AESTATE: INACTIVE");

                        break;
                    case CaptureResult.CONTROL_AE_STATE_LOCKED:
                        Log.v(TAG, "AESTATE: LOCKED");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_PRECAPTURE:
                        Log.v(TAG, "AESTATE: PRECAPTURE");
                        break;
                    case CaptureResult.CONTROL_AE_STATE_SEARCHING:
                        Log.v(TAG, "AESTATE: SEARCHING");
                        break;
                }
            }
            if (result.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION)!= null && aeCompensationListner != null)
            {
                aeCompensationListner.onAeCompensationChanged(result.get(CaptureResult.CONTROL_AE_EXPOSURE_COMPENSATION));
                //Log.d(TAG,"ExpoCompensation:" + );
            }

            if (cameraUiWrapper.GetParameterHandler().ExposureLock != null)
                cameraUiWrapper.GetParameterHandler().ExposureLock.onValueHasChanged(result.get(CaptureResult.CONTROL_AE_LOCK).toString());
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            super.onCaptureProgressed(session, request, partialResult);
        }


    };

    private String getShutterString(long val)
    {
        int mili = (int) val / 1000000;
        //double sec =  mili / 1000;
        if (mili < 1000)
            return 1 + "/" +mili;
        else {
            float t = mili / 1000;
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
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            return choices[0];
        }
    }

    public class CaptureSessionHandler
    {
        private final String TAG = CaptureSessionHandler.class.getSimpleName();
        private final List<Surface> surfaces;
        public final Point displaySize;
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
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
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
            Log.d(TAG, "AddSurface");
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
            Log.d(TAG, "RemoveSurface");
            if (surfaces.contains(surface))
                surfaces.remove(surface);
            mPreviewRequestBuilder.removeTarget(surface);

        }

        public void Clear()
        {
            Log.d(TAG, "Clear");
            if (mPreviewRequestBuilder != null)
                for (Surface s: surfaces)
                    mPreviewRequestBuilder.removeTarget(s);
            surfaces.clear();

        }

        public void CreateCaptureSession()
        {
            if(mCameraDevice == null)
                return;
            Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, previewStateCallBackRestart, null);
            } catch (CameraAccessException | SecurityException ex) {
                ex.printStackTrace();
            }
        }

        @TargetApi(VERSION_CODES.M)
        public void CreateHighSpeedCaptureSession(StateCallback customCallback)
        {
            if(mCameraDevice == null)
                return;
            Log.d(TAG, "CreateCaptureSession: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createConstrainedHighSpeedCaptureSession(surfaces, customCallback, null);
            } catch (CameraAccessException | SecurityException ex) {
                ex.printStackTrace();
            }
        }

        public void CreateCaptureSession(StateCallback customCallback)
        {
            Log.d(TAG, "CreateCaptureSessionWITHCustomCallback: Surfaces Count:" + surfaces.size());
            try {
                mCameraDevice.createCaptureSession(surfaces, customCallback, null);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void StopRepeatingCaptureSession()
        {
            Log.d(TAG, "StopRepeatingCaptureSession");
            if (mCaptureSession != null)
            try {
                mCaptureSession.stopRepeating();
            } catch (CameraAccessException | java.lang.SecurityException ex) {
                ex.printStackTrace();
                mCaptureSession = null;
            }
            catch (IllegalStateException ex)
            {
                ex.printStackTrace();
                mCaptureSession = null;
            }
        }

        public void CancelRepeatingCaptureSession()
        {
            Log.d(TAG, "StopRepeatingCaptureSession");
            if (mCaptureSession != null)
                try {
                    mCaptureSession.abortCaptures();
                } catch (CameraAccessException | java.lang.SecurityException ex) {
                    ex.printStackTrace();
                    mCaptureSession = null;
                }
                catch (IllegalStateException ex)
                {
                    ex.printStackTrace();
                    mCaptureSession = null;
                }
        }

        public void StartRepeatingCaptureSession()
        {
            Log.d(TAG, "StartRepeatingCaptureSession");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void StartRepeatingCaptureSession(CaptureCallback listener)
        {
            Log.d(TAG, "StartRepeatingCaptureSession with Custom CaptureCallback");
            if (mCaptureSession == null)
                return;
            try {
                mCaptureSession.setRepeatingRequest(mPreviewRequestBuilder.build(), listener,
                        null);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void StopHighspeedCaptureSession()
        {
            if (mHighSpeedCaptureSession != null)
                try {
                    mHighSpeedCaptureSession.stopRepeating();
                } catch (CameraAccessException ex) {
                    ex.printStackTrace();
                }
                catch (IllegalStateException ex)
                {
                    ex.printStackTrace();
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

                        Log.d("Completed", "fps:" + result.getFrameNumber());
                    }
                }, null);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void StartImageCapture(@NonNull Builder request,
                                      @Nullable CaptureCallback listener, Handler handler)
        {
            //StopRepeatingCaptureSession();
            CancelRepeatingCaptureSession();
            try {
                mCaptureSession.capture(request.build(),listener,handler);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void cancelCapture()
        {
            try {
                mCaptureSession.abortCaptures();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public void StartCaptureBurst(@NonNull List<CaptureRequest>  request,
                                 @Nullable CaptureCallback listener, Handler handler)
        {
            try {
                mCaptureSession.captureBurst(request,listener,handler);
            } catch (CameraAccessException ex) {
                ex.printStackTrace();
            }
        }

        public void CloseCaptureSession()
        {
            CancelRepeatingCaptureSession();
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
            Log.d(TAG,"DisplaySize:" + displaySize.x +"x"+ displaySize.y);
            RectF bufferRect;
            if (video)
                bufferRect = new RectF(0, 0, h, w);
            else
                bufferRect = new RectF(0, 0, w, h);
            Log.d(TAG, "PreviewSize:" + w +"x"+ h);
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            if (video)
            {
               // if(bufferRect.width() <= viewRect.width())
                    matrix.setRectToRect(bufferRect, viewRect, ScaleToFit.FILL);

                if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(cameraUiWrapper.getResString(R.string.on_)))
                    matrix.preRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.preRotate(orientation, centerX, centerY);
            }
            else
            {
                matrix.setRectToRect(viewRect, viewRect, ScaleToFit.FILL);
                if (appSettingsManager.getApiString(AppSettingsManager.SETTING_OrientationHack).equals(cameraUiWrapper.getResString(R.string.on_)))
                    matrix.postRotate(orientationWithHack, centerX, centerY);
                else
                    matrix.postRotate(orientation, centerX, centerY);
            }

            textureView.setTransform(matrix);
            textureView.setAspectRatio((int)viewRect.width(), (int)viewRect.height());
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
