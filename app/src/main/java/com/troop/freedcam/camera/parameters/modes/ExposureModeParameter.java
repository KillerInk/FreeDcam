package com.troop.freedcam.camera.parameters.modes;

import com.troop.freedcam.camera.parameters.I_ParameterChanged;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ExposureModeParameter extends BaseModeParameter {
    public ExposureModeParameter(HashMap<String, String> parameters, I_ParameterChanged parameterChanged, String value, String values) {
        super(parameters,parameterChanged, value, values);

        try
        {
            String tmp = parameters.get("exposure-mode-values");
            if(tmp != null && !tmp.equals("")) {
                isSupported = true;
                this.values = "exposure-mode-values";
                this.value = "exposure";
            }
        }
        catch (Exception ex)
        {

        }
        if (isSupported == false)
        {
            try
            {
                String tmp = parameters.get("auto-exposure-values");
                if(tmp != null && !tmp.equals("")) {
                    isSupported = true;
                    this.values = "auto-exposure-values";
                    this.value = "auto-exposure";
                }
            }
            catch (Exception ex)
            {

            }
        }
        if(!isSupported)
        {
            try {
                String tmp = parameters.get("sony-ae-mode-values");
                if (tmp != null && !tmp.equals("")) {
                    this.value = "sony-ae-mode";
                    this.values = "sony-ae-mode-values";
                    isSupported = true;
                }
            } catch (Exception ex) {}
        }
    }
}
