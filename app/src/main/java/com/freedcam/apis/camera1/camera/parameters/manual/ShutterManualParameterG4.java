package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameterG4 extends BaseManualParameter
{
    private static String TAG = "freedcam.ShutterManualParameterG4";
    private AE_Handler_LGG4.AeManualEvent manualevent;

    public ShutterManualParameterG4(Camera.Parameters parameters, CamParametersHandler camParametersHandler, AE_Handler_LGG4.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.isSupported = true;
        stringvalues = parameters.get("shutter-speed-values").replace(",0","").split(",");
        stringvalues[0] = KEYS.AUTO;
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
    public void SetValue(int valueToSet)
    {
        if (valueToSet == 0)
        {
            manualevent.onManualChanged(AE_Handler_LGG4.AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(AE_Handler_LGG4.AeManual.shutter, false, valueToSet);
        }

    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.set("shutter-speed", "0");
        }
        else
        {
            currentInt = value;
            parameters.set("shutter-speed", stringvalues[value]);
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