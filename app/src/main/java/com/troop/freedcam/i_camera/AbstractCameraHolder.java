package com.troop.freedcam.i_camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractCameraHolder implements I_CameraHolder
{
    public boolean isRdy = false;

    public boolean isPreviewRunning = false;
    public AbstractParameterHandler ParameterHandler;
    public AbstractFocusHandler Focus;
    public SurfaceHolder surfaceHolder;
    protected I_CameraChangedListner cameraChangedListner;
    protected HandlerThread backGroundThread;
    protected Handler backGroundHandler;
    protected Handler UIHandler;

    public AbstractCameraHolder(I_CameraChangedListner cameraChangedListner, HandlerThread backGroundThread, Handler backGroundHandler,Handler UIHandler)
    {
        this.cameraChangedListner = cameraChangedListner;
        this.backGroundHandler = backGroundHandler;
        this.backGroundThread = backGroundThread;
        this.UIHandler = UIHandler;
    }

    @Override
    public boolean OpenCamera(int camera)
    {

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
