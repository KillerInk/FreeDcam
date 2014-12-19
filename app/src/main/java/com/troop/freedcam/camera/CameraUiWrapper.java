package com.troop.freedcam.camera;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.troop.freedcam.camera.modules.ModuleHandler;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.TextureView.ExtendedSurfaceView;

/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceHolder.Callback, I_ParametersLoaded, Camera.ErrorCallback
{
    protected ExtendedSurfaceView preview;
    protected I_error errorHandler;
    public AppSettingsManager appSettingsManager;

    public CameraUiWrapper(){};

    public CameraUiWrapper(SurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler)
    {
        super(preview,appSettingsManager);
        this.preview = (ExtendedSurfaceView)preview;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);

        this.errorHandler = errorHandler;
        BaseCameraHolder baseCameraHolder =new BaseCameraHolder(this, backGroundThread, backGroundHandler, uiHandler);
        cameraHolder = baseCameraHolder;
        baseCameraHolder.errorHandler = errorHandler;
        camParametersHandler = new CamParametersHandler(cameraHolder, appSettingsManager, backGroundHandler, uiHandler);
        baseCameraHolder.ParameterHandler = camParametersHandler;
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        this.preview.ParametersHandler = camParametersHandler;

        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager);
        Focus = new FocusHandler(this);
        baseCameraHolder.Focus = Focus;
        StartPreviewAndCamera();

    }

    //this get handled in backgroundThread when StartPreviewAndCamera() was called
    @Override
    protected void startCameraAndPreview() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
    }

    @Override
    protected void stopCameraAndPreview() {
        cameraHolder.StopPreview();
        cameraHolder.CloseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        StopPreviewAndCamera();
    }

    @Override
    public void ParametersLoaded()
    {
        cameraHolder.SetSurface(preview.getHolder());
        cameraHolder.StartPreview();
    }

    @Override
    public void onError(int i, Camera camera)
    {
        errorHandler.OnError("Got Error from camera: " + i);
        try
        {
            cameraHolder.CloseCamera();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //this gets called when the cameraholder has open the camera
    @Override
    public void onCameraOpen(String message)
    {
        cameraHolder.GetCamera().setErrorCallback(this);

        CamParametersHandler camParametersHandler1 = (CamParametersHandler) camParametersHandler;
        camParametersHandler1.LoadParametersFromCamera();

        super.onCameraOpen(message);
    }

    @Override
    public void onCameraError(String error) {
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }
}
