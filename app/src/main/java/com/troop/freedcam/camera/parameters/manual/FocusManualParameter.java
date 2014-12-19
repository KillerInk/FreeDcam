package com.troop.freedcam.camera.parameters.manual;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameter extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    String TAG ="freedcam.ManualFocus";
    public FocusManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        //TODO add missing logic
    }
    public FocusManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isLGADV() || DeviceUtils.isZTEADV() || DeviceUtils.isHTC_M8())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue()
    {
        try {
            if (DeviceUtils.isLGADV() || DeviceUtils.isZTEADV())
                return 79;
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
        return 0;
    }
//m8 Step Value
    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (DeviceUtils.isLGADV())
                i = parameters.getInt("manualfocus_step");
            if (DeviceUtils.isZTEADV());
                i = parameters.getInt("maf_key");
            if (DeviceUtils.isHTC_M8())
                i = parameters.getInt("focus");
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
        //baseCameraHolder.GetCamera().cancelAutoFocus();
        /*if (!parameters.getFocusMode().equals("manual-focus"))
        {
            parameters.set("manual-focus", 0);
            parameters.setFocusMode("normal");
        }*/
        //parameters.set("manual", 0);
        //parameters.setFocusAreas(null);
        if (DeviceUtils.isLGADV())
        {
            parameters.setFocusAreas(null);
            parameters.setFocusMode("normal");
            //baseCameraHolder.GetCamera().setParameters(parameters);
            parameters.set("manualfocus_step", valueToSet);
            //baseCameraHolder.GetCamera().setParameters(parameters);
        }
        if (DeviceUtils.isZTEADV())
        {
            //parameters.setFocusMode("macro");
            parameters.set("maf_key", valueToSet);
        }
        if (DeviceUtils.isHTC_M8())
        {
            parameters.set("focus", valueToSet);
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
