package com.freedcam.apis.i_camera.parameters;

/**
 * Created by troop on 01.09.2014.
 */
interface I_ManualParameter
{
    boolean IsSupported();
    boolean IsSetSupported();
    boolean IsVisible();

    int GetValue();
    String GetStringValue();
    String[] getStringValues();
    void SetValue(int valueToSet);
}
