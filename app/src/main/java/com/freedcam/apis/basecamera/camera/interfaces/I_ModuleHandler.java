package com.freedcam.apis.basecamera.camera.interfaces;

import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;

/**
 * Created by troop on 09.12.2014.
 */
public interface I_ModuleHandler
{
    /**
     * Load the new module
     * @param name of the module to load
     */
    void SetModule(String name);

    /**
     * Get the name of the current module
     * @return name of moduke
     */
    String GetCurrentModuleName();

    /**
     * get the current module instace
     * @return current active module
     */
    AbstractModule GetCurrentModule();

    /**
     * Start work on the current modulé
     * @return
     */
    boolean DoWork();

    /**
     * Add worklistner that listen to the current module
     * @param workerListner to add
     */
    void SetWorkListner(AbstractModuleHandler.I_worker workerListner);
}
