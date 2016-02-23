package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.utils.DeviceUtils;

import java.util.HashMap;

/**
 * Created by troop on 22.11.2014.
 */
public class VideoHDRModeParameter extends  BaseModeParameter
{
    BaseCameraHolder baseCameraHolder;

    public VideoHDRModeParameter(Handler handler,HashMap<String, String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder)
    {
        super(handler,parameters, parameterChanged, value, values);

        try
        {
            String tmp = parameters.get("sony-video-hdr");
            if(tmp != null && !tmp.equals("")) {
                this.isSupported = true;
                this.values = "sony-video-hdr-values";
                this.value = "sony-video-hdr";
            }
        }
        catch (Exception ex)
        {

        }
        if (isSupported == false)
        {
            try
            {
                String tmp = parameters.get("video-hdr");
                if((tmp != null && !tmp.equals(""))) {
                    this.isSupported = true;
                    this.values = "video-hdr-values";
                    this.value = "video-hdr";
                }
            }
            catch (Exception ex)
            {

            }
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
        return super.GetValues();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);
    }

    @Override
    public String GetValue()
    {
        String ret = super.GetValue();
        return ret;
    }
}
