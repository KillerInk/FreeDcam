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
public class FocusManualParameterLG extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusG4";

    public FocusManualParameterLG(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);
        this.baseCameraHolder = cameraHolder;
        isSupported = true;
}

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetMaxValue()
    {
        if (DeviceUtils.isG4())
            return 60;
        else if (DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return 102;
        else
            return 79;
    }

    @Override
    public int GetMinValue()
    {
        return -1;
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
        if(valueToSet != 0)
        {
            if (DeviceUtils.isLG_G3() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (!camParametersHandler.FocusMode.GetValue().equals("manual")) {
                    camParametersHandler.FocusMode.SetValue("manual", true);
                    parameters.put("manual-focus-pos-type", "1");
                }
                parameters.put("manual-focus-position", (valueToSet *10) + "");
            }
            else
            {
                if (!camParametersHandler.FocusMode.GetValue().equals("normal")) {
                    camParametersHandler.FocusMode.SetValue("normal", true);

                }
                parameters.put("manualfocus_step", (valueToSet - 1) + "");
            }
            camParametersHandler.SetParametersToCamera();
        }
        else if (valueToSet == 0)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }


    }

    @Override
    public String GetStringValue()
    {
        if (camParametersHandler.FocusMode.GetValue().equals("Auto"))
            return "Auto";
        else
            return GetValue()+"";
    }
}
