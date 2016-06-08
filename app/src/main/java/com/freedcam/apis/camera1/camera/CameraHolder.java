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

package com.freedcam.apis.camera1.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_error;
import com.freedcam.apis.basecamera.camera.modules.CameraFocusEvent;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by troop on 15.08.2014.
 */
public class CameraHolder extends AbstractCameraHolder
{
    //frame count that get attached to the camera when using focuspeak
    final int BUFFERCOUNT = 3;
    //camera object
    protected Camera mCamera;

    private final String TAG = CameraHolder.class.getSimpleName();
    public I_error errorHandler;
    private I_Callbacks.PictureCallback pictureCallback;
    private I_Callbacks.PictureCallback rawCallback;
    private I_Callbacks.ShutterCallback shutterCallback;
    private Surface previewSurfaceHolder;

    public Frameworks DeviceFrameWork = Frameworks.Normal;
    public Location gpsLocation;
    public int Orientation;

    public int CurrentCamera;

    public enum Frameworks
    {
        Normal,
        LG,
        MTK,
        MotoX
    }

    public CameraHolder(I_CameraChangedListner cameraChangedListner, AppSettingsManager appSettingsManager, Frameworks frameworks)
    {
        super(cameraChangedListner,appSettingsManager);
        DeviceFrameWork = frameworks;
    }

    public String GetParamsDirect(String para)
    {
        Parameters p = mCamera.getParameters();
        return p.get(para);
    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(int camera)
    {
        try
        {
            mCamera = Camera.open(camera);
            isRdy = true;
            cameraChangedListner.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            Logger.exception(ex);
        }
        currentState = CameraStates.open;
        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        if (currentState == CameraStates.closed)
            return;
        Logger.d(TAG, "Try to close Camera");
        if (mCamera != null)
        {
            try
            {
                mCamera.release();
            }
            catch (Exception ex)
            {
                Logger.exception(ex);
            }
            finally {
                mCamera = null;
                isRdy = false;
                Logger.d(TAG, "Camera closed");
            }
        }
        isRdy = false;
        cameraChangedListner.onCameraClose("");
        super.CloseCamera();
    }



    @Override
    public int CameraCout() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public boolean IsRdy() {
        return isRdy;
    }

    public void SetCameraParameters(Parameters parameters)
    {
        mCamera.setParameters(parameters);
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder)
    {
        previewSurfaceHolder = surfaceHolder.getSurface();
        try
        {
            if (isRdy && mCamera != null) {
                mCamera.setPreviewDisplay(surfaceHolder);
                return true;
            }
        } catch (IOException ex) {
            Logger.exception(ex);
            return false;
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
            return false;
        }
        return false;
    }


    public Surface getSurfaceHolder()
    {
        return previewSurfaceHolder;
    }

    @Override
    public void StartPreview()
    {
        if (mCamera == null)
        {
            SendUIMessage("Failed to Start Preview, Camera is null");
            return;
        }
        try
        {
            mCamera.startPreview();
            isPreviewRunning = true;
            Logger.d(TAG, "PreviewStarted");
            cameraChangedListner.onPreviewOpen("");

        } catch (Exception ex) {
            Logger.exception(ex);
            SendUIMessage("Failed to Start Preview");
        }
    }

    @Override
    public void StopPreview()
    {
        if (currentState == CameraStates.closed)
            return;
        if (mCamera == null)
            return;
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();

            isPreviewRunning = false;
            Logger.d(TAG, "Preview Stopped");
            cameraChangedListner.onPreviewClose("");

        } catch (Exception ex)
        {
            cameraChangedListner.onPreviewClose("");
            isPreviewRunning = false;
            Logger.d(TAG, "Camera was released");
            Logger.exception(ex);
        }
    }

