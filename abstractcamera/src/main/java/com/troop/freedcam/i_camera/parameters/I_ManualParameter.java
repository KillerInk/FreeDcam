package com.troop.freedcam.i_camera.parameters;

/**
 * Created by troop on 01.09.2014.
 */
public interface I_ManualParameter
{
    public boolean IsSupported();
    public boolean IsSetSupported();
    public boolean IsVisible();

    public int GetValue();
    public String GetStringValue();
    String[] getStringValues();
    public void SetValue(int valueToSet);
}
