package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;

import com.troop.freecam.manager.ParametersManager;

/**
 * Created by troop on 18.10.13.
 */
public class BaseCamera
{
    public Camera mCamera;
    public SharedPreferences preferences;
    //protected byte[] rawbuffer = new byte[52428800];

    public BaseCamera(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    protected void OpenCamera()
    {
        String tmp = preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_Front);
        //mCamera.unlock();
        if (Camera.getNumberOfCameras() == 3)
        {
            if (tmp.equals(ParametersManager.SwitchCamera_MODE_3D))
                mCamera = Camera.open(2);
            if(tmp.equals(ParametersManager.SwitchCamera_MODE_2D))
                mCamera = Camera.open(0);
            //mCamera.setDisplayOrientation(90);
            if (tmp.equals(ParametersManager.SwitchCamera_MODE_Front))
                mCamera = Camera.open(1);
        }
        else if (Camera.getNumberOfCameras() == 2)
        {
            if(tmp.equals(ParametersManager.SwitchCamera_MODE_2D))
                mCamera = Camera.open(0);
            //mCamera.setDisplayOrientation(90);
            if (tmp.equals(ParametersManager.SwitchCamera_MODE_Front))
                mCamera = Camera.open(1);
        }
        else if (Camera.getNumberOfCameras() == 1)
        {
            mCamera = Camera.open(0);
        }
        //mCamera.addCallbackBuffer(rawbuffer);
    }

    protected  void CloseCamera()
    {
        mCamera.release();
        mCamera = null;
    }
}
