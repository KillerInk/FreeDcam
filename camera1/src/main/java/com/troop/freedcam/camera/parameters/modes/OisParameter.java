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
        return DeviceUtils.isLG_G3() || DeviceUtils.isG2();
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
