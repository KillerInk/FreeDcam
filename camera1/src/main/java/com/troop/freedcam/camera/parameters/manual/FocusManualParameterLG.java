package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.DeviceUtils.Devices;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterLG extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusG4";

    private final Devices[] g3m_g4 = {Devices.LG_G3, Devices.LG_G4};

    public FocusManualParameterLG(HashMap<String, String> parameters, String value, String maxValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler);
        this.baseCameraHolder = cameraHolder;
        isSupported = true;
        isVisible = isSupported;
}

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetMaxValue()
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(g3m_g4))
            return 60;
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
            if (DeviceUtils.IsMarshMallowG3())
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
