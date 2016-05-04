package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Handler;

import com.freedcam.apis.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 11.01.2015.
 */
public class SimpleModeParameter extends AbstractModeParameter
{
    private boolean isSupported;

    public SimpleModeParameter(Handler uiHandler) {
        super(uiHandler);
    }

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
