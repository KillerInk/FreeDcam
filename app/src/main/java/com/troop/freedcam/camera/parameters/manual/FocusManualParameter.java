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
    String TAG ="freedcam.ManualFocus";
    public FocusManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public FocusManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported()
    {
        if ((DeviceUtils.isLGADV() /*&& Build.VERSION.SDK_INT < 21*/) || DeviceUtils.isZTEADV() || DeviceUtils.isHTC_M8())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue()
    {
        try {
            if ((DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21) || DeviceUtils.isZTEADV())
                return 79;
            /*if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT >= 21)
                return parameters.getInt("max-focus-pos-index");*/
            if (DeviceUtils.isHTC_M8())
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
    	if (DeviceUtils.isHTC_M8())
            return Integer.parseInt(parameters.get("min-focus"));
        /*if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT >= 21)
            return parameters.getInt("min-focus-pos-index");*/
        return 0;
    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isLGADV()&& Build.VERSION.SDK_INT < 21)
                i = Integer.parseInt(parameters.get("manualfocus_step"));
            if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT >= 21)
                i=  Integer.parseInt(parameters.get("focus-pos"));
            if (DeviceUtils.isZTEADV());
                i = Integer.parseInt(parameters.get("maf_key"));
            if (DeviceUtils.isHTC_M8())
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
        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT < 21)
        {
            camParametersHandler.FocusMode.SetValue("normal", true);
        }
        if (DeviceUtils.isLGADV() && Build.VERSION.SDK_INT >= 21)
        {
            camParametersHandler.FocusMode.SetValue("manual", true);
            parameters.put("focus-pos", valueToSet + "");
        }
        if (DeviceUtils.isZTEADV())
        {
            //parameters.setFocusMode("macro");
            parameters.put("maf_key", valueToSet + "");
        }
        if (DeviceUtils.isHTC_M8())
        {
            parameters.put("focus", valueToSet + "");
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
