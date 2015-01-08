package com.troop.freedcam.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lge.hardware.LGCamera;
import com.sec.android.seccamera.SecCamera;
import com.troop.freedcam.camera.modules.CameraFocusEvent;
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
    I_Callbacks.PreviewCallback previewCallback;

    public boolean hasLGFrameWork = false;
    public boolean hasSamsungFrameWork = false;


    public int CurrentCamera;

    public BaseCameraHolder(I_CameraChangedListner cameraChangedListner, HandlerThread backGroundThread, Handler backGroundHandler, Handler UIHandler)
    {
        super(cameraChangedListner, backGroundThread, backGroundHandler, UIHandler);
        hasSamsungFramework();
        hasLGFramework();
    }

    private void hasSamsungFramework()
    {
        try {
            Class c = Class.forName("com.sec.android.seccamera.SecCamera");
            Log.d(TAG, "Has Samsung Framework");
            hasSamsungFrameWork = true;

        } catch (Exception e) {

            hasSamsungFrameWork = false;
            Log.d(TAG, "No Samsung Framework");
        }

    }

    private void hasLGFramework()
    {
        try {
            Class c = Class.forName("com.lge.hardware.LGCamera");
            Log.d(TAG, "Has Lg Framework");
            hasLGFrameWork = true;

        } catch (Exception e) {

            hasLGFrameWork = false;
            Log.d(TAG, "No LG Framework");
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
            if (hasSamsungFrameWork)
                samsungCamera = SecCamera.open(camera);
            else if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/) {
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

            if (hasSamsungFrameWork)
            {
                SecCamera.Parameters p = samsungCamera.getParameters();
                p.unflatten(ret);
                samsungCamera.setParameters(p);
            }
            else if (hasLGFrameWork /*&& Build.VERSION.SDK_INT < 21*/)
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
            if(hasSamsungFrameWork)
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
        if (hasSamsungFrameWork)
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
            if (hasSamsungFrameWork)
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
        String[] split = null;
        if (hasSamsungFrameWork)
            split = samsungCamera.getParameters().flatten().split(";");
        else if (hasLGFrameWork)
            split = lgCamera.getLGParameters().getParameters().flatten().split(";");
        else
            split = mCamera.getParameters().flatten().split(";");
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
        if (hasSamsungFrameWork)
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

    public void SetPreviewCallback(final I_Callbacks.PreviewCallback previewCallback)
    {
        this.previewCallback = previewCallback;
        if (hasSamsungFrameWork)
        {
            if (previewCallback == null)
                samsungCamera.setPreviewCallback(null);
            else
                samsungCamera.setPreviewCallback(new SecCamera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] bytes, SecCamera secCamera) {
                        previewCallback.onPreviewFrame(bytes);
                    }
                });
        }
        else
        {
            if (previewCallback == null)
                mCamera.setPreviewCallback(null);
            else
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        previewCallback.onPreviewFrame(data);
                    }
                });
        }
    }

    public void SetErrorCallback(final I_Callbacks.ErrorCallback errorCallback)
    {
        if (hasSamsungFrameWork)
        {
            samsungCamera.setErrorCallback(new SecCamera.ErrorCallback() {
                @Override
                public void onError(int i, SecCamera secCamera) {
                    errorCallback.onError(i);
                }
            });
        }
        else
        {
            mCamera.setErrorCallback(new Camera.ErrorCallback() {
                @Override
                public void onError(int error, Camera camera) {
                    errorCallback.onError(error);
                }
            });
        }
    }

    public void StartFocus(final I_Callbacks.AutoFocusCallback autoFocusCallback)
    {

        if (hasSamsungFrameWork)
        {
            samsungCamera.autoFocus(new SecCamera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(int i, SecCamera secCamera)
                {
                    CameraFocusEvent focusEvent = new CameraFocusEvent();
                    focusEvent.samsungCamera = secCamera;
                    if (i == 1) //no idea if this correct
                        focusEvent.success = true;
                    else
                        focusEvent.success = false;
                    autoFocusCallback.onAutoFocus(focusEvent);
                }
            });
        }
        else
        {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera)
                {
                    CameraFocusEvent focusEvent = new CameraFocusEvent();
                    focusEvent.camera = camera;
                    focusEvent.success = success;
                    autoFocusCallback.onAutoFocus(focusEvent);
                }
            });
        }
    }

    public void CancelFocus()
    {
        if (hasSamsungFrameWork)
        {
            samsungCamera.cancelAutoFocus();
        }
        else
        {
            mCamera.cancelAutoFocus();
        }
    }

    public Camera GetCamera() {
        return mCamera;
    }
    public SecCamera GetSamsungCamera() {
        return samsungCamera;
    }


}
