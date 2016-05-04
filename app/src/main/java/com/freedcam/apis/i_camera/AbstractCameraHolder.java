package com.freedcam.apis.i_camera;

import android.hardware.Camera;
import android.location.Location;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.freedcam.apis.i_camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.i_camera.interfaces.I_CameraHolder;
import com.freedcam.apis.i_camera.modules.I_Callbacks;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

import java.util.HashMap;

/**
 * Created by troop on 12.12.2014.
 */
public abstract class AbstractCameraHolder implements I_CameraHolder
{
    protected boolean isRdy = false;

    public boolean isPreviewRunning = false;
    private AbstractParameterHandler ParameterHandler;
    public AbstractFocusHandler Focus;
    public AbstractExposureMeterHandler ExposureM;
    public SurfaceHolder surfaceHolder;
    protected I_CameraChangedListner cameraChangedListner;
    protected Handler UIHandler;
    protected AppSettingsManager appSettingsManager;

    protected CameraStates currentState = CameraStates.closed;
    public enum CameraStates
    {
        opening,
        open,
        closing,
        closed,
        working,
    }


    protected AbstractCameraHolder(I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager appSettingsManager)
    {
        this.cameraChangedListner = cameraChangedListner;
        this.appSettingsManager = appSettingsManager;
        this.UIHandler = UIHandler;
    }

    public void SendUIMessage(String msg)
    {
        if (cameraChangedListner != null)
            cameraChangedListner.onCameraError(msg);
    }

    @Override
    public boolean OpenCamera(int camera)
    {
        currentState = CameraStates.open;
        return false;
    }

    @Override
    public void CloseCamera() {
        currentState = CameraStates.closed;
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

    public void StartFocus(I_Callbacks.AutoFocusCallback autoFocusCallback){}
    public void CancelFocus(){}

    public abstract void SetLocation(Location loc);

    public abstract void SetPreviewCallback(final I_Callbacks.PreviewCallback previewCallback);

    public void SetPreviewCallback(final Camera.PreviewCallback previewCallback){}

    public void ResetPreviewCallback(){}

    public void SetParameterHandler(AbstractParameterHandler parametersHandler)
    {
        this.ParameterHandler = parametersHandler;
    }

    public AbstractParameterHandler GetParameterHandler()
    {
        return ParameterHandler;
    }
}
