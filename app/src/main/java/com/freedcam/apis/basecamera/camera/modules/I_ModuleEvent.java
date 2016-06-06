package com.freedcam.apis.basecamera.camera.modules;

/**
 * Created by troop on 23.08.2014.
 */
public interface I_ModuleEvent
{
    /**
     * Gets called when the module has changed
     * @param module
     */
    void ModuleChanged(String module);
}
