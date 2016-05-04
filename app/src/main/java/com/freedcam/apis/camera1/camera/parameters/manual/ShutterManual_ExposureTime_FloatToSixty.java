package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_FloatToSixty extends ShutterManual_ExposureTime_Micro
{

    private final static String TAG = ShutterManual_ExposureTime_FloatToSixty.class.getSimpleName();
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_FloatToSixty(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues, String max, String min) {
        super(parameters, camParametersHandler, shuttervalues, "exposure-time", "max-exposure-time", "min-exposure-time");
    }

    @Override
    protected void setvalue(int valueToset)
    {
        currentInt = valueToset;
        if(!stringvalues[currentInt].equals("Auto"))
        {
            String shutterstring = StringUtils.FormatShutterStringToDouble(stringvalues[currentInt]);
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = StringUtils.FLOATtoSixty4(shutterstring);
            parameters.put("exposure-time", shutterstring);
        }
        else
        {
            parameters.put("exposure-time", "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
