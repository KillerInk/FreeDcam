package com.troop.freedcam.camera.parameters.modes;

import android.os.Handler;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 05.09.2014.
 */
public class ZeroShutterLagParameter extends BaseModeParameter
{
    I_CameraHolder baseCameraHolder;

    public ZeroShutterLagParameter(Handler handler,HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder) {
        super(handler,parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;

        try
        {
            String zsl = parameters.get("zsl");
            if (zsl != null && !zsl.equals("")) {
                this.value = "zsl";
                this.values = "zsl-values";
                this.isSupported = true;
            }

        }
        catch (Exception ex)
        {
            this.isSupported = false;
        }
        if (!this.isSupported)
        {
            try {
                String zsl = parameters.get("mode-values");
                if (zsl != null && !zsl.equals(""))
                {
                    this.value = "mode";
                    this.values ="mode-values";
                    this.isSupported = true;
                }
            }
            catch (Exception ex)
            {
                this.isSupported = false;
            }
        }
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
        {
            //baseCameraHolder.StopPreview();
            super.SetValue(valueToSet, setToCam);
            //baseCameraHolder.StartPreview();
        }
        else
            super.SetValue(valueToSet, setToCam);
    }
}
