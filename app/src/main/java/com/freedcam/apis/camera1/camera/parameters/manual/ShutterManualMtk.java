package com.freedcam.apis.camera1.camera.parameters.manual;


import android.hardware.Camera;

import com.freedcam.apis.basecamera.camera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.camera1.camera.parameters.CamParametersHandler;

import java.util.HashMap;

/**
 * Created by troop on 28.03.2016.
 */
public class ShutterManualMtk extends BaseManualParameter
{
    private static String TAG = ShutterManualMtk.class.getSimpleName();
    private AE_Handler_MTK.AeManualEvent manualevent;

    private String MTKShutter = "Auto,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,1,2";

    public ShutterManualMtk(Camera.Parameters parameters, CamParametersHandler camParametersHandler, AE_Handler_MTK.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler,1);
        this.isSupported = true;
        stringvalues = MTKShutter.split(",");
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
            manualevent.onManualChanged(AE_Handler_MTK.AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(AE_Handler_MTK.AeManual.shutter, false, valueToSet);
        }

    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.set("m-ss", "0");
        }
        else
        {
            String shutterstring = stringvalues[value];
            if (shutterstring.contains("/")) {
                String split[] = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            currentInt = value;
            parameters.set("m-ss", FLOATtoThirty(shutterstring));
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

    private String FLOATtoThirty(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000;
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
