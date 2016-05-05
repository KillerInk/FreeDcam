package com.freedcam.apis.camera1.camera.parameters.manual;

import android.os.Build;

import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.StringUtils;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameter extends BaseManualParameter
{
    /*M8 Stuff
    //M_SHUTTER_SPEED_MARKER=1/8000,1/1000,1/125,1/15,0.5,4 ???
    //return cameraController.getStringCameraParameter("shutter-threshold");
    */
    private static String TAG = "freedcam.ShutterManualParameter";
    private Double Cur;

    public static String LGG4Values = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30";

    public static String KrillinShutterValues = "Auto,1/30000,1/15000,1/10000,1/8000,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,32";

    public static String xIMX214_IMX230 = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,1/1.9,1/1.8,1/1.7,1/1.6,1/1.5,1/1.4,1";




    public ShutterManualParameter(HashMap<String, String> parameters, String value, String maxValue, String MinValue, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler,1);
         if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994) || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
        {
            this.isSupported = true;
            stringvalues = ShutterClassHandler.IMX214_IMX230.split(",");
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W) )
        {
            this.isSupported = true;
            stringvalues = ShutterClassHandler.Mi3WValues.split(",");
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W) )
        {
            this.isSupported = true;
            stringvalues = ShutterClassHandler.Mi4WValues.split(",");
        }
        else if (parameters.containsKey("exposure-time") || DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote)) {
            try {

                int min = Integer.parseInt(parameters.get("min-exposure-time"));
                int max = Integer.parseInt(parameters.get("max-exposure-time"));
                stringvalues = StringUtils.getSupportedShutterValues(min, max, true);
                this.isSupported = true;

            } catch (NumberFormatException ex) {
                Logger.exception(ex);
                isSupported = false;
            }
        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    protected void setvalue(int valueToSet)
    {
        if ( parameters.containsKey("exposure-time") || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994)||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
        {
            currentInt = valueToSet;
            String shutterstring = stringvalues[currentInt];
            if (shutterstring.contains("/")) {
                String split[] = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
                Cur = a;

            }
            if(!stringvalues[currentInt].equals("Auto"))
            {
                try {
                    shutterstring = setExposureTimeToParameter(shutterstring);
                }
                catch (Exception ex)
                {
                    Logger.d("Freedcam", "Shutter Set FAil");
                }
            }
            else
            {
                setShutterToAuto();
            }
            Logger.e(TAG, shutterstring);
        }
        else
        {
            parameters.put("exposure-time", valueToSet + "");
            camParametersHandler.SetParametersToCamera(parameters);
        }
    }

    private void setShutterToAuto() {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994)||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.QC_Manual_New))
        {
            parameters.put("exposure-time", "0");
        }
        else if (parameters.containsKey("exposure-time"))
            parameters.put("exposure-time", 0+"");
        camParametersHandler.SetParametersToCamera(parameters);
    }

    private String setExposureTimeToParameter(String shutterstring) {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994))
        {
            try {
                parameters.put("exposure-time", String.valueOf(StringUtils.getMicroSec(shutterstring)));
            }
            catch (Exception ex)
            {
                System.out.println("Freedcam Manual Exposure Time Error Hal Rejected ");
            }

        }
        else if(parameters.containsKey("exposure-time")||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) || DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote))
        {
            if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)&& Build.VERSION.SDK_INT < 23) {
                shutterstring = StringUtils.FLOATtoSixty4(shutterstring);
                parameters.put("exposure-time", shutterstring);
            }
            else
                parameters.put("exposure-time", String.valueOf(StringUtils.getMicroSec(shutterstring)));
        }
        camParametersHandler.SetParametersToCamera(parameters);
        return shutterstring;
    }





    @Override
    public String[] getStringValues()
    {
        return stringvalues;
    }
}
