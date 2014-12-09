package com.troop.freedcam.i_camera.modules;

import com.troop.freedcam.camera.modules.AbstractModule;
import com.troop.freedcam.camera.modules.ModuleEventHandler;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_ModuleHandler
{

    public void SetModule(String name);
    public String GetCurrentModuleName();
    public AbstractModule GetCurrentModule();
    public boolean DoWork();
}
