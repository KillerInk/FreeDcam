package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.utils.Logger;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class BaseModeParameter extends AbstractModeParameter implements I_ModuleEvent, AbstractModeParameter.I_ModeParameterEvent {
    protected String value;
    protected String values;
    boolean isSupported = false;
    boolean isVisible = true;
    protected Camera.Parameters  parameters;
    protected CameraHolderApi1 cameraHolderApi1;
    protected boolean firststart = true;
    private static String TAG = BaseModeParameter.class.getSimpleName();

    protected String[] valuesArray;

    /***
     *
     * @param uihandler
     * Holds the ui Thread to invoke the ui from antother thread
     * @param parameters
     * Hold the Camera Parameters
     * @param cameraHolder
     * Hold the camera object
     * @param value
     * The String to get/set the value from the parameters
     * @param values
     * the string to get the values avail/supported for @param value
     */
    public BaseModeParameter(Handler uihandler, Camera.Parameters  parameters, CameraHolderApi1 cameraHolder, String value, String values)
    {
        super(uihandler);
        this.parameters = parameters;
        this.value = value;
        this.values = values;
        this.cameraHolderApi1 = cameraHolder;
        if (parameters != null && !value.isEmpty() && parameters.get(value) != null && parameters.get(values) != null)
        {
            String tmp = parameters.get(value);
            if (!tmp.isEmpty())
            {
                this.isSupported = true;
                valuesArray = parameters.get(values).split(",");
                ArrayList<String> tmpl  = new ArrayList<>();
                for (String s : valuesArray)
                {
                    if (!tmpl.contains(s))
                        tmpl.add(s);
                }
                valuesArray = new String[tmpl.size()];
                tmpl.toArray(valuesArray);
            }
        }
        else
            this.isSupported =false;
        this.isVisible = isSupported;
        Logger.d(TAG, value + ":" +isSupported);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return isVisible;
    }

    @Override
    public void SetValue(String valueToSet,  boolean setToCam)
    {
        if (valueToSet == null)
            return;
        parameters.set(value, valueToSet);
        Logger.d(TAG, "set " + value + " to " + valueToSet);
        BackgroundValueHasChanged(valueToSet);
        if (setToCam) {
            try {
                cameraHolderApi1.SetCameraParameters(parameters);

            } catch (Exception ex) {
                Logger.exception(ex);
            }
        }
        firststart = false;
    }


    @Override
    public String GetValue()
    {
        return parameters.get(value);
    }
    @Override
    public String[] GetValues()
    {
        return valuesArray;
    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }
}
