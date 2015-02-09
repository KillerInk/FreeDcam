package com.troop.freedcam.i_camera;

import android.location.Location;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.troop.freedcam.camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractCameraHolder implements I_CameraHolder
{
    public boolean isRdy = false;

    public boolean isPreviewRunning = false;
    public AbstractParameterHandler ParameterHandler;
    public AbstractFocusHandler Focus;
    public AbstractExposureMeterHandler ExposureM;
    public SurfaceHolder surfaceHolder;
    protected I_CameraChangedListner cameraChangedListner;
    protected Handler UIHandler;

    public AbstractCameraHolder(I_CameraChangedListner cameraChangedListner,Handler UIHandler)
    {
        this.cameraChangedListner = cameraChangedListner;

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
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
        return false;
    }

    @Override
    public boolean SetCameraParameters(HashMap<String, String> parameters) {
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

    public void StartFocus(I_Callbacks.AutoFocusCallback autoFocusCallback){};
    public void CancelFocus(){};

    public abstract void SetLocation(Location loc);
}
