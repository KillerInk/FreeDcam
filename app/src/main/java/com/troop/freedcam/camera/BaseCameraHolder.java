package com.troop.freedcam.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.lge.hardware.LGCamera;
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

        try {
            if (DeviceUtils.isLGADV() /*&& Build.VERSION.SDK_INT < 21*/) {
                lgCamera = new LGCamera(camera);
                /*lgCamera.setProxyDataListener(new LGCamera.ProxyDataListener() {
                    @Override
                    public void onDataListen(LGCamera.ProxyData proxyData, Camera camera) {
                        LGCamera.ProxyData data = proxyData;

                    }
                });*/
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
        if (mCamera != null)
        {
            try
            {
                mCamera.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        /*try {
            backGroundThread.quit();
            backGroundThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        isRdy = false;

        mCamera = null;
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

            if (DeviceUtils.isLGADV() /*&& Build.VERSION.SDK_INT < 21*/)
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
        mCamera.startPreview();
        isPreviewRunning = true;
        Log.d(TAG, "PreviewStarted");
        cameraChangedListner.onPreviewOpen("");
    }

    @Override
    public void StopPreview()
    {
        if (mCamera == null)
            return;
        try {
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

    public void TakePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback picture)
    {
        /*if (backGroundThread != null) {
            backGroundHandler.post(new Runnable() {
                @Override
                public void run() {*/
                    this.mCamera.takePicture(shutter, raw, picture);
                /*}
            });
        }*/
    }

    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
    {
        /*if (backGroundThread != null)
        {
            backGroundHandler.post(new Runnable() {
                @Override
                public void run() {*/

                    mCamera.setPreviewCallback(previewCallback);
                /*}
            });
        }*/
    }

    public void StartFocus(final Camera.AutoFocusCallback autoFocusCallback)
    {
        mCamera.autoFocus(autoFocusCallback);

    }

}
