package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by Ingo on 12.04.2015.
 */
public class CDS_Mode_Parameter extends BaseModeParameter
{
    private final String[] cds_values = {"auto", "on", "off"};
    public CDS_Mode_Parameter(Handler handler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value)
    {
        super(handler,parameters, cameraHolder, "", "");
        try {
            final String cds = parameters.get("cds-mode");
            if (cds != null && !cds.equals(""))
            {
                this.isSupported = true;
            }
        }
        catch (Exception ex)
        {

        }
        if (!this.isSupported)
        {
            if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.ZTE_DEVICES)|| DeviceUtils.IS(DeviceUtils.Devices.Htc_M9) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
                this.isSupported = true;
        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {
        return cds_values;
    }

    @Override
    public String GetValue()
    {
        final String cds = parameters.get("cds-mode");
        if (cds != null && !cds.equals(""))
            return cds;
        else
            return "off";
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.put("cds-mode", valueToSet);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        firststart = false;
    }
}
