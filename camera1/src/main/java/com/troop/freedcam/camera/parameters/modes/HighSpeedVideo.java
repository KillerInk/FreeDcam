package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by Ar4eR on 03.01.16.
 */
public class HighSpeedVideo extends  BaseModeParameter {

    BaseCameraHolder cameraHolder;
    CameraUiWrapper cameraUiWrapper;

    final String[] hsr_values = {"off","60","90","120"};
    public HighSpeedVideo(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, CameraUiWrapper cameraUiWrapper)
    {
        super(handler, parameters, parameterChanged, value, values);

        this.isSupported = true;
        this.value = "video-hsr";
        //this.values="video-hfr-values";
        try {
            String hsr =  parameters.get(value);
            if (hsr == null || hsr.equals(""))
                this.isSupported = false;
        }
        catch (Exception ex) {
            this.isSupported = false;
        }
        this.cameraHolder = parameterChanged;
        this.cameraUiWrapper = cameraUiWrapper;

    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues()
    {
        return hsr_values;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
        if (cameraUiWrapper.moduleHandler.GetCurrentModule() != null && cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
            cameraUiWrapper.moduleHandler.GetCurrentModule().LoadNeededParameters();
    }

    @Override
    public String GetValue()
    {
        String tmp = parameters.get(value);
        if ( tmp == null || tmp == "")
            return "off";
        else
            return (parameters.get(value));
    }
}
