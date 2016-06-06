package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;

import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;
import com.freedcam.apis.KEYS;

import java.util.ArrayList;

/**
 * Created by GeorgeKiarie on 6/2/2016.
 */
public class BaseISOManual extends BaseManualParameter {

    private String cur_iso_mode = KEYS.AUTO;

    public BaseISOManual(Camera.Parameters parameters, String value, int min, int max
            , CamParametersHandler camParametersHandler, float step) {
        super(parameters, value, "", "", camParametersHandler, step);
        this.isSupported = true;
        this.isVisible = true;
        this.stringvalues = createStringArray(min,max,step);
    }

    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet) {
        currentInt = valueToSet;
        //set to auto
        if (currentInt == 0) {
            set_to_auto();
        } else //set manual wb mode and key_value
        {
            set_manual();
        }
        camParametersHandler.SetParametersToCamera(parameters);
    }


    protected void set_manual()
    {
        cur_iso_mode = camParametersHandler.IsoMode.GetValue();

        if (!camParametersHandler.IsoMode.GetValue().equals(KEYS.KEY_MANUAL_FOCUS_POSITION))
            camParametersHandler.FocusMode.SetValue(KEYS.KEY_MANUAL_FOCUS_POSITION, true);
        parameters.set(key_value, stringvalues[currentInt]);


    }

    protected void set_to_auto()
    {
        camParametersHandler.FocusMode.SetValue(cur_iso_mode, true);

    }


    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
        ArrayList<String> t = new ArrayList<>();
        t.add(KEYS.AUTO);
        for (int i = min; i<=max;i+=step)
        {
            t.add(i+"");
        }
        stringvalues = new String[t.size()];
        t.toArray(stringvalues);
        return stringvalues;
    }
}
