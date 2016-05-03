package com.troop.freedcam.camera.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import android.os.Build;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class BurstManualParam extends BaseManualParameter {

    BaseCameraHolder baseCameraHolder;
    public BurstManualParam(HashMap<String, String> parameters, AbstractParameterHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G3)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G2)
                || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G4)
                || parameters.containsKey("num-snaps-per-shutter")
                || parameters.containsKey("snapshot-burst-num")
                || parameters.containsKey("burst-num"))
        {
            isSupported = true;
            int max = 10;
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES) || DeviceUtils.IS(DeviceUtils.Devices.LG_G2))
                max =  7;
            else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G3)||DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI4W) )
                max =  9;
            else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI3W))
                if (Build.VERSION.SDK_INT < 23)
                    max =  6;
                else
                    max =  10;
            else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                max =  6;
            stringvalues = createStringArray(2,max,1);
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add("off");
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)|| parameters.containsKey("num-snaps-per-shutter"))
            parameters.put("num-snaps-per-shutter", String.valueOf(1));
        currentInt = valueToSet;

        if(!parameters.containsKey("burst-num")){

            if (valueToSet == 0)
            parameters.put("snapshot-burst-num", String.valueOf(0));
        else
            parameters.put("snapshot-burst-num", stringvalues[valueToSet]);
        }
        else
        {
            if (valueToSet == 0)
                parameters.put("burst-num", String.valueOf(0));
            else
                parameters.put("burst-num", stringvalues[valueToSet]);
        }

        camParametersHandler.SetParametersToCamera(parameters);

    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }

    @Override
    public I_ModuleEvent GetModuleListner() {
        return moduleListner;
    }

    private I_ModuleEvent moduleListner =new I_ModuleEvent() {
        @Override
        public String ModuleChanged(String module)
        {
            if ((module.equals(AbstractModuleHandler.MODULE_VIDEO) || module.equals(AbstractModuleHandler.MODULE_HDR)) && isSupported)
                BackgroundIsSupportedChanged(false);
            else if ((module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    )&& isSupported)
            {
                BackgroundIsSupportedChanged(true);
            }
            return null;
        }
    };

}
