package com.troop.freecam.manager;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 27.08.13.
 */
public class ZoomManager implements Camera.OnZoomChangeListener
{
    CameraManager cameraManager;
    boolean zoomaktiv = false;
    int currentzoom = 0;
    String TAG = "freecam.ZoomManager";

    public ZoomManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public  void setZoom(int zoom)
    {
        if(!zoomaktiv)
        {
        int temp = currentzoom + zoom;
            if ( temp >= 0 && temp <= cameraManager.parametersManager.getParameters().getMaxZoom() )
            {
                currentzoom = temp;
                Log.d(TAG, "SmoothZoomSupported:" + cameraManager.parametersManager.getParameters().isSmoothZoomSupported());
                cameraManager.parametersManager.getParameters().setZoom(currentzoom);
                if (cameraManager.parametersManager.getParameters().isSmoothZoomSupported())
                {
                    cameraManager.mCamera.startSmoothZoom(currentzoom);
                    zoomaktiv = true;
                }
                else
                {
                    cameraManager.ReloadCameraParameters(false);
                    zoomaktiv = false;
                }


            }
        }
    }

    @Override
    public void onZoomChange(int zoomValue, boolean stopped, Camera camera)
    {
        if (stopped)
            zoomaktiv = false;
        //cameraManager.mCamera.stopSmoothZoom();
    }

    public  void ResetZoom()
    {
        currentzoom = 0;
    }


}
