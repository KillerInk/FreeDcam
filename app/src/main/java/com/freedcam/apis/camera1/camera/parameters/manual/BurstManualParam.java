package com.freedcam.apis.camera1.camera.parameters.manual;

/**
 * Created by George on 1/21/2015.
 */

import android.hardware.Camera;
import android.os.Build;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

public class BurstManualParam extends BaseManualParameter
{

    final String TAG = BurstManualParam.class.getSimpleName();

    public BurstManualParam(Camera.Parameters parameters, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);

        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G3)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G2)
                || DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4)
                || DeviceUtils.IS(DeviceUtils.Devices.LG_G4)
                || parameters.get(KEYS.NUM_SNAPS_PER_SHUTTER) != null
                || parameters.get(KEYS.SNAPSHOT_BURST_NUM) != null
                || parameters.get(KEYS.BURST_NUM)!= null)
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
            currentInt = 0;
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.OFF);
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
        currentInt = valueToSet;

        if (parameters.get(KEYS.NUM_SNAPS_PER_SHUTTER) != null ||DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.MI3_4))
        {
            if (currentInt == 0)
                parameters.set(KEYS.NUM_SNAPS_PER_SHUTTER, 1+"");
            else
                parameters.set(KEYS.NUM_SNAPS_PER_SHUTTER, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.NUM_SNAPS_PER_SHUTTER+ stringvalues[currentInt]);

        }
        if (parameters.get(KEYS.BURST_NUM)!=null)
        {
            if (currentInt == 0)
                parameters.set(KEYS.SNAPSHOT_BURST_NUM, String.valueOf(0));
            else
                parameters.set(KEYS.SNAPSHOT_BURST_NUM, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.SNAPSHOT_BURST_NUM+ stringvalues[currentInt]);
        }
        else if(parameters.get(KEYS.BURST_NUM) != null)
        {
            if (valueToSet == 0)
                parameters.set(KEYS.BURST_NUM, String.valueOf(0));
            else
                parameters.set(KEYS.BURST_NUM, stringvalues[currentInt]);
            Logger.d(TAG, KEYS.BURST_NUM+ stringvalues[currentInt]);
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
        public void ModuleChanged(String module)
        {
            if ((module.equals(AbstractModuleHandler.MODULE_VIDEO) || module.equals(AbstractModuleHandler.MODULE_HDR)) && isSupported)
                BackgroundIsSupportedChanged(false);
            else if ((module.equals(AbstractModuleHandler.MODULE_PICTURE)
                    || module.equals(AbstractModuleHandler.MODULE_INTERVAL)
                    )&& isSupported)
            {
                BackgroundIsSupportedChanged(true);
            }
        }
    };

}
