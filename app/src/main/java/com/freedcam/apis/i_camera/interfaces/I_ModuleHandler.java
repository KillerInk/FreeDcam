package com.freedcam.apis.i_camera.interfaces;

import com.freedcam.apis.i_camera.modules.AbstractModule;
import com.freedcam.apis.i_camera.modules.AbstractModuleHandler;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_ModuleHandler
{

    void SetModule(String name);
    String GetCurrentModuleName();
    AbstractModule GetCurrentModule();
    boolean DoWork();
    void SetWorkListner(AbstractModuleHandler.I_worker workerListner);
}
