package com.troop.freecam;

import android.hardware.Camera;

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
            if ( temp >= 0 && temp <= cameraManager.parameters.getMaxZoom() )
            {
                currentzoom = temp;
                cameraManager.parameters.setZoom(currentzoom);
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
