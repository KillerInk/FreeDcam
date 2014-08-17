package com.troop.freecam.camera;

import android.hardware.Camera;
import android.util.Log;

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

    public Camera.Parameters GetCameraParameters()
    {
        return mCamera.getParameters();
    }
}
