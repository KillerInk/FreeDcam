package com.freedcam.apis.i_camera.parameters;

/**
 * Created by troop on 17.08.2014.
 */
interface I_ModeParameter
{
    boolean IsSupported();

    void SetValue(String valueToSet, boolean setToCamera);

    String GetValue();

    String[] GetValues();

    boolean IsVisible();

}
