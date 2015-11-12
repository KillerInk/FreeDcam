package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusHTC";

    public FocusManualParameterHTC(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.baseCameraHolder = cameraHolder;
        if (!parameters.containsKey("min-focus") || !parameters.containsKey("max-focus") || !parameters.containsKey("focus"))
            this.isSupported = false;
        else
            this.isSupported = true;
        this.max_value = "max-focus";
        this.value = "focus";
        this.min_value = "min-focus";
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
            return Integer.parseInt(parameters.get(max_value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public int GetMinValue()
    {
        try {
            return Integer.parseInt(parameters.get(min_value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

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
        if(valueToSet != -1)
        {
            parameters.put(value, valueToSet+"");
            camParametersHandler.SetParametersToCamera();
        }
        else if (valueToSet == -1)
        {
            parameters.put(value, valueToSet+"");
            camParametersHandler.FocusMode.SetValue("auto", true);
        }


    }

}
