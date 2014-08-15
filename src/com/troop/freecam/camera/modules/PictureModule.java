package com.troop.freecam.camera.modules;

import com.troop.freecam.camera.I_CameraHandler;

/**
 * Created by troop on 15.08.2014.
 */
public class PictureModule implements I_Module
{
    private I_CameraHandler cameraHandler;

    public PictureModule()
    {
    }
    public PictureModule(I_CameraHandler cameraHandler)
    {
        this.cameraHandler = cameraHandler;
    }

    @Override
    public String ModuleName() {
        return null;
    }

    @Override
    public void SetCameraHandler(I_CameraHandler cameraHandler) {
        this.cameraHandler = cameraHandler;
    }

    @Override
    public void DoWork() {

    }

    @Override
    public boolean IsWorking() {
        return false;
    }
}
