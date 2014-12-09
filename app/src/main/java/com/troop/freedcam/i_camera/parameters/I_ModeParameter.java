package com.troop.freedcam.i_camera.parameters;

/**
 * Created by troop on 17.08.2014.
 */
public interface I_ModeParameter
{
    public boolean IsSupported();

    public void SetValue(String valueToSet, boolean setToCamera);

    public String GetValue();

    public String[] GetValues();

}
