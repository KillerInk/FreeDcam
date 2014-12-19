package com.troop.freedcam.i_camera;

import android.view.SurfaceView;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_Module;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.ui.AppSettingsManager;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper, I_CameraChangedListner
{
    public AbstractModuleHandler moduleHandler;
    public AbstractParameterHandler camParametersHandler;
    public I_CameraHolder cameraHolder;
    public AbstractFocusHandler Focus;
    protected I_error errorHandler;
    I_CameraChangedListner cameraChangedListner;

    public AbstractCameraUiWrapper(){};
    public AbstractCameraUiWrapper(SurfaceView preview, AppSettingsManager appSettingsManager, I_error errorHandler)
    {

    };

    public void SetCameraChangedListner(I_CameraChangedListner cameraChangedListner)
    {
        this.cameraChangedListner = cameraChangedListner;
    }

    public void ErrorHappend(String error)
    {
        if  (errorHandler != null)
            errorHandler.OnError(error);
    }


    @Override
    public void SwitchModule(String moduleName)
    {
        moduleHandler.SetModule(moduleName);
    }

    @Override
    public void StartPreviewAndCamera()
    {

    }

    @Override
    public void StopPreviewAndCamera() {

    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }

    @Override
    public void onCameraOpen(String message)
    {
        if (cameraChangedListner != null)
            cameraChangedListner.onCameraOpen(message);

    }

    @Override
    public void onCameraError(String error) {
        if (cameraChangedListner != null)
            cameraChangedListner.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        if (cameraChangedListner != null)
            cameraChangedListner.onCameraStatusChanged(status);

    }

    @Override
    public void onModuleChanged(I_Module module) {
        if (cameraChangedListner != null)
            cameraChangedListner.onModuleChanged(module);
    }
}
