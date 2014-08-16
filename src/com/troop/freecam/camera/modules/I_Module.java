package com.troop.freecam.camera.modules;

import com.troop.freecam.camera.BaseCameraHolder;
import com.troop.freecam.camera.I_CameraHandler;
import com.troop.freecam.enums.E_ManualSeekbar;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_Module
{
    public String ModuleName();

    /**
     * Let the Module start its work
     */
    public void DoWork();
    public boolean IsWorking();
}
