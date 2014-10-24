package com.troop.freecamv2.camera.parameters.manual;

import android.hardware.Camera;

import com.troop.freecamv2.utils.DeviceUtils;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameter extends BaseManualParameter
{
    public static String HTCShutterValues = "4.0,3.2,2.5,1.6,1.3,1.0,0.8,0.6,0.5,0.4,0.3,0.25,0.2,0.125,0.1,0.07,0.06,0.05,0.04,0.03,0.025,0.02,0.015,0.0125,0.01,1/125"+
            ",1/200,1/250,1/300,1/400,1/500,1/640,1/800,1/1000,1/1250,1/1600,1/2000,1/2500,1/3200,1/4000,1/5000,1/6400,1/8000";

    String shutterValues[];
    int current = 0;

    public ShutterManualParameter(Camera.Parameters parameters, String value, String maxValue, String MinValue) {
        super(parameters, value, maxValue, MinValue);


        if (DeviceUtils.isHTCADV())
        {
            this.isSupported = true;
            shutterValues = HTCShutterValues.split(",");
        }
        //TODO add missing logic
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public int GetMaxValue() {
        return shutterValues.length;
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
    public void SetValue(int valueToSet)
    {
        if (DeviceUtils.isHTCADV())
        {
            String shutterstring = shutterValues[valueToSet];
            if (shutterstring.contains("/"))
            {
                String split[] = shutterstring.split("/");
                float a = Float.parseFloat(split[0])/ Float.parseFloat(split[1]);
                shutterstring = ""+a;
            }
            parameters.set("shutter", shutterstring);
        }
    }


    public String GetStringValue()
    {
        return shutterValues[current];
    }
}
