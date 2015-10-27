package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 9/22/2015.
 */
public class HighSpeedVideo extends  BaseModeParameter
{
    BaseCameraHolder baseCameraHolder;

    public HighSpeedVideo(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder)
    {
        super(handler,parameters, parameterChanged, value, values);

        if(DeviceUtils.isZTEADV()||DeviceUtils.isMoto_MSM8974()||DeviceUtils.isMoto_MSM8982_8994()) {
            String tmp = parameters.get("video-hfr");
            if (tmp != null && !tmp.equals("")) {

                this.values = "video-hfr-values";
                this.value = "video-hfr";
            }
            this.isSupported = true;
        }
        else
        {
            this.isSupported = false;
        }

        this.baseCameraHolder = (BaseCameraHolder) baseCameraHolder;
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues()
    {
        if (DeviceUtils.isMoto_MSM8974())
            return new String[] {"off","60"};
        else
            return super.GetValues();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        //baseCameraHolder.StopPreview();
        if (DeviceUtils.isLG_G3())
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
        if (DeviceUtils.isMoto_MSM8974())

        {
            return new String ("off");
        }
        else {
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
}
