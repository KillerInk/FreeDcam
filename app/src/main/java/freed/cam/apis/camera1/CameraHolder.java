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

package freed.cam.apis.camera1;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.location.Location;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.events.CameraStateEvents;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.Frameworks;
import freed.utils.Log;

/**
 * Created by troop on 15.08.2014.
 */
public class CameraHolder extends CameraHolderAbstract implements CameraHolderInterfaceApi1
{
    //camera object
    protected Camera mCamera;

    private final String TAG = CameraHolder.class.getSimpleName();
    private Surface previewSurfaceHolder;

    public Frameworks DeviceFrameWork = Frameworks.Default;
    public int Orientation;

    private Method setPreviewSurfaceMethod;


    public CameraHolder(CameraWrapperInterface cameraUiWrapper, Frameworks frameworks)
    {
        super(cameraUiWrapper);
        DeviceFrameWork = frameworks;
        try {
            setPreviewSurfaceMethod = Camera.class.getMethod("setPreviewSurface",Surface.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public String GetParamsDirect(String para)
    {
        Parameters p = mCamera.getParameters();
        return p.get(para);
    }

    public boolean canSetSurfaceDirect()
    {
        return setPreviewSurfaceMethod != null;
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
            Log.d(TAG, "open camera");
            mCamera = Camera.open(camera);
            mCamera.setErrorCallback((error, camera1) -> Log.e(TAG, "Error:" + error));
            CameraStateEvents.fireCameraOpenEvent();
            return true;

        } catch (Exception ex) {
            Log.WriteEx(ex);
            if (mCamera != null)
                mCamera.release();
        }
        return false;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        try
        {
            mCamera.release();
            Log.d(TAG, "Camera Released");
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }
        finally {
            mCamera = null;
            Log.d(TAG, "Camera closed");
        }
        CameraStateEvents.fireCameraCloseEvent();
    }

    public void SetCameraParameters(Parameters parameters)
    {
        try {
            mCamera.setParameters(parameters);
        }
        catch (Exception ex)
        {
            Log.WriteEx(ex);
        }

    }


    @Override
    public boolean setSurface(Surface texture) {
        Log.d(TAG, "setSurface surface");
        try {
            if (mCamera != null) {
                if (setPreviewSurfaceMethod != null) {
                    setPreviewSurfaceMethod.setAccessible(true);
                    setPreviewSurfaceMethod.invoke(mCamera, texture);
                    setPreviewSurfaceMethod.setAccessible(false);
                }
                return true;
            }
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        } catch (IllegalAccessException ex) {
            Log.WriteEx(ex);
        } catch (InvocationTargetException ex) {
            Log.WriteEx(ex);
        }
        return false;
    }

    public void setTextureView(TextureView texturView)
    {
        try {
            mCamera.setPreviewTexture(texturView.getSurfaceTexture());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            UserMessageHandler.sendMSG("Failed to Start Preview, Camera is null",false);
            return;
        }
        try
        {
            mCamera.startPreview();
            Log.d(TAG, "PreviewStarted");
            CameraStateEvents.firePreviewOpenEvent();
        } catch (Exception ex) {
            Log.WriteEx(ex);
            UserMessageHandler.sendMSG("Failed to Start Preview",false);
        }
    }

    @Override
    public void StopPreview()
    {
        Log.d(TAG, "Stop Preview");
        if (mCamera == null)
            return;
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            Log.d(TAG, "Preview Stopped");
            CameraStateEvents.firePreviewCloseEvent();
        } catch (Exception ex)
        {
            Log.d(TAG, "Camera was released");
            Log.WriteEx(ex);
        }
    }

    public Parameters GetCameraParameters()
    {
        try {
            return mCamera.getParameters();
        }catch (NullPointerException ex)
        {
            return null;
        }


    }

    public void TakePicture(PictureCallback picture)
    {
        try {
            mCamera.takePicture(null, null, picture);
        }
        catch (RuntimeException ex)
        {
            UserMessageHandler.sendMSG("Picture Taking failed, What a Terrible Failure!!",false);
            Log.WriteEx(ex);
        }
    }


    public void resetPreviewCallback()
    {
        try {
            mCamera.setPreviewCallbackWithBuffer(null);
        }
        catch (NullPointerException ex)
        {
            Log.e(TAG,ex.getMessage());
        }
        catch (RuntimeException ex)
        {
            Log.d(TAG, "Camera was released");
            Log.WriteEx(ex);
        }

    }

    public void StartFocus(final FocusEvents autoFocusCallback)
    {
        if (mCamera == null)
            return;
        try {
            mCamera.autoFocus((success, camera) -> {
                if (mCamera == null)
                    return;
                if (success)
                    mCamera.cancelAutoFocus();
                autoFocusCallback.onFocusEvent(success);
            });
        } catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage());
            autoFocusCallback.onFocusEvent(false);
        }
    }

    public void CancelFocus()
    {
        if (mCamera == null)
            return;
        mCamera.cancelAutoFocus();
    }

    public void SetMeteringAreas(Rect meteringRect)
    {
        try {

            List<Area> meteringList = new ArrayList<>();
            if (meteringRect != null)
                meteringList.add(new Area(new Rect(meteringRect.left, meteringRect.top, meteringRect.right, meteringRect.bottom), 100));
            Parameters p = mCamera.getParameters();
            if(p.getMaxNumMeteringAreas() > 0)
                p.setMeteringAreas(meteringList);

            try {
                Log.d(TAG, "try Set Metering");
                mCamera.setParameters(p);
                Log.d(TAG, "Setted Metering");
            } catch (Exception ex) {
                Log.d(TAG, "Set Metering FAILED!");
            }
        }
        catch (Exception ex)
        {
            Log.e(TAG,ex.getMessage());
        }
    }

    @Override
    public void SetLocation(Location loc)
    {
        if (mCamera != null && loc != null) {
            Parameters paras = mCamera.getParameters();
            if (loc.hasAltitude())
                paras.setGpsAltitude(loc.getAltitude());
            paras.setGpsLatitude(loc.getLatitude());
            paras.setGpsLongitude(loc.getLongitude());
            paras.setGpsProcessingMethod(loc.getProvider());
            paras.setGpsTimestamp(loc.getTime()/1000);
            try {
                mCamera.setParameters(paras);
            }
            catch (RuntimeException ex)
            {
                UserMessageHandler.sendMSG("Set Location failed",false);

            }
        }
    }

    public void SetCameraRotation(int rotation)
    {
        if (mCamera == null)
            return;
        mCamera.setDisplayOrientation(rotation);
    }

    public Camera GetCamera() {
        return mCamera;
    }


}
