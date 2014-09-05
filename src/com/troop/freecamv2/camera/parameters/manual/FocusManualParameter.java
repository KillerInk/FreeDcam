package com.troop.freecamv2.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freecam.utils.DeviceUtils;
import com.troop.freecamv2.camera.BaseCameraHolder;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameter extends  BaseManualParameter
{
    BaseCameraHolder baseCameraHolder;
    public FocusManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);

        //TODO add missing logic
    }
    public FocusManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue, BaseCameraHolder cameraHolder) {
        super(parameters, value, maxValue, MinValue);

        this.baseCameraHolder = cameraHolder;
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported()
    {
        if (DeviceUtils.isLGADV())
            return true;
        else
            return false;
    }

    @Override
    public int GetMaxValue() {
        return 80;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            i = parameters.getInt("manualfocus_step");
        }
        catch (Exception ex)
        {
            parameters.set("manual-focus", 0);
        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        baseCameraHolder.GetCamera().cancelAutoFocus();
        /*if (!parameters.getFocusMode().equals("manual-focus"))
        {
            parameters.set("manual-focus", 0);
            parameters.setFocusMode("normal");
        }*/
        //parameters.set("manual", 0);
        parameters.setFocusAreas(null);
        parameters.setFocusMode("normal");
        parameters.set("manualfocus_step", valueToSet);

    }
}
