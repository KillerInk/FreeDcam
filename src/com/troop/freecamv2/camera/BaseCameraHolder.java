package com.troop.freecamv2.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.troop.freecamv2.camera.parameters.CamParametersHandler;

import java.io.IOException;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder implements I_CameraHolder
{
    Camera mCamera;
    final  String TAG = "freecam.BaseCameraHolder";
    boolean isRdy = false;

    boolean isPreviewRunning = false;

    public CamParametersHandler ParameterHandler;

    HandlerThread cameraThread;
    Handler cameraHandler;

    public BaseCameraHolder()
    {
    }

    /**
     * Opens the Camera
     * @param camera the camera to open
     * @return false if camera open fails, return true when open
     */
    @Override
    public boolean OpenCamera(final int camera)
    {
        //open camera into new looper thread
        if (cameraThread == null) {
            cameraThread = new HandlerThread(TAG);
            cameraThread.start();
            cameraHandler = new Handler(cameraThread.getLooper());
        }
        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                try
                {
                    mCamera = Camera.open(camera);
                    isRdy = true;

                }
                catch (Exception ex)
                {
                    isRdy = false;
                }
            }
        });


        return isRdy;
    }

    @Override
    public void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        mCamera.release();
        isRdy = false;

        mCamera = null;
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
    public boolean SetCameraParameters(Camera.Parameters parameters) {
        try{
            mCamera.setParameters(parameters);
            return true;
        }
        catch (Exception ex)
        {}
        return false;
    }

    @Override
    public boolean SetSurface(SurfaceHolder surfaceHolder) {
        try {
            while (!isRdy)
                Thread.sleep(100);
            mCamera.setPreviewDisplay(surfaceHolder);
            return  true;
        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void StartPreview() {
        mCamera.startPreview();
        isPreviewRunning = true;
    }

    @Override
    public void StopPreview()
    {
        mCamera.stopPreview();
        isPreviewRunning = false;
    }

    public Camera.Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }

    public boolean IsPreviewRunning() { return isPreviewRunning; }

    public void TakePicture(final Camera.ShutterCallback shutter, final Camera.PictureCallback raw, final Camera.PictureCallback picture)
    {
        if (cameraThread != null) {
            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.takePicture(shutter, raw, picture);
                }
            });
        }
    }
}
