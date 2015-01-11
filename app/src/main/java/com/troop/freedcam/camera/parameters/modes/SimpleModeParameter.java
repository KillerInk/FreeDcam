package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 11.01.2015.
 */
public class SimpleModeParameter extends AbstractModeParameter
{
    boolean isSupported;

    public boolean IsSupported()
    {
        return isSupported;
    }
    public void setIsSupported(boolean s)
    {
        this.isSupported = s;
    }

    public void SetValue(String valueToSet, boolean setToCamera) {

    }

    public String GetValue() {
        return null;
    }

    public String[] GetValues() {
        return new String[0];
    }

}
