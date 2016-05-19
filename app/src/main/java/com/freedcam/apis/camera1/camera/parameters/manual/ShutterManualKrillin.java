package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

import java.util.HashMap;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class ShutterManualKrillin extends BaseManualParameter {

    private static String TAG = "freedcam.ShutterManualKrillin";
    private I_CameraHolder baseCameraHolder;
    //AE_Handler_LGG4.AeManualEvent manualevent;
    private String KrillinShutterValues = "Auto,1/30000,1/15000,1/10000,1/8000,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,32";

    public ShutterManualKrillin(Camera.Parameters parameters, I_CameraHolder baseCameraHolder, CamParametersHandler camParametersHandler) {
        super(parameters, "", "", "", camParametersHandler, 1);

        this.baseCameraHolder = baseCameraHolder;
        this.isSupported = true;
        stringvalues = KrillinShutterValues.split(",");
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
    public void SetValue(int valueToSet) {
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-manual-exposure-value", "auto");
        } else {
            currentInt = valueToSet;
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-manual-exposure-value", stringvalues[currentInt]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[valueToSet]);


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