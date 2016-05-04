package com.freedcam.apis.camera1.camera.parameters.manual;



import com.freedcam.apis.i_camera.interfaces.I_CameraHolder;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterLG extends  BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private static String TAG ="freedcam.ManualFocusG4";

    private final DeviceUtils.Devices[] g3m_g4 = {DeviceUtils.Devices.LG_G3, DeviceUtils.Devices.LG_G4};

    public FocusManualParameterLG(HashMap<String, String> parameters, String maxValue, String MinValue, I_CameraHolder cameraHolder, AbstractParameterHandler camParametersHandler) {
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
    protected void setvalue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet != 0)
        {
            if (!camParametersHandler.FocusMode.GetValue().equals("normal")) {
                camParametersHandler.FocusMode.SetValue("normal", true);
            }
            parameters.put("manualfocus_step", stringvalues[valueToSet]);
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
