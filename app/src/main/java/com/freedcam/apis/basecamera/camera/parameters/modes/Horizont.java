package com.freedcam.apis.basecamera.camera.parameters.modes;


/**
 * Created by Ar4eR on 14.01.16.
 */
public class Horizont extends AbstractModeParameter {

    private String value;

    public Horizont() {
        super();
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
