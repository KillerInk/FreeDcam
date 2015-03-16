package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import troop.com.androiddng.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 22.11.2014.
 */
public class VideoHDRModeParameter extends  BaseModeParameter
{
    BaseCameraHolder baseCameraHolder;

    public VideoHDRModeParameter(HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder)
    {
        super(parameters, parameterChanged, value, values);
        if (DeviceUtils.isLGADV())
            this.value = "hdr-mode";
        this.baseCameraHolder = (BaseCameraHolder) baseCameraHolder;
    }

    @Override
    public String[] GetValues()
    {
        if (DeviceUtils.isLGADV())
            return new String[] {"off","on", "auto" };
        else
            return super.GetValues();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //baseCameraHolder.StopPreview();
        if (DeviceUtils.isLGADV())
        {
            if (valueToSet.equals("off"))
                super.SetValue("0", setToCam);
            if (valueToSet.equals("on"))
                super.SetValue("1", setToCam);
            if (valueToSet.equals("auto"))
                super.SetValue("2", setToCam);
        }
        else
            super.SetValue(valueToSet, setToCam);
        //baseCameraHolder.StartPreview();
    }

    @Override
    public String GetValue()
    {
        String ret = super.GetValue();
        if (ret == null || ret == "")
            ret = "off";
        else if (ret.equals("0"))
            ret = "off";
        else if (ret.equals("1"))
            ret = "on";
        else if (ret.equals("2"))
            ret = "auto";


        return ret;
    }
}
