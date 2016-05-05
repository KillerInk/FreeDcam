package com.freedcam.apis.basecamera.camera.interfaces;

/**
 * Created by troop on 15.08.2014.
 */
public interface I_Module
{
    String ModuleName();

    /**
     * Let the Module start its work
     */
    boolean DoWork();
    boolean IsWorking();

    String LongName();
    String ShortName();

    void LoadNeededParameters();
    void UnloadNeededParameters();

}
