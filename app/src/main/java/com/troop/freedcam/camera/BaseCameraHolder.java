package com.troop.freedcam.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lge.hardware.LGCamera;
import com.sec.android.seccamera.SecCamera;
import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.utils.DeviceUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder extends AbstractCameraHolder
{
    Camera mCamera;
    LGCamera lgCamera;
    LGCamera.LGParameters lgParameters;
    final  String TAG = "freedcam.BaseCameraHolder";
    public I_error errorHandler;
    SecCamera samsungCamera;
    I_Callbacks.PictureCallback pictureCallback;
    I_Callbacks.PictureCallback rawCallback;
    I_Callbacks.ShutterCallback shutterCallback;


    public int CurrentCamera;

    public BaseCameraHolder(I_CameraChangedListner cameraChangedListner, HandlerThread backGroundThread, Handler backGroundHandler, Handler UIHandler)
    {
        super(cameraChangedListner, backGroundThread, backGroundHandler, UIHandler);
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
            if (DeviceUtils.isSamsungADV())
                samsungCamera = SecCamera.open(camera);
            else if (DeviceUtils.isLGADV() /*&& Build.VERSION.SDK_INT < 21*/) {
                lgCamera = new LGCamera(camera);
                mCamera = lgCamera.getCamera();
                lgParameters = lgCamera.getLGParameters();
            } else {
                mCamera = Camera.open(camera);
            }

            isRdy = true;
            cameraChangedListner.onCameraOpen("");

        } catch (Exception ex) {
            isRdy = false;
            ex.printStackTrace();
        }

        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        if (samsungCamera != null)
        {
            samsungCamera.release();
            samsungCamera = null;
        }
        else if (mCamera != null)
        {
            try
            {
                mCamera.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally {
                mCamera = null;
            }
        }
        isRdy = false;
        cameraChangedListner.onCameraClose("");
    }

    /**
     * Check if the camera isrdy bevor calling this
     * @return returns the CameraObject
     */

    @Override
    public Camera GetCamera() {
        return mCamera;
    }

    @Override
    public int CameraCout() {
        return Camera.getNumberOfCameras();
    }

    @Override
    public boolean IsRdy() {
        return isRdy;
    }

    @Override
    public boolean SetCameraParameters(final HashMap<String, String> parameters)
    {
        String ret = "";
        for (Map.Entry s : parameters.entrySet())
        {
            ret += s.getKey() + "=" + s.getValue()+";";
        }
        try{

            if (DeviceUtils.isSamsungADV())
            {
                SecCamera.Parameters p = samsungCamera.getParameters();
                p.unflatten(ret);
                samsungCamera.setParameters(p);
            }
            else if (DeviceUtils.isLGADV() /*&& Build.VERSION.SDK_INT < 21*/)
            {
                Camera.Parameters p = lgParameters.getParameters();
                p.unflatten(ret);
                lgParameters.setParameters(p);
            }
            else
            {
                Camera.Parameters p = mCamera.getParameters();
                p.unflatten(ret);
                mCamera.setParameters(p);
            }


            return true;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder) {
        try {
            while (!isRdy)
                Thread.sleep(10);
            if(DeviceUtils.isSamsungADV())
                samsungCamera.setPreviewDisplay(surfaceHolder);
            else
                mCamera.setPreviewDisplay(surfaceHolder);
            this.surfaceHolder = surfaceHolder;
            return  true;
        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void StartPreview()
    {
        if (DeviceUtils.isSamsungADV())
            samsungCamera.startPreview();
        else
            mCamera.startPreview();
        isPreviewRunning = true;
        Log.d(TAG, "PreviewStarted");
        cameraChangedListner.onPreviewOpen("");
    }

    @Override
    public void StopPreview()
    {
        if (mCamera == null && samsungCamera == null)
            return;
        try {
            if (DeviceUtils.isSamsungADV())
                samsungCamera.stopPreview();
            else
                mCamera.stopPreview();
            isPreviewRunning = false;
            Log.d(TAG, "Preview Stopped");
            cameraChangedListner.onPreviewClose("");

        } catch (Exception ex) {
            isPreviewRunning = false;
            Log.d(TAG, "Camera was released");
            ex.printStackTrace();
        }
    }

    public HashMap<String, String> GetCameraParameters()
    {
        String[] split = mCamera.getParameters().flatten().split(";");
        HashMap<String, String> map = new HashMap<>();
        for (String s: split)
        {
            String[] valSplit = s.split("=");
            boolean sucess = false;
            try
            {
                map.put(valSplit[0], valSplit[1]);
            }
            catch (Exception ex)
            {
                map.put(valSplit[0], "");
            }

        }

        return map;
    }

    public void TakePicture(final I_Callbacks.ShutterCallback shutter, final I_Callbacks.PictureCallback raw, final I_Callbacks.PictureCallback picture)
    {
        this.pictureCallback = picture;
        this.shutterCallback = shutter;
        this.rawCallback = raw;
        if (DeviceUtils.isSamsungADV())
        {
            takeSamsungPicture();
        }
        else
        {
            takePicture();
        }

    }

    private void takePicture() {
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
                public void onPictureTaken(byte[] bytes, Camera secCamera) {
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
                rawCallback = null;
                shutterCallback = null;
                pictureCallback = null;
            }
        };
        this.mCamera.takePicture(sh, r, pic);
    }

    private void takeSamsungPicture() {
        SecCamera.ShutterCallback sh = null;
        if (shutterCallback != null)
        {
            sh = new SecCamera.ShutterCallback() {
                @Override
                public void onShutter() {
                    shutterCallback.onShutter();
                }
            };
        }
        SecCamera.PictureCallback r = null;
        if (rawCallback != null)
        {
            r = new SecCamera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, SecCamera secCamera) {
                    rawCallback.onPictureTaken(bytes);
                }
            };
        }
        if (pictureCallback == null)
            return;
        SecCamera.PictureCallback pic = new SecCamera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, SecCamera secCamera) {
                pictureCallback.onPictureTaken(bytes);
                rawCallback = null;
                shutterCallback = null;
                pictureCallback = null;
            }
        };
        samsungCamera.takePicture(sh,r,pic);
    }

    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
    {

        mCamera.setPreviewCallback(previewCallback);
    }

    public void StartFocus(final Camera.AutoFocusCallback autoFocusCallback)
    {
        mCamera.autoFocus(autoFocusCallback);

    }

}
