package com.troop.freecam.manager;

import android.hardware.Camera;

import com.troop.freecam.camera.CameraManager;

/**
 * Created by troop on 27.08.13.
 */
public class ZoomManager implements Camera.OnZoomChangeListener
{
    CameraManager cameraManager;
    boolean zoomaktiv = false;
    int currentzoom = 0;

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
                cameraManager.parametersManager.getParameters().setZoom(currentzoom);
                cameraManager.mCamera.startSmoothZoom(currentzoom);
                zoomaktiv = true;
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
