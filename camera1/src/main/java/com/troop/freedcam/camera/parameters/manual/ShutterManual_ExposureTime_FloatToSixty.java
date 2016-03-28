package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManual_ExposureTime_FloatToSixty extends ShutterManual_ExposureTime_Micro
{

    final static String TAG = ShutterManual_ExposureTime_FloatToSixty.class.getSimpleName();
    /**
     * @param parameters
     * @param camParametersHandler
     */
    public ShutterManual_ExposureTime_FloatToSixty(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler, String[] shuttervalues) {
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
            Logger.d(TAG, "StringUtils.FLOATtoSixty4:" + shutterstring);
            // MTK Takes int for shutter speed seems to have three Values Preview Shutter speed Manual shutter speed and capture shutter speed
            //m-ss
            //cap-ss these seems to visble on on sony C5 and meizu mx4 can be used for reverse look for exif data same as iso using gain
            if (DeviceUtils.isSonyM5_MTK() || DeviceUtils.isSonyC5_MTK() || DeviceUtils.IS(DeviceUtils.Devices.MeizuMX4_MTK) || DeviceUtils.IS(DeviceUtils.Devices.MeizuMX5_MTK))
                parameters.put("cap-ss", shutterstring);
            else if(DeviceUtils.IS(DeviceUtils.Devices.ForwardArt_MTK)) {
                parameters.put("eng-preview-shutter-speed", shutterstring);
                parameters.put("eng-capture-shutter-speed", shutterstring);
            }

            else
                parameters.put("exposure-time", shutterstring);
        }
        else
        {
            if (DeviceUtils.isSonyM5_MTK() || DeviceUtils.isSonyC5_MTK() || DeviceUtils.IS(DeviceUtils.Devices.MeizuMX4_MTK) || DeviceUtils.IS(DeviceUtils.Devices.MeizuMX5_MTK))
                parameters.put("cap-ss", "0");
            else if(DeviceUtils.IS(DeviceUtils.Devices.ForwardArt_MTK)) {
                parameters.put("eng-preview-shutter-speed", 0 + "");
                parameters.put("eng-capture-shutter-speed", 0 + "");
            }
            else
                parameters.put("exposure-time", "0");
            Logger.d(TAG, "set exposure time to auto");
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }
}
