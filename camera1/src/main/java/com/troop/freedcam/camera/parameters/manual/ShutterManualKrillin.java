package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class ShutterManualKrillin extends BaseManualParameter {

    private static String TAG = "freedcam.ShutterManualKrillin";
    I_CameraHolder baseCameraHolder;
    //LG_G4AeHandler.AeManualEvent manualevent;

    public ShutterManualKrillin(HashMap<String, String> parameters, I_CameraHolder baseCameraHolder, AbstractParameterHandler camParametersHandler, LG_G4AeHandler.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler, 1);

        this.baseCameraHolder = baseCameraHolder;
        this.isSupported = true;
        stringvalues = ShutterManualParameter.KrillinShutterValues.split(",");
        // this.manualevent =manualevent;
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return super.IsSupported();
    }


    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    protected void setvalue(int valueToSet) {
        /*if (valueToSet == 0)
        {
            manualevent.onManualChanged(LG_G4AeHandler.AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(LG_G4AeHandler.AeManual.shutter, false, valueToSet);
        }*/
        setValue(valueToSet);


    }

    public void setValue(int value) {

        if (value == 0) {
            parameters.put("hw-hwcamera-flag", "on");
            parameters.put("hw-manual-exposure-value", "auto");
        } else {
            currentInt = value;
            parameters.put("hw-hwcamera-flag", "on");
            parameters.put("hw-manual-exposure-value", stringvalues[value]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[value]);
    }


    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }
}