package com.troop.freedcam.camera.parameters.manual;

import android.util.Log;

import com.troop.freedcam.i_camera.interfaces.I_CameraChangedListner;
import com.troop.freedcam.i_camera.interfaces.I_CameraHolder;
import com.troop.freedcam.i_camera.interfaces.I_Shutter_Changed;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.utils.DeviceUtils;
import com.troop.freedcam.utils.StringUtils;

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
    String shutterValues[];
    private int current = 0;
    I_CameraHolder baseCameraHolder;
    LG_G4AeHandler.AeManualEvent manualevent;

    public ShutterManualParameterG4(HashMap<String, String> parameters, I_CameraHolder baseCameraHolder, AbstractParameterHandler camParametersHandler, LG_G4AeHandler.AeManualEvent manualevent) {
        super(parameters, "", "", "", camParametersHandler);

        this.baseCameraHolder = baseCameraHolder;
        this.isSupported = true;
        shutterValues = ShutterManualParameter.LGG4Values.split(",");
        this.manualevent =manualevent;
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public int GetMaxValue() {
            return shutterValues.length-1;
    }

    @Override
    public int GetMinValue() {
            return 0;
    }

    @Override
    public int GetValue() {
        return current;
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
            current = value;
            parameters.put("shutter-speed", shutterValues[value]);
        }
        ThrowCurrentValueStringCHanged(shutterValues[value]);
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
        return shutterValues[current];
    }

    @Override
    public String[] getStringValues()
    {
        return shutterValues;
    }

    @Override
    public void RestartPreview()
    {

    }
}