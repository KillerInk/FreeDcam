package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.camera.CameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;

import java.util.HashMap;

/**
 * Created by Ar4eR on 03.01.16.
 */
public class HighSpeedVideo extends  BaseModeParameter {

    private BaseCameraHolder cameraHolder;
    private CameraUiWrapper cameraUiWrapper;

    private final String[] hsr_values = {"off","60","90","120"};
    public HighSpeedVideo(Handler handler, HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, CameraUiWrapper cameraUiWrapper)
    {
        super(handler, parameters, parameterChanged, "", "");

        this.isSupported = true;
        this.value = "video-hsr";
        //this.values="video-hfr-values";
        try {
            String hsr =  parameters.get("");
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
