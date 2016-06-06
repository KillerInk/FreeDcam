package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.utils.DeviceUtils;

public class ISOManualParameter extends BaseManualParameter {

    public ISOManualParameter(Camera.Parameters parameters, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler,1);

        this.isSupported = true;
        this.max_value = KEYS.MIN_ISO;
        this.value = KEYS.ISO;
        this.min_value = KEYS.MAX_ISO;
        if (parameters.get(max_value) != null && parameters.get(min_value) != null) {

            if (min_value.equals(null)) {
                this.isSupported = false;
            }
            stringvalues = createStringArray(Integer.parseInt(parameters.get(min_value)), Integer.parseInt(parameters.get(max_value)), 100);
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
        final String tmp = parameters.get(value);
        if (tmp.equals(KEYS.AUTO))
            return 0;
        try {
            return Integer.parseInt(parameters.get(value));
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


