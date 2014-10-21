package com.troop.freecamv2.camera.parameters.modes;

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
