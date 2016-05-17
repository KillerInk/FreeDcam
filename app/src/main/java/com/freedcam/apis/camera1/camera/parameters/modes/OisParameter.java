package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Handler;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 26.05.2015.
 */
public class OisParameter extends BaseModeParameter {
    /**
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     * @param values
     */
    public OisParameter(Handler uihandler, Camera.Parameters parameters, CameraHolderApi1 cameraHolder, String values) {
        super(uihandler, parameters, cameraHolder, "", "");
    }

    @Override
    public boolean IsSupported() {
        return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || DeviceUtils.IS(DeviceUtils.Devices.p8lite) || DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            parameters.set("ois-ctrl", valueToSet);
        else if (DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5))
            parameters.set("ois", valueToSet);
        else
            parameters.set("hw_ois_enable", valueToSet);
        cameraHolderApi1.SetCameraParameters(parameters);
    }

    @Override
    public String GetValue() {
        return parameters.get("ois-ctrl");
    }

    @Override
    public String[] GetValues() {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
        return new String[] {
                "preview-capture","capture","video","centering-only","centering-off"
        };
        else if(DeviceUtils.IS(DeviceUtils.Devices.XiaomiMI5))
        {
            return new String[] {
                    "enable,disable"
            };
        }
        else
            return new String[] {
                    "on,off"
            };
    }
}
