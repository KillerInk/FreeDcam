package com.freedcam.apis.basecamera.camera;

import android.hardware.Camera;
import android.location.Location;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraChangedListner;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.camera.modules.I_Callbacks;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 12.12.2014.
 * holds the instance for the camera to work with
 */
public abstract class AbstractCameraHolder implements I_CameraHolder
{
    protected boolean isRdy = false;

    public boolean isPreviewRunning = false;
    //holds the parameters for the camera
    private AbstractParameterHandler ParameterHandler;
    //handel focus realted stuff
    public AbstractFocusHandler Focus;
    public SurfaceHolder surfaceHolder;
    //the listner to for camera state changes
    protected I_CameraChangedListner cameraChangedListner;
    //handler wich runs in mainthread
    protected Handler UIHandler;
    //holds the appsettings
    protected AppSettingsManager appSettingsManager;

    //the current camera state
    protected CameraStates currentState = CameraStates.closed;

    public enum CameraStates
    {
        opening,
        open,
        closing,
        closed,
        working,
    }


    /**
     *
     * @param cameraChangedListner to listen on camera state changes
     * @param UIHandler handler to invoke mainthread
     * @param appSettingsManager
     */
    protected AbstractCameraHolder(I_CameraChangedListner cameraChangedListner, Handler UIHandler, AppSettingsManager appSettingsManager)
    {
        this.cameraChangedListner = cameraChangedListner;
        this.appSettingsManager = appSettingsManager;
        this.UIHandler = UIHandler;
    }

    /**
     * Send message to UI
     * @param msg to send
     */
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

    /**
     *
     * @return the count of avail cameras
     */
    @Override
    public int CameraCout() {
        return 0;
    }

    @Override
    public boolean IsRdy() {
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
