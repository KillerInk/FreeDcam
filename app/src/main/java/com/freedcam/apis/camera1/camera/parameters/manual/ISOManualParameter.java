package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;

public class ISOManualParameter extends BaseManualParameter {

    public ISOManualParameter(Camera.Parameters parameters, ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler,1);

        this.isSupported = true;
        this.key_max_value = KEYS.MIN_ISO;
        this.key_value = KEYS.ISO;
        this.key_min_value = KEYS.MAX_ISO;
        if (parameters.get(key_max_value) != null && parameters.get(key_min_value) != null) {

            if (key_min_value.equals(null)) {
                this.isSupported = false;
            }
            stringvalues = createStringArray(Integer.parseInt(parameters.get(key_min_value)), Integer.parseInt(parameters.get(key_max_value)), 100);
        }
        else
            isSupported = false;

        isVisible = isSupported;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public int GetValue() {
        final String tmp = parameters.get(key_value);
        if (tmp.equals(KEYS.AUTO))
            return 0;
        try {
            return Integer.parseInt(parameters.get(key_value));
        } catch (NullPointerException | NumberFormatException ex) {
            return 0;
        }
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        parameters.set(KEYS.ISO, stringvalues[valueToSet]);
    }

}


