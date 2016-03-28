package com.troop.freedcam.camera.parameters.manual;

import android.os.Build;

import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 05.03.2016.
 */
public class ManualFocusClassHandler
{
    private static final String manual_focus_position = "manual-focus-position";
    private static final String focusMode_manual = "manual";

    private static final String cur_focus_scale = "cur-focus-scale";
    private static final String max_focus_pos_index =  "max-focus-pos-index";
    private static final String min_focus_pos_index =  "min-focus-pos-index";
    private static final String max_focus_pos_ratio = "max-focus-pos-ratio";
    private static final String min_focus_pos_ratio = "min-focus-pos-ratio";


    public static BaseManualParameter GetManualFocus(HashMap<String, String> parameters, CamParametersHandler parametersHandler, I_CameraHolder cameraHolder)
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES))
        {
            return new BaseFocusManual(parameters,manual_focus_position,0,79,focusMode_manual,parametersHandler,1,1);
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.RedmiNote))
        {
            return new BaseFocusManual(parameters,manual_focus_position,0,79,focusMode_manual,parametersHandler,1,1);
        }
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4C))
        {
            return new BaseFocusManual(parameters,manual_focus_position,0,1000,focusMode_manual,parametersHandler,10,1);
        }
        else if( DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8982_8994))
        {
            return new BaseFocusManual(parameters,manual_focus_position,0,1000,focusMode_manual,parametersHandler,10,2);
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.Alcatel_Idol3))
        {
            return new BaseFocusManual(parameters, cur_focus_scale,max_focus_pos_ratio, min_focus_pos_ratio,focusMode_manual,parametersHandler,1,2);
        }
        else if(DeviceUtils.isLenovoK920())
        {
            return new BaseFocusManual(parameters, manual_focus_position,max_focus_pos_index, min_focus_pos_index,focusMode_manual,parametersHandler,1,1);
        }
        else if(DeviceUtils.IS(DeviceUtils.Devices.SonyM4_QC))
            return new BaseFocusManual(parameters, manual_focus_position,max_focus_pos_index, min_focus_pos_index,focusMode_manual,parametersHandler,1,0);
        else if (DeviceUtils.IsMarshMallowG3())
        {
            return new BaseFocusManual(parameters,manual_focus_position,0,1023,focusMode_manual,parametersHandler,10,1);
        }
        else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || (DeviceUtils.IS(DeviceUtils.Devices.LG_G3) && Build.VERSION.SDK_INT < 21) || DeviceUtils.IS(DeviceUtils.Devices.LG_G2))
            return new FocusManualParameterLG(parameters,"","","", cameraHolder, parametersHandler);
        else if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.HTC_m8_9))
            return new FocusManualParameterHTC(parameters,"","","", cameraHolder,parametersHandler);
        else if(parameters.containsKey("focus-fs-fi-max") && parameters.containsKey("focus-fs-fi-min") && parameters.containsKey("focus-fs-fi"))
            return new FocusManualMTK(parameters,"focus-fs-fi","focus-fs-fi-max","focus-fs-fi-min", focusMode_manual,parametersHandler,10,0);
        else
            return null;

    }
}
