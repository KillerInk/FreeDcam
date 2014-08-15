package com.troop.freecam.camera.modules;

import com.troop.freecam.camera.I_CameraHandler;
import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_Module
{
    public String ModuleName();
    public void SetCameraHandler(I_CameraHandler cameraHandler);
    public void DoWork();
    public boolean IsWorking();
}
