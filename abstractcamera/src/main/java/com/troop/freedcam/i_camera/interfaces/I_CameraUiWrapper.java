package com.troop.freedcam.i_camera.interfaces;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_CameraUiWrapper
{
    public void SwitchModule(String moduleName);
    public void StartCamera();
    public void StopCamera();
    public void StartPreview();
    public void StopPreview();
    public void DoWork();
}
