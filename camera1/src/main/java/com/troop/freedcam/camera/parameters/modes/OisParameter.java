package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 26.05.2015.
 */
public class OisParameter extends BaseModeParameter {
    /**
     * @param uihandler    Holds the ui Thread to invoke the ui from antother thread
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     * @param value        The String to get/set the value from the parameters
     * @param values
     */
    public OisParameter(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value, String values) {
        super(uihandler, parameters, cameraHolder, value, values);
    }

    @Override
    public boolean IsSupported() {
        return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || DeviceUtils.IS(DeviceUtils.Devices.p8lite);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if(DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4))
            parameters.put("ois-ctrl", valueToSet);
        else
            parameters.put("hw_ois_enable", valueToSet);
        baseCameraHolder.SetCameraParameters(parameters);
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
        else
            return new String[] {
                    "on,off"
            };
    }
}
