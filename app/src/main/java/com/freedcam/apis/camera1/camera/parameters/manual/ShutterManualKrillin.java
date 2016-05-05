package com.freedcam.apis.camera1.camera.parameters.manual;


import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class ShutterManualKrillin extends BaseManualParameter {

    private static String TAG = "freedcam.ShutterManualKrillin";
    private I_CameraHolder baseCameraHolder;
    //AE_Handler_LGG4.AeManualEvent manualevent;

    public ShutterManualKrillin(HashMap<String, String> parameters, I_CameraHolder baseCameraHolder, AbstractParameterHandler camParametersHandler) {
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
            manualevent.onManualChanged(AE_Handler_LGG4.AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(AE_Handler_LGG4.AeManual.shutter, false, valueToSet);
        }*/
        setValue(valueToSet);


    }

    private void setValue(int value) {

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