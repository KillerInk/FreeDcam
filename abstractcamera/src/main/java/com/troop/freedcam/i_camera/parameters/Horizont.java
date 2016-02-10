package com.troop.freedcam.i_camera.parameters;


import android.os.Handler;


/**
 * Created by Ar4eR on 14.01.16.
 */
public class Horizont extends AbstractModeParameter {

    String value;

    public Horizont(Handler uiHandler) {
        super(uiHandler);
    }



    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(final String valueToSet, boolean setToCam)
    {
        value = valueToSet;
        BackgroundValueHasChanged(valueToSet);
    }

    @Override
    public String GetValue()
    {
        if (value == null || value.equals(""))
            return "Off";
        else
            return value;
    }

    @Override
    public String[] GetValues()
    {
    return new String[]{"Off","On"};
    }

}
