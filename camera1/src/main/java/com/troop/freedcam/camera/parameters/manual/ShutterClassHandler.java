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
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/250,1/125,1/100,1/85,1/75,1/60"+
            ",1/55,1/45,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28,29.0" +
            ",30.0,31.0,32.0,64.0";

    public static String OppoIMX214 = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/250,1/125,1/100,1/85,1/75,1/60"+
            ",1/55,1/45,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28,29.0" +
            ",30.0,31.0,32.0,33.0,34.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44.0,45.0,46.0,47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57" +
            "58.0,59.0,60.0,61.0,62.0,63.0,64.0";



    public static BaseManualParameter getShutterClass(HashMap<String, String> parameters, CamParametersHandler parametersHandler, I_CameraHolder cameraHolder)
    {
        if (DeviceUtils.IS(DeviceUtils.Devices.SonyADV))
            return new ShutterManualSony(parameters,"","","",parametersHandler);
        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
           return new  ShutterManualZTE(parameters,"","","", cameraHolder, parametersHandler);

        //else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
           // return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,OppoIMX214.split(","));

        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return new ShutterManualParameterHTC(parameters,"","","",parametersHandler);

        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.AlcatelIdol3_Moto_MSM8982_8994) ||DeviceUtils.IS(DeviceUtils.Devices.SonyM4_QC))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,TEST.split(","));

        else if(DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) &&   !parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,OppoIMX214.split(","));

        else if( DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) &&   parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,OppoIMX214.split(","));

        else if((DeviceUtils.IS(DeviceUtils.Devices.RedmiNote) ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W ) || DeviceUtils.IS(DeviceUtils.Devices.LenovoK920))&& parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,Mi3WValues.split(","));

        else if((DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W))&&  parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,Mi4WValues.split(","));

        else if((DeviceUtils.IS(DeviceUtils.Devices.RedmiNote) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W) || DeviceUtils.IS(DeviceUtils.Devices.LenovoK920))
                && (parameters.containsKey("max-exposure-time")&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,Mi3WValues.split(","));

        else if((DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W))
                && (parameters.containsKey("max-exposure-time")&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,Mi4WValues.split(","));
        else if (DeviceUtils.IsMarshMallowG3())
            return null;
        else if ((parameters.containsKey("max-exposure-time")&& parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,null);
        else if ((parameters.containsKey("max-exposure-time")&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null);
        else if (parameters.containsKey("m-ss"))
            return new ShutterManualMtk(parameters, parametersHandler,TEST.split(","));
        else
            return null;
    }


}
