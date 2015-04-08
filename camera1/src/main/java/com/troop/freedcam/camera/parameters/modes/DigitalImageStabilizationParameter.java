package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;

import java.util.HashMap;

/**
 * Created by troop on 05.09.2014.
 */
public class DigitalImageStabilizationParameter extends  BaseModeParameter {
    I_CameraHolder baseCameraHolder;

    public DigitalImageStabilizationParameter(HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
        try
        {
            String tmp = parameters.get("sony-vs");
            if(tmp != null && !tmp.equals("")) {
                isSupported = true;
                this.values = "sony-vs-values";
                this.value = "sony-vs";
            }
        }
        catch (Exception ex)
        {

        }
        if (isSupported == false)
        {
            try
            {
                String tmp = parameters.get("dis");
                if(tmp != null && !tmp.equals("")) {
                    isSupported = true;
                    this.values = "dis-values";
                    this.value = "dis";
                }
            }
            catch (Exception ex)
            {

            }
        }
    }

    public DigitalImageStabilizationParameter(HashMap<String,String> parameters, BaseCameraHolder parameterChanged, String value, String values, I_CameraHolder baseCameraHolder) {
        super(parameters, parameterChanged, value, values);
        this.baseCameraHolder = baseCameraHolder;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        super.SetValue(valueToSet, setToCam);

    }
}
