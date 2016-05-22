package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 12.04.2015.
 */
public class CDS_Mode_Parameter extends BaseModeParameter
{
    final String TAG = CDS_Mode_Parameter.class.getSimpleName();
    final String[] cds_values = {"auto", "on", "off"};
    public CDS_Mode_Parameter(Handler handler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, String value)
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
            Logger.exception(ex);
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
        parameters.set("cds-mode", valueToSet);
        try {
            cameraHolderApi1.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        firststart = false;
    }
}
