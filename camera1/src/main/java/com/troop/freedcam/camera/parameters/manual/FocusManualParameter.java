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

        if ((DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2())
        {
            this.isSupported = true;
            this.max_value = null;
            this.value = "manualfocus_step";
            this.min_value = null;
        }
        else if (DeviceUtils.isHTC_M8() || DeviceUtils.isHTC_M9())
        {
            if (!parameters.containsKey("min-focus") || !parameters.containsKey("max-focus") || !parameters.containsKey("focus"))
                return;
            this.isSupported = true;
            this.max_value = "max-focus";
            this.value = "focus";
            this.min_value = "min-focus";
        }
        else if (DeviceUtils.isZTEADV() || DeviceUtils.isRedmiNote() || DeviceUtils.isXiaomiMI3W())
        {
            this.isSupported = true;
            this.max_value = null;
            this.value = "manual-focus-position";
            this.min_value = null;
        }
        /*else if (parameters.containsKey("manual-focus-position") && !DeviceUtils.isZTEADV())
        {
            this.value = "manual-focus-position";
            this.max_value = "min-focus-pos-dac"; // this is like camera2 it returns only the min lens position up to 0
            this.isSupported = true;
        }*/
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
        if (max_value == null)
            return 79;
        else {
            try {
                return Integer.parseInt(parameters.get(max_value));
            } catch (NumberFormatException ex) {
                return 0;
            }
        }

    }
// HTC Focus Step "focus-step"
    @Override
    public int GetMinValue()
    {
        if (min_value == null)
            return -1;
        else
            return Integer.parseInt(parameters.get(min_value));
    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        try {
                return Integer.parseInt(parameters.get(value));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "get ManualFocus value failed");
        }
        return 0;
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if ((DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isG2() || DeviceUtils.isG4())
        {
            if(valueToSet != -1 && !camParametersHandler.FocusMode.GetValue().equals("normal"))
            {
                camParametersHandler.FocusMode.SetValue("normal", true);
            }
            else if (valueToSet == -1)
                camParametersHandler.FocusMode.SetValue("auto", true);
        }
        else if (DeviceUtils.isZTEADV() || DeviceUtils.isXiaomiMI3W() || DeviceUtils.isRedmiNote())
        {
            if(valueToSet != -1)
            {
                camParametersHandler.FocusMode.SetValue("manual", true);
                if (DeviceUtils.isZTEADV())
                    parameters.put("manual-focus-pos-type", "1");
            }
            else
                camParametersHandler.FocusMode.SetValue("auto", true);

        }
        else if (DeviceUtils.isHTC_M8()|| DeviceUtils.isHTC_M9())
        {
            if(valueToSet == -1)
                camParametersHandler.FocusMode.SetValue("auto", true);
        }
        if (value != null && !value.equals("") && valueToSet > -1)
        {
            parameters.put(value, valueToSet+"");
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
