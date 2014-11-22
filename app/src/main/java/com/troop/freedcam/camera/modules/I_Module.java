package com.troop.freedcam.camera.modules;

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

    public void LoadNeededParameters();
    public void UnloadNeededParameters();

}
