package com.troop.freecamv2.camera.parameters.modes;

import android.hardware.Camera;

import com.troop.freecamv2.camera.parameters.I_ParameterChanged;

/**
 * Created by troop on 17.08.2014.
 */
public class IsoModeParameter extends BaseModeParameter
{
    public IsoModeParameter(Camera.Parameters parameters, I_ParameterChanged parameterChanged, String value, String values)
    {
        super(parameters, parameterChanged, value, values);
        try
        {
            String isomodes = parameters.get("iso-mode-values");
            if (isomodes != null && !isomodes.equals("")) {
                this.value = "iso";
                this.values = "iso-mode-values";
                isSupported = true;
            }
        }
        catch (Exception ex){}
        if (!isSupported)
        {
            try {
                String isomodes = parameters.get("iso-values");
                if (isomodes != null && !isomodes.equals("")) {
                    this.value = "iso";
                    this.values = "iso-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}
        }
    }

}
