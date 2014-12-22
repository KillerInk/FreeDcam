package com.troop.freedcam.camera;

import android.hardware.Camera;
import android.os.Build;
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
            if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21) {
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
    public boolean SetCameraParameters(final Camera.Parameters parameters)
    {
        try{

            if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21)
            {
                lgParameters.setParameters(parameters);
            }
            else
                mCamera.setParameters(parameters);


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

    public Camera.Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }

    public void TakePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback picture)
    {
        if (backGroundThread != null) {
            backGroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.takePicture(shutter, raw, picture);
                }
            });
        }
    }

    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
    {
        if (backGroundThread != null)
        {
            backGroundHandler.post(new Runnable() {
                @Override
                public void run() {

                    mCamera.setPreviewCallback(previewCallback);
                }
            });
        }
    }

    public void StartFocus(final Camera.AutoFocusCallback autoFocusCallback)
    {
        if (backGroundThread != null)
        {
            backGroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.autoFocus(autoFocusCallback);
                }
            });
        }
    }

}
