package com.freedcam.apis.camera1.camera.parameters.manual;

import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_Micro extends BaseManualParameter
{
    private final String TAG = ShutterManual_ExposureTime_Micro.class.getSimpleName();
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_Micro(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues, String value, String maxval , String minval ) {
        super(parameters, value, maxval, minval, camParametersHandler,1);
        try {
            if (shuttervalues == null)
            {
                if (!parameters.get(minval).contains("."))
                {
                    int min = Integer.parseInt(parameters.get(min_value));
                    int max = Integer.parseInt(parameters.get(max_value));
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }
                else
                {
                    double tmpMin = Double.parseDouble(parameters.get(min_value))*1000000;
                    double tmpMax = Double.parseDouble(parameters.get(max_value))*1000000;
                    int min = (int)tmpMin;
                    int max = (int)tmpMax;
                    stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                }

            }
            else
                stringvalues = shuttervalues;
            parameters.put(value, "0");
            this.isSupported = true;

        } catch (NumberFormatException ex) {
            Logger.exception(ex);
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
            Logger.d(TAG, "StringUtils.FormatShutterStringToDouble:" + shutterstring);
            shutterstring = StringUtils.getMicroSec(shutterstring);
            Logger.d(TAG, " StringUtils.getMicroSec"+ shutterstring);
            parameters.put(value, shutterstring);
        }
        else
        {
            parameters.put(value, "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
