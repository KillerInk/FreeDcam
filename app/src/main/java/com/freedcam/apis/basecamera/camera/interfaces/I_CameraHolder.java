package com.freedcam.apis.basecamera.camera.interfaces;

import android.view.SurfaceHolder;

import java.util.HashMap;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_CameraHolder
{
    /**
     * open the camera
     * @param camera to open
     * @return true when open sucessfull, false when something went wrong
     */
    boolean OpenCamera(int camera);
    void CloseCamera();
    /**
     *
     * @return the count of avail cameras
     */
    int CameraCout();
    boolean IsRdy();

    /**
     * The the surface to camera
     * @param texture to set
     * @return
     */
    boolean SetSurface(SurfaceHolder texture);
    void StartPreview();
    void StopPreview();

}
