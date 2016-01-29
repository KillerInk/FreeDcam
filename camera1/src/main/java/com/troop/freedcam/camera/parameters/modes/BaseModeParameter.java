package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;
import android.util.Log;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class BaseModeParameter extends AbstractModeParameter {
    protected String value;
    protected String values;
    boolean isSupported = false;
    HashMap<String, String> parameters;
    BaseCameraHolder baseCameraHolder;
    protected boolean firststart = true;
    private static String TAG = BaseModeParameter.class.getSimpleName();
    public String IdentifySub = "Ignore";

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
    }

    @Override
    public boolean IsSupported()
    {
        try
        {
            String tmp = parameters.get(values);
            if (!tmp.isEmpty())
                this.isSupported = true;
        }
        catch (Exception ex)
        {
            this.isSupported = false;
        }
        //Log.d(TAG, "is Supported :" + isSupported);
        BackgroundSetIsSupportedHasChanged(isSupported);
        return isSupported;
    }

    public void SetValue(String valueToSet,  boolean setToCam)
    {
        Log.e(TAG,"Index :" + IdentifySub);
        if (valueToSet == null)
            return;
        String tmp = parameters.get(value);
        parameters.put(value, valueToSet);
        Log.d(TAG, "set " + value + " from " + tmp + " to " + valueToSet);

        if(IdentifySub.equals("Ignore")) {

            BackgroundValueHasChanged(valueToSet);
            if (setToCam) {
                try {
                    baseCameraHolder.SetCameraParameters(parameters);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "set " + value + " to " + valueToSet + " failed set back to: " + tmp);
                    if (tmp == null)
                        return;
                    parameters.put(value, tmp);
                    try {
                        baseCameraHolder.SetCameraParameters(parameters);
                        BackgroundValueHasChanged(valueToSet);
                    } catch (Exception ex2) {
                        ex.printStackTrace();
                        Log.e(TAG, "set " + value + " back to " + tmp + " failed");
                    }
                }
            }
        }
        else
        {
            if(DeviceUtils.isHTC_M8()||DeviceUtils.isHTC_M9())
            {
                if(Integer.parseInt(valueToSet) == 60)
                {
                    parameters.put("video-hdr", "false");
                    parameters.put("video-mode", "2");
                    parameters.put("video-hfr", "off");
                }

                if(Integer.parseInt(valueToSet) == 120)
                {
                    parameters.put("video-hdr", "false");
                    parameters.put("video-hfr", ""+120);
                    try
                    {
                        parameters.put("video-hsr", ""+120);
                    }
                    catch (Exception ex)
                    {
                        
                    }
                    parameters.put("slow-motion-version", ""+2);
                    parameters.put("cam-mode", ""+2);
                }




            }
            BackgroundValueHasChanged(IdentifySub);
            if (setToCam) {
                try {
                    baseCameraHolder.SetCameraParameters(parameters);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e(TAG, "set " + value + " to " + IdentifySub + " failed set back to: " + tmp);
                    if (tmp == null)
                        return;
                    parameters.put(value, tmp);
                    try {
                        baseCameraHolder.SetCameraParameters(parameters);
                        BackgroundValueHasChanged(IdentifySub);
                    } catch (Exception ex2) {
                        ex.printStackTrace();
                        Log.e(TAG, "set " + value + " back to " + tmp + " failed");
                    }
                }
            }
        }
        //here


        firststart = false;
    }



    public String GetValue()
    {
        return parameters.get(value);
    }

    public String[] GetValues()
    {
        return parameters.get(values).split(",");
    }
}
