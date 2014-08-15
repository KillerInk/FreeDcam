package com.troop.freecam.camera;

import android.hardware.Camera;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_CameraHandler
{
    public void OpenCamera();
    public void CloseCamera();
    public Camera GetCamera();
    public boolean IsWorking();

}
