package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameter extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocus";

    public FocusManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;

        if (((DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2()) ||
                DeviceUtils.isZTEADV() ||
                DeviceUtils.isHTC_M8()||
                DeviceUtils.isHTC_M9())
            this.isSupported = true;
        else if (parameters.containsKey("manual-focus-position"))
        {
            this.value = "manual-focus-position";
            this.max_value = "min-focus-pos-dac"; // this is like camera2 it returns only the min lens position up to 0
        }
        else
            this.isSupported = false;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetMaxValue()
    {
        try {
            if (max_value != null || !max_value.equals(""))
                return Integer.parseInt(parameters.get(max_value));
            else if ((DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2() || DeviceUtils.isZTEADV())
                return 79;
            /*if (DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT >= 21)
                return parameters.getInt("max-focus-pos-index");*/
            else if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
                return Integer.parseInt(parameters.get("max-focus"));
            else return 0;
        }
        catch (Exception ex)
        {
            Log.e(TAG, "get ManualFocus max value failed");
        }
        return 0;
    }
// HTC Focus Step "focus-step"
    @Override
    public int GetMinValue() {
    	if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
            return Integer.parseInt(parameters.get("min-focus"));
        if (value != null || !value.equals(""))
            return 0;
        /*if (DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT >= 21)
            return parameters.getInt("min-focus-pos-index");*/
        return -1;
    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (value != null || !value.equals(""))
                i = Integer.parseInt(parameters.get(value));
            else if ((DeviceUtils.isLG_G3()&& Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2())
                i = Integer.parseInt(parameters.get("manualfocus_step"));
            else if (DeviceUtils.isZTEADV())
                i = -1;
            else if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
                i = Integer.parseInt(parameters.get("focus"));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "get ManualFocus value failed");
        }

        return i;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if (value != null || !value.equals(""))
        {
            parameters.put(value, valueToSet+"");
        }
        else if ((DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2())
        {
            camParametersHandler.FocusMode.SetValue("normal", true);
            parameters.put("manualfocus_step", valueToSet+"");
        }
        else if (DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT >= 21 && !DeviceUtils.isG2())
        {
            camParametersHandler.FocusMode.SetValue("manual", true);
            parameters.put("focus-pos", valueToSet + "");
        }
        else if (DeviceUtils.isZTEADV())
        {
            if(valueToSet != -1)
            {
                camParametersHandler.FocusMode.SetValue("manual", true);
                parameters.put("manual-focus-pos-type", "1");
                parameters.put("manual-focus-position", String.valueOf(valueToSet));
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);

        }
        else if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
        {
            if(valueToSet != -1)
            {
                parameters.put("focus", valueToSet + "");
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);
        }
        camParametersHandler.SetParametersToCamera();

    }
    /* HTC M8 value -1 disable mf
     *  May have to use this key "non-zsl-manual-mode" set to true
     * 
     * 
     * 
     * 
     */
}
