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
import android.location.Location;
import android.os.Handler;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.freedcam.apis.basecamera.camera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.camera.FocusRect;
import com.freedcam.apis.basecamera.camera.Size;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_error;
import com.freedcam.apis.basecamera.camera.modules.CameraFocusEvent;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;
import com.lge.hardware.LGCamera;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private Camera mCamera;
    //lg camera object
    private LGCamera lgCamera;

    final static String TAG = CameraHolder.class.getSimpleName();
    public I_error errorHandler;
    private I_Callbacks.PictureCallback pictureCallback;
    private I_Callbacks.PictureCallback rawCallback;
    private I_Callbacks.ShutterCallback shutterCallback;
    private Surface previewSurfaceHolder;

    public Frameworks DeviceFrameWork = Frameworks.Normal;
    public Location gpsLocation;
    public int Orientation;

    public int CurrentCamera;

    public  enum Frameworks
    {
        Normal,
        LG,
        MTK,
        MotoX
    }

    public CameraHolder(I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager appSettingsManager)
    {
        super(cameraChangedListner, UIHandler,appSettingsManager);
        //hasSamsungFramework();
        DeviceFrameWork = hasLGFramework();
        if (DeviceFrameWork == Frameworks.Normal)
            DeviceFrameWork= isMTKDevice();
        if (DeviceFrameWork == Frameworks.Normal)
            DeviceFrameWork = isMotoExt();

    }

    public String GetParamsDirect(String para)
    {
        Camera.Parameters p = mCamera.getParameters();
        return p.get(para);
    }

    private Frameworks hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCamera");
            Logger.d(TAG, "Has Lg Framework");
            c = Class.forName("com.lge.media.CamcorderProfileEx");
            Logger.d(TAG, "Has Lg Framework");
            return Frameworks.LG;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {

            Logger.d(TAG, "No LG Framework");
            return Frameworks.Normal;
        }
    }

    private Frameworks isMotoExt()
    {
        try {
            Class c = Class.forName("com.motorola.android.camera.CameraMotExt");
            Logger.d(TAG, "Has Moto Framework");
            c = Class.forName("com.motorola.android.media.MediaRecorderExt");
            Logger.d(TAG, "Has Moto Framework");
            return Frameworks.MotoX;

        } catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e) {
            Logger.d(TAG, "No Moto Framework");
            return Frameworks.Normal;
        }

    }

    private Frameworks isMTKDevice()
    {
        try
        {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app != null) {
                Logger.d(TAG,"MTK Framework found");
                return Frameworks.MTK;
            }
            Logger.d(TAG, "MTK Framework not found");
            return Frameworks.Normal;
        }
        catch (ClassNotFoundException|NullPointerException|UnsatisfiedLinkError | ExceptionInInitializerError e)
        {
            Logger.exception(e);
            Logger.d(TAG, "MTK Framework not found");
            return Frameworks.Normal;
        }
    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(final int camera)
    {
        try
        {
            if (DeviceFrameWork == Frameworks.LG /*&& Build.VERSION.SDK_INT < 21*/)
            {
                try {
                    if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                        lgCamera = new LGCamera(camera, 256);
                    else
                        lgCamera = new LGCamera(camera);
                    mCamera = lgCamera.getCamera();
                }
                catch (RuntimeException ex)
                {
                    cameraChangedListner.onCameraError("Fail to connect to camera service");
                }
            }
            else if (DeviceFrameWork == Frameworks.MTK)
            {
                setMtkAppMode();
                mCamera = Camera.open(camera);
            }
            else if(DeviceFrameWork == Frameworks.MotoX)
            {
                mCamera = openWrapper(camera);
                Camera.Parameters paras = mCamera.getParameters();
                paras.set("mot-app", "true");
                mCamera.setParameters(paras);

            }
            else
            {
                mCamera = Camera.open(camera);
                if(DeviceUtils.IS(DeviceUtils.Devices.Htc_M8))
                {
                    Camera.Parameters paras = mCamera.getParameters();
                    paras.set("zsl", "off");
                    mCamera.setParameters(paras);
                }
            }

            isRdy = true;
            cameraChangedListner.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            Logger.exception(ex);
        }
        super.OpenCamera(0);
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

    public boolean SetCameraParameters(Camera.Parameters parameters)
    {
        mCamera.setParameters(parameters);
        return true;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder)
    {
        this.previewSurfaceHolder = surfaceHolder.getSurface();
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
            if (DeviceFrameWork == Frameworks.MTK)
                ((ParametersHandler)GetParameterHandler()).initMTKSHit();
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

    public Camera.Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }

    public void TakePicture(final I_Callbacks.PictureCallback raw, final I_Callbacks.PictureCallback picture)
    {
        this.pictureCallback = picture;
        this.shutterCallback = null;
        this.rawCallback = raw;
        takePicture();
    }

    private void takePicture()
    {
        Camera.ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new Camera.ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        Camera.PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new Camera.PictureCallback() {
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
        Camera.PictureCallback pic = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera secCamera) {
                pictureCallback.onPictureTaken(bytes);

            }
        };
        try {
            this.mCamera.takePicture(sh, r, pic);
        }
        catch (RuntimeException ex)
        {
            errorHandler.OnError("Picture Taking failed, What a Terrible Failure!!");
            Logger.exception(ex);
        }

    }

    private static Camera openWrapper(int n) {
        Class[] arrclass = new Class[]{Integer.TYPE, Integer.TYPE};
        try {
            Method method = Class.forName("android.hardware.Camera").getDeclaredMethod("openLegacy", arrclass);
            Object[] arrobject = new Object[]{n, 256};
            return (Camera)method.invoke(null, arrobject);
        }
        catch (NoSuchMethodException e) {
            Logger.e(TAG, e.getMessage());
            return Camera.open(n);}
        catch (ClassNotFoundException e) {
            Logger.e(TAG, e.getMessage());
            return Camera.open(n);}
        catch (IllegalAccessException e) {
            Logger.e(TAG, e.getMessage());
            return Camera.open(n);}
        catch (InvocationTargetException e) {
            Logger.e(TAG, e.getMessage());
            return Camera.open(n);}
    }

    private void setMtkAppMode()
    {
        try {
            Class camera = Class.forName("android.hardware.Camera");
            Method[] meths = camera.getMethods();
            Method app = null;
            for (Method m : meths)
            {
                if (m.getName().equals("setProperty"))
                    app = m;
            }
            if (app == null)
                throw new  NoSuchMethodException();
            app.invoke(null, "client.appmode", "MtkEng");
        } catch (ClassNotFoundException e) {
            Logger.e(TAG,e.getMessage());
        } catch (IllegalAccessException e) {
            Logger.e(TAG,e.getMessage());
        } catch (InvocationTargetException e) {
            Logger.e(TAG,e.getMessage());
        } catch (NoSuchMethodException e) {
            Logger.e(TAG,e.getMessage());
        }
    }


    @Override
    public void SetPreviewCallback(final I_Callbacks.PreviewCallback previewCallback)
    {
        if (!isPreviewRunning && !isRdy)
            return;

            if (previewCallback == null)
                mCamera.setPreviewCallback(null);
            else
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        previewCallback.onPreviewFrame(data, I_Callbacks.YUV);
                    }
                });

    }

    @Override
    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
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

    @Override
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
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
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
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
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

                List<Camera.Area> meteringList = new ArrayList<>();
                if (meteringRect != null)
                    meteringList.add(new Camera.Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 100));
                Camera.Parameters p = mCamera.getParameters();
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
        this.gpsLocation = loc;
        if(!isRdy)
            return;

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
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
        this.Orientation = or;

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setRotation(or);
                mCamera.setParameters(paras);
            }

    }

    public void SetPreviewSize(String size)
    {
        String split[] = size.split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);

            if (mCamera != null) {
                Camera.Parameters paras = mCamera.getParameters();
                paras.setPreviewSize(width, height);
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
