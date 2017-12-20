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
import android.graphics.Point;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Key;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.util.Size;
import android.view.TextureView;

import com.troop.freedcam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StringUtils;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class CameraHolderApi2 extends CameraHolderAbstract
{
    private final String TAG = CameraHolderApi2.class.getSimpleName();

    //limits the preview to use maximal that size for preview
    //when set to high it its possbile to get a laggy preview with active focuspeak
    public static int MAX_PREVIEW_WIDTH = 1920;
    public static int MAX_PREVIEW_HEIGHT = 1080;

    public interface AeCompensationListner
    {
        void onAeCompensationChanged(int aecompensation);
    }

    public interface WaitForFirstFrameCallback
    {
        void onFirstFrame();
    }

    public boolean isWorking;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    public AutoFitTextureView textureView;

    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;
    public CaptureSessionHandler captureSessionHandler;
    public boolean flashRequired = false;
    int afState;
    int aeState;
    public long currentExposureTime;
    public int currentIso;
    private Pair<Float,Float> focusRanges;
    private float focus_distance;

    private boolean waitForFirstFrame = false;
    private WaitForFirstFrameCallback waitForFirstFrameCallback;

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

     }


     public void setWaitForFirstFrame()
     {
         waitForFirstFrame = true;
     }

     public void setWaitForFirstFrameCallback(WaitForFirstFrameCallback callback)
     {
         this.waitForFirstFrameCallback = callback;
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
            characteristics = manager.getCameraCharacteristics(CurrentCamera + "");
            /*if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }*/
            manager.openCamera(cam, mStateCallback, null);

            List<CameraCharacteristics.Key<?>> keys = characteristics.getKeys();
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        } catch (CameraAccessException ex) {
            Log.WriteEx(ex);
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
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
//            mCameraOpenCloseLock.acquire();
            captureSessionHandler.Clear();

            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        catch (Exception ex) {
            Log.WriteEx(ex);
            //throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        }
        finally
        {
//            mCameraOpenCloseLock.release();
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
            Log.WriteEx(ex);
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

    public void StartAePrecapture(CaptureCallback listener)
    {
        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START,listener);
    }

    public <T> void SetFocusArea(@NonNull Key<T> key, T value)
    {
        captureSessionHandler.SetParameter(key,null);
        /*captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);*/
        Log.d(TAG, "Set :" + key.getName() + " to " + value);
        captureSessionHandler.SetParameter(key,value);
        captureSessionHandler.SetParameter(CaptureRequest.CONTROL_AF_TRIGGER,CameraMetadata.CONTROL_AF_TRIGGER_START);
    }


    @Override
    public void SetLocation(Location loc)
    {

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

    //###########################  CALLBACKS
    //###########################
    //###########################

    CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera previewSize here.
//            mCameraOpenCloseLock.release();
            CameraHolderApi2.this.mCameraDevice = cameraDevice;

            Log.d(TAG, "Camera open");
            if (UIHandler != null)
                UIHandler.post(new Runnable() {
                @Override
                public void run() {
                    cameraUiWrapper.onCameraOpen("");
                }
            });

            captureSessionHandler.CreatePreviewRequestBuilder();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice)
        {
            Log.d(TAG,"Camera Disconnected");
//            mCameraOpenCloseLock.release();
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
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
//            mCameraOpenCloseLock.release();
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;

            }
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
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result)
        {
            if (result == null)
                return;
            if (waitForFirstFrame)
            {
                if (waitForFirstFrameCallback != null)
                    waitForFirstFrameCallback.onFirstFrame();
                waitForFirstFrame = false;
            }

            ParameterInterface expotime = cameraUiWrapper.getParameterHandler().get(Settings.M_ExposureTime);
            ParameterInterface iso = cameraUiWrapper.getParameterHandler().get(Settings.M_ManualIso);
            if (SettingsManager.get(Settings.useHuaweiCamera2Extension).getBoolean())
            {
                if (expotime.GetValue() == 0) {
                    Long expoTime = result.get(CaptureResult.SENSOR_EXPOSURE_TIME);
                    if (expoTime != null) {
                        currentExposureTime = expoTime;
                        expotime.fireStringValueChanged(getShutterStringNS(expoTime));
                    }
                }
                if (iso.GetValue() == 0)
                {
                    Integer isova = result.get(CaptureResult.SENSOR_SENSITIVITY);
                    if(isova != null) {
                        currentIso = isova;
                        iso.fireStringValueChanged(String.valueOf(isova));
                    }
                }
            }
            else {
                if (expotime != null && expotime.IsSupported()) {
                    if (result != null && result.getKeys().size() > 0) {
                        try {
                            if (!cameraUiWrapper.getParameterHandler().get(Settings.ExposureMode).GetStringValue().equals(cameraUiWrapper.getContext().getString(R.string.off))
                                    && !cameraUiWrapper.getParameterHandler().get(Settings.ControlMode).equals(cameraUiWrapper.getContext().getString(R.string.off))) {
                                try {
                                    long expores = result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME);
                                    currentExposureTime = expores;
                                    if (expores != 0) {
                                        expotime.fireStringValueChanged(getShutterStringNS(expores));
                                    } else
                                        expotime.fireStringValueChanged("1/60");

                                    //Log.v(TAG, "ExposureTime: " + result.get(TotalCaptureResult.SENSOR_EXPOSURE_TIME));
                                } catch (Exception ex) {
                                    Log.WriteEx(ex);
                                }
                                try {
                                    int isova = result.get(TotalCaptureResult.SENSOR_SENSITIVITY);
                                    currentIso = isova;
                                    iso.fireStringValueChanged("" + isova);
                                    //Log.v(TAG, "Iso: " + result.get(TotalCaptureResult.SENSOR_SENSITIVITY));
                                } catch (NullPointerException ex) {
                                    Log.WriteEx(ex);
                                }
                                try {
                                    focus_distance = result.get(TotalCaptureResult.LENS_FOCUS_DISTANCE);
                                    cameraUiWrapper.getParameterHandler().get(Settings.M_Focus).fireStringValueChanged(StringUtils.getMeterString(1 / focus_distance));
                                } catch (NullPointerException ex) {
                                    Log.WriteEx(ex);
                                }
                            }
                        } catch (NullPointerException ex) {
                            Log.WriteEx(ex);
                        }
                    }
                }
            }

            /*if (result.get(CaptureResult.TONEMAP_CURVE)!=null)
            {

                TonemapCurve curve = result.get(CaptureResult.TONEMAP_CURVE);
                Log.d(TAG,"Curve:" +curve.toString());
                Log.d(TAG,"Red count"+curve.getPointCount(0));
                Log.d(TAG,"Green count"+curve.getPointCount(1));
                Log.d(TAG,"Blue count"+curve.getPointCount(2));
            }
*/
            if (result.get(CaptureResult.LENS_FOCUS_RANGE) != null)
                focusRanges = result.get(CaptureResult.LENS_FOCUS_RANGE);

            //handel focus callback to ui if it was sucessfull. dont reset focusareas or trigger again afstate.
            //else it could happen that it refocus
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
                        break;
                    case 3:
                        state="ACTIVE_SCAN";
                        break;
                    case 4:
                        state = "FOCUSED_LOCKED";
                        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE,true);
                        if (cameraUiWrapper.getFocusHandler().focusEvent != null)
                            cameraUiWrapper.getFocusHandler().focusEvent.FocusFinished(true);
                        break;
                    case 5:
                        state = "NOT_FOCUSED_LOCKED";
                        captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE,true);
                        if (cameraUiWrapper.getFocusHandler().focusEvent != null)
                            cameraUiWrapper.getFocusHandler().focusEvent.FocusFinished(false);
                        break;
                    case 6:
                        state ="PASSIVE_UNFOCUSED";
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

            if (cameraUiWrapper.getParameterHandler().get(Settings.ExposureLock) != null && result.get(CaptureResult.CONTROL_AE_LOCK) != null) {
                String expolock = result.get(CaptureResult.CONTROL_AE_LOCK).toString();
                if (expolock != null)
                    cameraUiWrapper.getParameterHandler().get(Settings.ExposureLock).fireStringValueChanged(expolock);
            }
        }

    };

    private String getShutterStringNS(long val)
    {
        if (val > 1000000000) {
            return "" + val / 1000000000;
        }
        int i = (int)(0.5D + 1.0E9F / val);
        return "1/" + Integer.toString(i);
    }


    public Size getSizeForPreviewDependingOnImageSize(int imageformat, int mImageWidth, int mImageHeight)
    {
        List<Size> sizes = new ArrayList<>();
        Size[] choices = map.getOutputSizes(imageformat);
        Point displaysize = captureSessionHandler.getDisplaySize();
        double ratio = (double)mImageWidth/mImageHeight;
        for (Size s : choices)
        {
            if (s.getWidth() <= MAX_PREVIEW_WIDTH && s.getHeight() <= MAX_PREVIEW_HEIGHT && ratioMatch((double)s.getWidth()/s.getHeight(),ratio))
                sizes.add(s);
        }
        if (sizes.size() > 0) {
            return Collections.max(sizes, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable previewSize size");
            Size s = choices[0];
            if (s.getWidth() > displaysize.x && s.getHeight() > displaysize.y)
                return new Size(displaysize.x, displaysize.y);
            return choices[0];
        }
    }

    private boolean ratioMatch(double preview, double image)
    {
        double rangelimter = 0.1;

        if (preview+rangelimter >= image && preview-rangelimter <= image)
            return true;
        else
            return false;
    }


}
