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
public class FocusManualParameterG4 extends  BaseManualParameter
{
    I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusG4";

    public FocusManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
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
        return 100;
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
        if(valueToSet != -1)
        {
            camParametersHandler.FocusMode.SetValue("normal", true);
            parameters.put("manualfocus_step", valueToSet+"");
        }
        else if (valueToSet == -1)
        {
            camParametersHandler.FocusMode.SetValue("auto", true);
        }
        camParametersHandler.SetParametersToCamera();

    }

}
