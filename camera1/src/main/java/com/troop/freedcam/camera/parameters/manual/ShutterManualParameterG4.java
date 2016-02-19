package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameterG4 extends BaseManualParameter
{
    /*M8 Stuff
    //M_SHUTTER_SPEED_MARKER=1/8000,1/1000,1/125,1/15,0.5,4 ???
    //return cameraController.getStringCameraParameter("shutter-threshold");
    */
    private static String TAG = "freedcam.ShutterManualParameterG4";
    I_CameraHolder baseCameraHolder;
    LG_G4AeHandler.AeManualEvent manualevent;

    public ShutterManualParameterG4(HashMap<String, String> parameters, I_CameraHolder baseCameraHolder, AbstractParameterHandler camParametersHandler, LG_G4AeHandler.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler);

        this.baseCameraHolder = baseCameraHolder;
        this.isSupported = true;
        stringvalues = ShutterManualParameter.LGG4Values.split(",");
        this.manualevent =manualevent;
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
    protected void setvalue(int valueToSet)
    {
        if (valueToSet == 0)
        {
            manualevent.onManualChanged(LG_G4AeHandler.AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(LG_G4AeHandler.AeManual.shutter, false, valueToSet);
        }

    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.put("shutter-speed", "0");
        }
        else
        {
            currentInt = value;
            parameters.put("shutter-speed", stringvalues[value]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[value]);
    }


    public Double getMicroSec(String shutterString)
    {
        Double a = Double.parseDouble(shutterString);

        return a * 1000;

    }

    public String FLOATtoSixty4(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000000;
        return String.valueOf(c);
    }


    @Override
    public String GetStringValue()
    {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues()
    {
        return stringvalues;
    }

}