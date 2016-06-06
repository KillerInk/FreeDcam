package com.freedcam.apis.camera1.camera.parameters.modes;

import android.os.Build;

/**
 * Created by Ingo on 15.05.2016.
 */
public class StackModeParameter extends BaseModeParameter
{
    public static String AVARAGE = "avarage";
    public static String AVARAGE1x2 = "avarage1x2";
    public static String AVARAGE1x3 = "avarage1x3";
    public static String AVARAGE3x3 = "avarage3x3";
    public static String LIGHTEN = "lighten";
    public static String LIGHTEN_V = "lighten_v";
    public static String MEDIAN = "median";

    private String current = AVARAGE;

    public StackModeParameter() {
        super(null, null, "", null);
    }

    @Override
    public boolean IsSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public boolean IsVisible() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        current = valueToSet;
    }

    @Override
    public String GetValue() {
        return current;
    }

    @Override
    public String[] GetValues() {
        return new String[] {AVARAGE, AVARAGE1x2, AVARAGE1x3, AVARAGE3x3, LIGHTEN, LIGHTEN_V,MEDIAN };
    }
}
