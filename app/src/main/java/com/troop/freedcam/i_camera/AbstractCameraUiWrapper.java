package com.troop.freedcam.i_camera;

import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleHandler;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 09.12.2014.
 */
public abstract class AbstractCameraUiWrapper implements I_CameraUiWrapper
{
    public AbstractModuleHandler moduleHandler;
    public AbstractParameterHandler camParametersHandler;
    public I_CameraHolder cameraHolder;
    public AbstractFocusHandler Focus;

    @Override
    public void SwitchModule(String moduleName)
    {
        moduleHandler.SetModule(moduleName);
    }

    @Override
    public void StartPreviewAndCamera() {

    }

    @Override
    public void StopPreviewAndCamera() {

    }

    @Override
    public void DoWork() {
        moduleHandler.DoWork();
    }
}
