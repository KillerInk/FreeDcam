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

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.BlackLevelPattern;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.os.Build.VERSION_CODES;
import android.util.Size;
import android.view.TextureView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.events.CameraStateEvents;
import freed.utils.Log;
import freed.views.AutoFitTextureView;

/**
 * Created by troop on 07.12.2014.
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class CameraHolderApi2 extends CameraHolderAbstract
{
    private final String TAG = CameraHolderApi2.class.getSimpleName();


    public boolean isWorking;

    public CameraManager manager;
    public CameraDevice mCameraDevice;
    public AutoFitTextureView textureView;

    public StreamConfigurationMap map;
    public int CurrentCamera;
    public CameraCharacteristics characteristics;

    boolean errorRecieved;

    private Method method_setVendorStreamConfigMode = null;

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public CameraHolderApi2(CameraWrapperInterface cameraUiWrapper)
    {
        super(cameraUiWrapper);
        manager = (CameraManager) FreedApplication.getContext().getSystemService(Context.CAMERA_SERVICE);
        checkSetOpMode();
     }


     private void checkSetOpMode()
     {
         try {
             if (method_setVendorStreamConfigMode == null) {
                 method_setVendorStreamConfigMode = CameraDevice.class.getDeclaredMethod(
                         "setVendorStreamConfigMode", int.class);
             }
         }
         catch (NoSuchMethodException ex)
         {
             Log.w(TAG, "setOpModeForVideoStream method is not existing");
         }
         catch (Exception exception) {
             Log.w(TAG, "setOpModeForVideoStream method is not existing");
         }
     }

     public void setOpModeForHFRVideoStreamToActiveCamera(int hfrResIndex)
     {
         if (method_setVendorStreamConfigMode != null) {
             try {
                 method_setVendorStreamConfigMode.invoke(mCameraDevice, hfrResIndex);
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             } catch (InvocationTargetException e) {
                 e.printStackTrace();
             }
         }
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
        try
        {
            characteristics = manager.getCameraCharacteristics(CurrentCamera + "");
            manager.openCamera(cam, mStateCallback, null);

            List<CameraCharacteristics.Key<?>> keys = characteristics.getKeys();
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

        } catch (CameraAccessException | IllegalArgumentException ex ) {
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

            if (null != mCameraDevice)
            {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
        catch (Exception ex) {
            Log.WriteEx(ex);
        }
        finally
        {
//            mCameraOpenCloseLock.release();
            CameraStateEvents.fireCameraCloseEvent();
            Log.d(TAG, "camera closed");
        }
    }

    public void SetSurface(TextureView surfaceHolder)
    {
        textureView = (AutoFitTextureView) surfaceHolder;
    }

    @Override
    public void StartFocus(FocusEvents autoFocusCallback) {

    }

    @Override
    public void CancelFocus() {

    }




    @Override
    public void SetLocation(Location loc)
    {

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
        public void onOpened( CameraDevice cameraDevice) {
            CameraHolderApi2.this.mCameraDevice = cameraDevice;

            Log.d(TAG, "Camera open");
            CameraStateEvents.fireCameraOpenEvent();
        }

        @Override
        public void onDisconnected( CameraDevice cameraDevice)
        {
            Log.d(TAG,"Camera Disconnected");
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            CameraStateEvents.fireCameraCloseEvent();
        }

        @Override
        public void onError(CameraDevice cameraDevice, final int error)
        {
            Log.d(TAG, "Camera Error" + error);
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;

            }
            errorRecieved = true;
            CameraStateEvents.fireCameraCloseEvent();

        }
    };






}
