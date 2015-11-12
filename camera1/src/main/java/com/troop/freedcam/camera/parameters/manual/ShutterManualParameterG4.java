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
    int current = 0;
    I_CameraHolder baseCameraHolder;
    I_CameraChangedListner i_cameraChangedListner;

    public ShutterManualParameterG4(HashMap<String, String> parameters, String value, String maxValue, String MinValue, I_CameraHolder baseCameraHolder, I_CameraChangedListner i_cameraChangedListner, AbstractParameterHandler camParametersHandler) {
        super(parameters, value, maxValue, MinValue, camParametersHandler);

        this.baseCameraHolder = baseCameraHolder;
        this.i_cameraChangedListner = i_cameraChangedListner;

            this.isSupported = true;
            shutterValues = ShutterManualParameter.LGG4Values.split(",");

    }

    private I_Shutter_Changed i_shutter_changed;

    public void setTheListener(I_Shutter_Changed i_shutter_changedx) {
        i_shutter_changed = i_shutter_changedx;

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
            parameters.put("lg-manual-mode-reset", "0");
            parameters.put("shutter-speed", "-1000");
            parameters.put("lg-iso", "-1000");
        }
        else
        {
            parameters.put("lg-manual-mode-reset", "1");
            parameters.put("shutter-speed", shutterValues[valueToSet]);
        }
        baseCameraHolder.SetCameraParameters(parameters);
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