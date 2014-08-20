package com.troop.freecamv2.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;

/**
 * Created by troop on 15.08.2014.
 */
public class BaseCameraHolder implements I_CameraHolder
{
    Camera mCamera;
    final  String TAG = "freecam.BaseCameraHolder";
    boolean isRdy = false;

    public BaseCameraHolder()
    {
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
            return true;
        }
        catch (Exception ex)
        {
            isRdy = false;
        }
        return false;
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
    public boolean SetPreviewTexture(SurfaceTexture texture) {
        try {
            mCamera.setPreviewTexture(texture);
            return  true;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return false;
    }

    @Override
    public void StartPreview() {
        mCamera.startPreview();
    }

    @Override
    public void StopPreview()
    {
        mCamera.stopPreview();
    }

    public Camera.Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }
}
