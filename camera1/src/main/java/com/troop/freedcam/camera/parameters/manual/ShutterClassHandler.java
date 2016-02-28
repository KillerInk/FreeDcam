package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterClassHandler
{

    public static String IMX214_IMX230 = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250"+
            ",1/1000,1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65" +
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,1/1.9,1/1.8,1/1.7,1/1.6";

    public static String TEST = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250"+
            ",1/1000,1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65" +
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,1/1.9,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,17,18,19,20";

    public static String Mi3WValues = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0";
    public static String Mi4WValues = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0,4.0,8.0,16.0,32.0";

    public static BaseManualParameter getShutterClass(HashMap<String, String> parameters, CamParametersHandler parametersHandler, I_CameraHolder cameraHolder)
    {
        if (DeviceUtils.IS(DeviceUtils.Devices.SonyADV))
            return new ShutterManualSony(parameters,"","","",parametersHandler);
        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
           return new  ShutterManualZTE(parameters,"","","", cameraHolder, parametersHandler);
        //else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
          //  return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,Mi3WValues.split(","));

        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return new ShutterManualParameterHTC(parameters,"","","",parametersHandler);
        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994) ||DeviceUtils.IS(DeviceUtils.Devices.SonyM4_QC))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,TEST.split(","));
        else if(DeviceUtils.IS(DeviceUtils.Devices.RedmiNote) || DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null);
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W)&& Build.VERSION.SDK_INT < 23)
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,Mi3WValues.split(","));
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W)&& Build.VERSION.SDK_INT < 23)
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,Mi4WValues.split(","));
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W)&& Build.VERSION.SDK_INT >= 23)
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,Mi3WValues.split(","));
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W)&& Build.VERSION.SDK_INT >= 23)
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,Mi4WValues.split(","));
        else
            return null;
    }
}
