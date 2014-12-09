package com.troop.freedcam.i_camera;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_CameraUiWrapper
{
    public void SwitchModule(String moduleName);
    public void StartPreviewAndCamera();
    public void StopPreviewAndCamera();
    public void DoWork();
}
