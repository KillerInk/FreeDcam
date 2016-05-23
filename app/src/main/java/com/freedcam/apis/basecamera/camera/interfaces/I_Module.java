package com.freedcam.apis.basecamera.camera.interfaces;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_Module
{
    /**
     * holds the modulename
     * @return the name of the module
     */
    String ModuleName();

    /**
     * Let the Module start its work
     */
    boolean DoWork();

    /**
     * The workstate of the module
     * @return true if it has work to process
     */
    boolean IsWorking();

    /**
     * Short name of the module
     * @return
     */
    String LongName();
    /**
     * Full name of the module
     * @return
     */
    String ShortName();


    /**
     * geht thrown when the module gets loaded
     */
    void InitModule();

    /**
     * get thrown when the module get unloaded and a new gets loaded
     */
    void DestroyModule();

}
