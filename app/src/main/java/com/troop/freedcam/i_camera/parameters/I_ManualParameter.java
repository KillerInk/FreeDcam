package com.troop.freedcam.i_camera.parameters;

/**
 * Created by troop on 01.09.2014.
 */
public interface I_ManualParameter
{
    public boolean IsSupported();

    public int GetMaxValue();

    public  int GetMinValue();

    public int GetValue();
    public String GetStringValue();
    String[] getStringValues();

    public void SetValue(int valueToSet);
    public void RestartPreview();
}
