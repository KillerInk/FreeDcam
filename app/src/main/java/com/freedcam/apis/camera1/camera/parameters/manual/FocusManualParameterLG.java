package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterLG extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private static String TAG =FocusManualParameterLG.class.getSimpleName();

    public FocusManualParameterLG(Camera.Parameters parameters, I_CameraHolder cameraHolder, ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler,1);
        this.baseCameraHolder = cameraHolder;
        isSupported = true;
        isVisible = isSupported;
        if (isSupported)
        {
            int max = 0;
            step = 1;
            if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                max = 60;
            else
                max = 79;
            stringvalues = createStringArray(0,max,step);
        }

    }


    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet != 0)
        {
            if (!parametersHandler.FocusMode.GetValue().equals(KEYS.FOCUS_MODE_NORMAL)) {
                parametersHandler.FocusMode.SetValue(KEYS.FOCUS_MODE_NORMAL, true);
            }
            parameters.set(KEYS.MANUALFOCUS_STEP, stringvalues[valueToSet]);
            parametersHandler.SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
        }


    }

    @Override
    public String GetStringValue()
    {
        if (parametersHandler.FocusMode.GetValue().equals(KEYS.AUTO))
            return KEYS.AUTO;
        else
            return GetValue()+"";
    }
}
