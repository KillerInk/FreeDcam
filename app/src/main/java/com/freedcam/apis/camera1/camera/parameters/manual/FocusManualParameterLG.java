package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterLG extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusG4";

    public FocusManualParameterLG(Camera.Parameters parameters, I_CameraHolder cameraHolder, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);
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
            if (!camParametersHandler.FocusMode.GetValue().equals("normal")) {
                camParametersHandler.FocusMode.SetValue("normal", true);
            }
            parameters.set("manualfocus_step", stringvalues[valueToSet]);
            camParametersHandler.SetParametersToCamera(parameters);
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
