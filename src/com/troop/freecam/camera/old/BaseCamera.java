package com.troop.freecam.camera.old;

import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.troop.freecam.manager.AppSettingsManager;
import com.troop.freecam.utils.DeviceUtils;

/**
 * Created by troop on 18.10.13.
 */
public class BaseCamera
{
    public Camera mCamera;
    public AppSettingsManager Settings;
    final  String TAG = "freecam.BaseCamera";
    //protected byte[] rawbuffer = new byte[52428800];

    public BaseCamera(AppSettingsManager Settings)
    {
        this.Settings = Settings;
    }

    protected void OpenCamera()
    {
        String tmp = Settings.Cameras.GetCamera();
        Settings.CameraCount = Camera.getNumberOfCameras();
        if (Camera.getNumberOfCameras() == 3 || DeviceUtils.isEvo3d())
        {
            Log.d(TAG, "Device Model: " + Build.MODEL);
            if (tmp.equals(AppSettingsManager.Preferences.MODE_3D))
            {
                if (DeviceUtils.isEvo3d())
                {
                    Log.d(TAG, "try open sense 3D camera");
                    try
                    {
                        mCamera = Camera.open(100);
                        //mCamera.setErrorCallback(errorCallback);
                        Settings.CurrentCamera = 100;
                        Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Back3D);
                        Log.d(TAG, "sense 3D camera open");
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        Log.e(TAG, "error open sense 3D Camera");
                        CloseCamera();
                    }
                }
                else
                {
                    mCamera = Camera.open(2);
                    //mCamera.setErrorCallback(errorCallback);
                    Settings.CurrentCamera = 2;
                    Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Back3D);
                }
            }
            else if(tmp.equals(AppSettingsManager.Preferences.MODE_2D))
            {
                Log.d(TAG, "try open 2D camera");
                try
                {
                    mCamera = Camera.open(0);
                    //mCamera.setErrorCallback(errorCallback);
                    Settings.CurrentCamera = 0;
                    Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Back2D);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Set Camera to 2D failed");
                    ex.printStackTrace();
                    CloseCamera();
                }
            }
            else if (tmp.equals(AppSettingsManager.Preferences.MODE_Front))
            {
                try
                {
                    Log.d(TAG, "try open Front camera");
                    mCamera = Camera.open(1);
                    //mCamera.setErrorCallback(errorCallback);
                    Settings.CurrentCamera = 1;
                    Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Front);
                }
                catch (Exception ex)
                {
                    Log.e(TAG, "Set Camera to Front failed");
                    ex.printStackTrace();
                    CloseCamera();
                }
            }
        }
        else if (Camera.getNumberOfCameras() == 2)
        {
            if(tmp.equals(AppSettingsManager.Preferences.MODE_2D))
            {
                mCamera = Camera.open(0);
                Settings.CurrentCamera = 0;
                Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Back2D);
            }
            if (tmp.equals(AppSettingsManager.Preferences.MODE_Front))
            {
                mCamera = Camera.open(1);
                Settings.CurrentCamera = 1;
                Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Front);
            }
        }
        else if (Camera.getNumberOfCameras() == 1)
        {
            mCamera = Camera.open(0);
            Settings.CurrentCamera = 0;
            Settings.Cameras.SetCameraEnum(AppSettingsManager.CameraValues.Front);
        }

        //mCamera.addCallbackBuffer(rawbuffer);
    }
    protected  void CloseCamera()
    {
        Log.d(TAG, "Try to close Camera");
        mCamera.release();

        mCamera = null;
    }


}
