package com.troop.freedcam.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

/**
 * Created by troop on 05.09.2014.
 */
public class ZeroShutterLagParameter extends BaseModeParameter
{
    I_CameraHolder baseCameraHolder;
    public ZeroShutterLagParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters, parameterChanged, value, values);
    }

    public ZeroShutterLagParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values, I_CameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;

        try
        {
            String zsl = parameters.get("zsl");
            if (zsl != null && zsl != "") {
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
                if (zsl != null && zsl != "")
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
