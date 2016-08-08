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

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.location.Location;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraHolderAbstract;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusEvents;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.Size;
import freed.utils.Logger;

/**
 * Created by troop on 15.08.2014.
 */
public class CameraHolder extends CameraHolderAbstract
{
    //frame count that get attached to the camera when using focuspeak
    final int BUFFERCOUNT = 3;
    //camera object
    protected Camera mCamera;

    private final String TAG = CameraHolder.class.getSimpleName();
    private Surface previewSurfaceHolder;

    public Frameworks DeviceFrameWork = Frameworks.Normal;
    public int Orientation;

    public int CurrentCamera;

    public enum Frameworks
    {
        Normal,
        LG,
        MTK,
        MotoX
    }

    public CameraHolder(CameraWrapperInterface cameraUiWrapper, Frameworks frameworks)
    {
        super(cameraUiWrapper);
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
            cameraUiWrapper.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            Logger.exception(ex);
        }
        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
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
        cameraUiWrapper.onCameraClose("");
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
        Parameters parameters1 = mCamera.getParameters();
        Logger.d(TAG, "PreviewFPS:" + parameters1.get(KEYS.PREVIEW_FRAME_RATE));
        Logger.d(TAG, "PreviewFPS_Range:" + parameters1.get("preview-fps-range"));
        Logger.d(TAG, "PreviewSize:" + parameters1.get("preview-size"));
        Logger.d(TAG, "VideoSize:" + parameters1.get("video-size"));

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
            Logger.d(TAG, "PreviewStarted");
            cameraUiWrapper.onPreviewOpen("");

        } catch (Exception ex) {
            Logger.exception(ex);
            SendUIMessage("Failed to Start Preview");
        }
    }

    @Override
    public void StopPreview()
    {
        if (mCamera == null)
            return;
        try {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            Logger.d(TAG, "Preview Stopped");
            cameraUiWrapper.onPreviewClose("");

        } catch (Exception ex)
        {
            cameraUiWrapper.onPreviewClose("");
            Logger.d(TAG, "Camera was released");
            Logger.exception(ex);
        }
    }

    public Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }

    public void TakePicture(PictureCallback picture)
    {
        try {
            mCamera.takePicture(null, null, picture);
        }
        catch (RuntimeException ex)
        {
            SendUIMessage("Picture Taking failed, What a Terrible Failure!!");
            Logger.exception(ex);
        }
    }

    public void SetPreviewCallback(PreviewCallback previewCallback)
    {
        try {
            if (!isRdy)
                return;
            Size s = new Size(cameraUiWrapper.GetParameterHandler().PreviewSize.GetValue());
            //Add 5 pre allocated buffers. that avoids that the camera create with each frame a new one
            for (int i = 0; i< BUFFERCOUNT; i++)
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

    public void StartFocus(final FocusEvents autoFocusCallback)
    {
        if (!isRdy)
            return;
        try {
            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera)
                {
                    if (mCamera == null)
                        return;
                    if (success)
                        mCamera.cancelAutoFocus();
                    autoFocusCallback.onFocusEvent(success);
                }
            });
        } catch (Exception ex)
        {
            Logger.e(TAG,ex.getMessage());
            autoFocusCallback.onFocusEvent(false);
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
                SendUIMessage("Set Location failed");
            }
        }
    }

    @Override
    public void StartFocus() {
        cameraUiWrapper.getFocusHandler().StartFocus();
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
