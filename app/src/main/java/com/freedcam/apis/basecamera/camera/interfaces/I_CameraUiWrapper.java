package com.freedcam.apis.basecamera.camera.interfaces;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_CameraUiWrapper
{
    void StartCamera();
    void StopCamera();
    void StartPreview();
    void StopPreview();
    /**
     * Starts a new work with the current active module
     * the module must handle the workstate on its own if it gets hit twice while work is already in progress
     */
    void DoWork();
}
