package com.troop.freedcam.camera.parameters.manual;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 28.03.2016.
 */
public class ShutterManualMtk extends ShutterManual_ExposureTime_FloatToSixty {

    /**
     * @param parameters
     * @param camParametersHandler
     * @param shuttervalues
     */
    public ShutterManualMtk(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues) {
        super(parameters, camParametersHandler, shuttervalues);
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
            Logger.d(TAG, "StringUtils.FLOATtoSixty4:"+ shutterstring);
            //parameters.put("cap-ss", shutterstring);
            parameters.put("eng-ae-enable","disable"); // not sure if it disables ae or if enable enables eng mode override so ae can be controlled
            parameters.put("m-ss", shutterstring);
        }
        else
        {
            parameters.put("eng-ae-enable","enable");

            parameters.put("m-ss", "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
