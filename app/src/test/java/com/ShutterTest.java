package com;

import org.junit.Test;

import com.freedcam.apis.basecamera.parameters.manual.AbstractManualShutter;

/**
 * Created by troop on 31.05.2016.
 */
public class ShutterTest
{
    @Test
    public void readSHutter()
    {
        float tmpMin = Float.parseFloat("0.010514")*1000;
        float tmpMax = Float.parseFloat("602.360952")*1000;
        int min = (int)tmpMin;
        int max = (int)tmpMax;
        AbstractManualShutter shutter = new AbstractManualShutter(null);
        String ar[] = shutter.getSupportedShutterValues(min,max,false);
        for (String s : ar) {
            System.out.println("value " + s);
            System.out.println("FormatShutterStringToDouble" +shutter.FormatShutterStringToDouble(s));
            System.out.println("FLOATtoSixty4 " +shutter.FLOATtoSixty4(shutter.FormatShutterStringToDouble(s)));
            System.out.println("getMicroSecFromMilliseconds" +shutter.getMicroSecFromMilliseconds(shutter.FormatShutterStringToDouble(s)));
        }
    }
}
