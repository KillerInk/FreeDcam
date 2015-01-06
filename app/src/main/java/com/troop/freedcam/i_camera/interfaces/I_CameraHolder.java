package com.troop.freedcam.i_camera.interfaces;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.util.HashMap;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_CameraHolder
{
    public boolean OpenCamera(int camera);
    public void CloseCamera();

    public int CameraCout();
    public boolean IsRdy();
    public boolean SetCameraParameters(HashMap<String, String> parameters);
    public boolean SetSurface(SurfaceHolder texture);
    public void StartPreview();
    public void StopPreview();

}