    public Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }

    public void TakePicture(I_Callbacks.PictureCallback raw, I_Callbacks.PictureCallback picture)
    {
        pictureCallback = picture;
        shutterCallback = null;
        rawCallback = raw;
        takePicture();
    }

    private void takePicture()
    {
        ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera secCamera)
                {
                    if (rawCallback != null)
                        rawCallback.onPictureTaken(bytes);
                }
            };
        }
        if (pictureCallback == null)
            return;
        PictureCallback pic = new PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera secCamera) {
                pictureCallback.onPictureTaken(bytes);

            }
        };
        try {
            mCamera.takePicture(sh, r, pic);
        }
        catch (RuntimeException ex)
        {
            errorHandler.OnError("Picture Taking failed, What a Terrible Failure!!");
            Logger.exception(ex);
        }

    }

    public void SetPreviewCallback(PreviewCallback previewCallback)
    {
        try {
            if (!isPreviewRunning && !isRdy)
                return;
            Size s = new Size(GetParameterHandler().PreviewSize.GetValue());
            //Add 5 pre allocated buffers. that avoids that the camera create with each frame a new one
            for (int i = 0; i<BUFFERCOUNT;i++)
            {
                mCamera.addCallbackBuffer(new byte[s.height * s.width *
                        ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8]);
            }
            mCamera.setPreviewCallbackWithBuffer(previewCallback);
        }
        catch (NullPointerException ex)
        {
            Logger.e(TAG,ex.getMessage());
        }

    }

    public void ResetPreviewCallback()
    {
        try {
            mCamera.setPreviewCallbackWithBuffer(null);
        }
        catch (NullPointerException ex)
        {
            Logger.e(TAG,ex.getMessage());
        }

    }

    public void SetErrorCallback(final I_Callbacks.ErrorCallback errorCallback)
    {
        if (mCamera == null)
            return;
        mCamera.setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(int error, Camera camera)
            {
                isRdy = false;
                errorCallback.onError(error);
            }
        });

    }

    public void StartFocus(final I_Callbacks.AutoFocusCallback autoFocusCallback)
    {
        if (!isRdy)
            return;
        try {
            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera)
                {
                    if (success)
                        mCamera.cancelAutoFocus();
                    CameraFocusEvent focusEvent = new CameraFocusEvent();
                    focusEvent.camera = camera;
                    focusEvent.success = success;

                    autoFocusCallback.onAutoFocus(focusEvent);
                }
            });
        } catch (Exception ex)
        {
            Logger.e(TAG,ex.getMessage());
            CameraFocusEvent focusEvent = new CameraFocusEvent();
            focusEvent.success = false;
            autoFocusCallback.onAutoFocus(focusEvent);
        }
    }

    public void CancelFocus()
    {
        if (!isRdy)
            return;
        mCamera.cancelAutoFocus();
    }

    public void SetMeteringAreas(FocusRect meteringRect)
    {
        try {

            List<Area> meteringList = new ArrayList<>();
            if (meteringRect != null)
                meteringList.add(new Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 100));
            Parameters p = mCamera.getParameters();
            if(p.getMaxNumMeteringAreas() > 0)
                p.setMeteringAreas(meteringList);

            try {
                Logger.d(TAG, "try Set Metering");
                mCamera.setParameters(p);
                Logger.d(TAG, "Setted Metering");
            } catch (Exception ex) {
                Logger.d(TAG, "Set Metering FAILED!");
            }
        }
        catch (Exception ex)
        {
            Logger.e(TAG,ex.getMessage());
        }
    }

    @Override
    public void SetLocation(Location loc)
    {
        gpsLocation = loc;
        if(!isRdy)
            return;

        if (mCamera != null) {
            Parameters paras = mCamera.getParameters();
            paras.setGpsAltitude(loc.getAltitude());
            paras.setGpsLatitude(loc.getLatitude());
            paras.setGpsLongitude(loc.getLongitude());
            paras.setGpsProcessingMethod(loc.getProvider());
            paras.setGpsTimestamp(loc.getTime());
            try {
                mCamera.setParameters(paras);
            }
            catch (RuntimeException ex)
            {
                errorHandler.OnError("Set Location failed");
            }
        }
    }

    public void SetOrientation(int or)
    {
        if (!isRdy || or == Orientation)
            return;
        Orientation = or;

        if (mCamera != null) {
            Parameters paras = mCamera.getParameters();
            paras.setRotation(or);
            mCamera.setParameters(paras);
        }
    }

    public void SetCameraRotation(int rotation)
    {
        if (!isRdy)
            return;
        mCamera.setDisplayOrientation(rotation);
    }

    public Camera GetCamera() {
        return mCamera;
    }


}
