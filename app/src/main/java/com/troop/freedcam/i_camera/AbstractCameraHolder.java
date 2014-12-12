package com.troop.freedcam.i_camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 12.12.2014.
 */
public class AbstractCameraHolder implements I_CameraHolder
{
    public boolean isRdy = false;

    protected boolean isPreviewRunning = false;
    public AbstractParameterHandler ParameterHandler;
    public AbstractFocusHandler Focus;
    public SurfaceHolder surfaceHolder;

    @Override
    public boolean OpenCamera(int camera) {
        return false;
    }

    @Override
    public void CloseCamera() {

    }

    @Override
    public Camera GetCamera() {
        return null;
    }

    @Override
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
        return false;
    }

    @Override
    public boolean SetCameraParameters(Camera.Parameters parameters) {
        return false;
    }

    @Override
    public boolean SetSurface(SurfaceHolder texture) {
        return false;
    }

    @Override
    public void StartPreview()
    {
        isPreviewRunning =true;
    }

    @Override
    public void StopPreview()
    {
        isPreviewRunning = false;

    }

    public boolean IsPreviewRunning() {
        return isPreviewRunning;
    }
}
