package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;
import android.util.Log;

import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends BaseManualParameter
{
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_Micro(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues) {
        super(parameters, "", "", "", camParametersHandler);
        try {
            if (shuttervalues == null) {
                int min = Integer.parseInt(parameters.get("min-exposure-time"));
                int max = Integer.parseInt(parameters.get("max-exposure-time"));
                stringvalues = StringUtils.getSupportedShutterValues(min, max);

            }
            else
                stringvalues = shuttervalues;
            parameters.put("exposure-time", "0");
            this.isSupported = true;

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals("Auto"))
        {
            String shutterstring = StringUtils.FormatShutterStringToDouble(stringvalues[currentInt]);
            parameters.put("exposure-time", StringUtils.getMicroSec(shutterstring));
        }
        else
        {
            parameters.put("exposure-time", "0");
        }
        camParametersHandler.SetParametersToCamera();
    }
}
