package com.troop.freecam.camera;

import android.hardware.Camera;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_CameraHolder
{
    public boolean OpenCamera(int camera);
    public void CloseCamera();
    public Camera GetCamera();
    public boolean IsRdy();
    public boolean SetCameraParameters(Camera.Parameters parameters);

}
