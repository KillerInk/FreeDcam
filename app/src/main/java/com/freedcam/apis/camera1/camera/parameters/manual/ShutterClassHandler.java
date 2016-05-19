package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterClassHandler
{

    private String IMX214_IMX230 = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250"+
            ",1/1000,1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65" +
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,1/1.9,1/1.8,1/1.7,1/1.6";

    private String TEST = "Auto,1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250"+
            ",1/1000,1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65" +
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,1/1.9,1,2,3,4,5,6,7,8,9,10,11,12,13,15,16,17,18,19,20";

    private String Mi3WValues = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/85,1/75,1/65"+
            ",1/55,1/45,1/35,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0";
    private String Mi4WValues = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/250,1/125,1/100,1/85,1/75,1/60"+
            ",1/55,1/45,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28,29.0" +
            ",30.0,31.0,32.0,64.0";

    private String OppoIMX214 = "Auto,1/5000,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/250,1/125,1/100,1/85,1/75,1/60"+
            ",1/55,1/45,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2"+
            ",1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0,15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28,29.0" +
            ",30.0,31.0,32.0,33.0,34.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44.0,45.0,46.0,47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57" +
            "58.0,59.0,60.0,61.0,62.0,63.0,64.0";



    public static AbstractManualParameter getShutterClass(Camera.Parameters parameters, CamParametersHandler parametersHandler, I_CameraHolder cameraHolder)
    {
        if (DeviceUtils.IS(DeviceUtils.Devices.SonyADV))
            return new ShutterManualSony(parameters, "","",parametersHandler);

        else if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
           return new  ShutterManualZTE(parameters, "","", cameraHolder, parametersHandler);

        else if(DeviceUtils.IS(DeviceUtils.Devices.ForwardArt_MTK))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");

        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return new ShutterManualParameterHTC(parameters, "","",parametersHandler);

        else if(DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) &&   !parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");

        else if(DeviceUtils.IS(DeviceUtils.Devices.OnePlusOne) &&   parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,null,"exposure-time", "max-exposure-time", "min-exposure-time");

        else if((DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote) ||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W ) || DeviceUtils.IS(DeviceUtils.Devices.LenovoK920))&& parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,null,"exposure-time", "max-exposure-time", "min-exposure-time");

        else if((DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W))&&  parameters.get("max-exposure-time").contains("."))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,null,"exposure-time", "max-exposure-time", "min-exposure-time");

        else if((DeviceUtils.IS(DeviceUtils.Devices.Xiaomi_RedmiNote) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W) || DeviceUtils.IS(DeviceUtils.Devices.LenovoK920))
                && (parameters.get("max-exposure-time") !=null&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");
        else if((DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI_Note_Pro) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W))
                && (parameters.get("max-exposure-time") != null&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");
        else if (DeviceUtils.IsMarshMallowG3())
            return null;
        else if ((parameters.get("max-exposure-time")!= null&& parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_Micro(parameters,parametersHandler,null,"exposure-time", "max-exposure-time", "min-exposure-time");
        else if ((parameters.get("max-exposure-time")!= null&& !parameters.get("max-exposure-time").contains(".")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");
        else if ((parameters.get("shutter-value")!= null&& parameters.get("shutter-value-supported").contains(",")))
            return new ShutterManual_ExposureTime_FloatToSixty(parameters,parametersHandler,null, "max-exposure-time", "min-exposure-time");
        else if (DeviceUtils.IS(DeviceUtils.Devices.p8lite))
            return new ShutterManualKrillin(parameters,cameraHolder,parametersHandler);
        else
            return null;
    }


}
