package com.troop.freedcamv2.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;

import com.troop.freedcamv2.camera.parameters.CamParametersHandler;

import java.io.IOException;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder implements I_CameraHolder
{
    Camera mCamera;
    final  String TAG = "freedcam.BaseCameraHolder";
    boolean isRdy = false;

    boolean isPreviewRunning = false;

    public CamParametersHandler ParameterHandler;
    public FocusHandler Focus;

    HandlerThread cameraThread;
    Handler cameraHandler;
    public I_error errorHandler;

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
        if (mCamera != null)
        {
            try {
                mCamera.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
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
    public boolean SetCameraParameters(final Camera.Parameters parameters)
    {
        try{

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
        cameraHandler.post(new Runnable() {
            @Override
            public void run() {
                mCamera.startPreview();
                isPreviewRunning = true;
                Log.d(TAG, "PreviewStarted");
            }
        });

    }

    @Override
    public void StopPreview()
    {
        cameraHandler.post(new Runnable() {
            @Override
            public void run()
            {
                //try
                //{

                    mCamera.stopPreview();
                    isPreviewRunning = false;
                    Log.d(TAG, "Preview Stopped");

                /*}
                catch (Exception ex)
                {
                    isPreviewRunning = false;
                    ex.printStackTrace();
                }*/

            }
        });

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

    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback)
    {
        if (cameraThread != null)
        {
            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.setPreviewCallback(previewCallback);
                }
            });
        }
    }

    public void StartFocus(final Camera.AutoFocusCallback autoFocusCallback)
    {
        if (cameraThread != null)
        {
            cameraHandler.post(new Runnable() {
                @Override
                public void run() {
                    mCamera.autoFocus(autoFocusCallback);
                }
            });
        }
    }

}
