package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

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
    HashMap<String, String> parameters;
    BaseCameraHolder baseCameraHolder;
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
    public BaseModeParameter(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value, String values)
    {
        super(uihandler);
        this.parameters = parameters;
        this.value = value;
        this.values = values;
        this.baseCameraHolder = cameraHolder;
        if (parameters != null && !value.isEmpty() && parameters.containsKey(value) && parameters.containsKey(values))
        {
            String tmp = parameters.get(value);
            if (!tmp.isEmpty())
            {
                this.isSupported = true;
                valuesArray = parameters.get(values).split(",");
                ArrayList<String> tmpl  = new ArrayList<String>();
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

    public void SetValue(String valueToSet,  boolean setToCam)
    {
        if (valueToSet == null)
            return;
        parameters.put(value, valueToSet);
        Logger.d(TAG, "set " + value + " to " + valueToSet);
        BackgroundValueHasChanged(valueToSet);
        if (setToCam) {
            try {
                baseCameraHolder.SetCameraParameters(parameters);

            } catch (Exception ex) {
                Logger.e(TAG, ex.getMessage());
            }
        }
        firststart = false;
    }



    public String GetValue()
    {
        return parameters.get(value);
    }

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
