package com.troop.freecam.camera.parameters.modes;

import java.util.List;

/**
 * Created by troop on 17.08.2014.
 */
public interface I_ModeParameter
{
    public boolean IsSupported();

    public void SetValue(String valueToSet);

    public void GetValue();

    public String[] GetValues();

}
