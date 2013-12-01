package com.troop.freecam.camera;

import android.content.SharedPreferences;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 18.10.13.
 */
public class BaseCamera
{
    public Camera mCamera;
    public SharedPreferences preferences;

    public BaseCamera(SharedPreferences preferences)
    {
        this.preferences = preferences;
    }

    protected void OpenCamera()
    {
        String tmp = preferences.getString(CameraManager.SwitchCamera, CameraManager.SwitchCamera_MODE_2D);
        //mCamera.unlock();
        if (tmp.equals(CameraManager.SwitchCamera_MODE_3D))
            mCamera = Camera.open(2);
        if(tmp.equals(CameraManager.SwitchCamera_MODE_2D))
            mCamera = Camera.open(0);
        //mCamera.setDisplayOrientation(90);
        if (tmp.equals(CameraManager.SwitchCamera_MODE_Front))
            mCamera = Camera.open(1);
    }

    protected  void CloseCamera()
    {
        mCamera.release();
        mCamera = null;
    }
}
