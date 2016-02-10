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
     */
    public OisParameter(Handler uihandler, HashMap<String, String> parameters, BaseCameraHolder cameraHolder, String value) {
        super(uihandler, parameters, cameraHolder, "", "");
    }

    @Override
    public boolean IsSupported() {
        return DeviceUtils.IS_DEVICE_ONEOF(DeviceUtils.LG_G2_3) || DeviceUtils.IS(DeviceUtils.Devices.LG_G4);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.put("ois-ctrl", valueToSet);
        baseCameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public String GetValue() {
        return parameters.get("ois-ctrl");
    }

    @Override
    public String[] GetValues() {
        return new String[] {
                "preview-capture","capture","video","centering-only","centering-off"
        };
    }
}
