package com.troop.freedcam.camera.parameters.manual;

import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

import java.util.HashMap;

/**
 * Created by troop on 28.03.2016.
 */
public class ShutterManualMtk extends BaseManualParameter
{
    /*M8 Stuff
    //M_SHUTTER_SPEED_MARKER=1/8000,1/1000,1/125,1/15,0.5,4 ???
    //return cameraController.getStringCameraParameter("shutter-threshold");
    */
    private static String TAG = ShutterManualMtk.class.getSimpleName();
    private I_CameraHolder baseCameraHolder;
    private AE_Handler_MTK.AeManualEvent manualevent;

    public ShutterManualMtk(HashMap<String, String> parameters, I_CameraHolder baseCameraHolder, AbstractParameterHandler camParametersHandler, AE_Handler_MTK.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler,1);

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
            parameters.put("m-ss", "0");
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
            parameters.put("m-ss", FLOATtoThirty(shutterstring));
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
