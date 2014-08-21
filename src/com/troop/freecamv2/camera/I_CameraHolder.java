package com.troop.freecamv2.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_CameraHolder
{
    public boolean OpenCamera(int camera);
    public void CloseCamera();
    public Camera GetCamera();
    public int CameraCout();
    public boolean IsRdy();
    public boolean SetCameraParameters(Camera.Parameters parameters);
    public boolean SetSurface(SurfaceHolder texture);
    public void StartPreview();
    public void StopPreview();

}
