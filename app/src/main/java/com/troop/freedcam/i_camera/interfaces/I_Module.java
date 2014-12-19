package com.troop.freedcam.i_camera.interfaces;

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

    public String LongName();
    public String ShortName();

    public void LoadNeededParameters();
    public void UnloadNeededParameters();

}
