package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;

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
    public static String MEDIAN = "median";

    private String current = AVARAGE;
    /***
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     * @param value        The String to get/set the value from the parameters
     * @param values
     */
    public StackModeParameter(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, String value, String values) {
        super(uihandler, parameters, cameraHolder, value, values);
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
        return new String[] {AVARAGE, AVARAGE1x2, AVARAGE1x3, AVARAGE3x3, LIGHTEN,MEDIAN };
    }
}
