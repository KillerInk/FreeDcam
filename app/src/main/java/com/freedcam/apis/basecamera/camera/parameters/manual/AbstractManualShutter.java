package com.freedcam.apis.basecamera.camera.parameters.manual;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;

import java.util.ArrayList;

/**
 * Created by troop on 19.05.2016.
 */
public class AbstractManualShutter extends AbstractManualParameter
{

    private String shutterValues = "auto,1/90000,1/75000,1/50000,1/45000,1/30000,1/20000,1/12000,1/10000"+
            ",1/8000,1/6400,1/5000,1/4000,1/3200,1/2500,1/2000,1/1600,1/1250,1/1000"+
            ",1/800,1/700,1/600,1/500,1/400,1/300,1/200,1/125,1/100,1/80,1/70,1/60"+
            ",1/50,1/40,1/35,1/30,1/25,1/20,1/15,1/13,1/10,1/9,1/8,1/7,1/6,1/5,1/4,1/3,1/2,0.6,0.8"+
            ",1.0,1.2,1.4,1.5,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0,11.0,12.0,13.0,14.0"+
            ",15.0,16.0,17.0,18.0,19.0,20.0,21.0,22.0,23.0,24.0,25.0,26.0,27.0,28.0,29.0"+
            ",30.0,31.0,32.0,33.0,35.0,36.0,37.0,38.0,39.0,40.0,41.0,42.0,43.0,44,45.0,46.0"+
            ",47.0,48.0,49.0,50.0,51.0,52.0,53.0,54.0,55.0,56.0,57.0,58.0,59.0,60.0,120.0,240.0";

    public AbstractManualShutter(AbstractParameterHandler camParametersHandler) {
        super(camParametersHandler);
    }

    public String[] getSupportedShutterValues(int minMillisec, int maxMiliisec, boolean withautomode)
    {
        final String[] allvalues = shutterValues.split(",");
        boolean foundmin = false, foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(KEYS.AUTO);
        for (int i = 1; i< allvalues.length; i++ )
        {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String split[] = s.split("/");
                a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
            }
            else
                a = (Float.parseFloat(s)*1000000f);

            if (a>= minMillisec && a <= maxMiliisec)
                tmp.add(s);
            if (a >= minMillisec && !foundmin)
            {
                foundmin = true;
            }
            if (a > maxMiliisec && !foundmax)
            {
                foundmax = true;
            }
            if (foundmax && foundmin)
                break;

        }
        return tmp.toArray(new String[tmp.size()]);
    }

    /**
     * Checks if the the string looks like 1/50 and if yes it gets formated to double
     * @param shutterstring
     * @return
     */
    public String FormatShutterStringToDouble(String shutterstring)
    {
        if (shutterstring.contains("/")) {
            String split[] = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a;
        }
        return shutterstring;
    }

    /**
     *
     * @param shutterString 693.863262
     * @return 693863.262
     */
    public String getMicroSecFromMilliseconds(String shutterString)
    {
        return (Double.parseDouble(shutterString) * 1000)+"";
    }

    public String FLOATtoSixty4(String a)
    {
        float b =  Float.parseFloat(a);
        float c = b * 1000000;
        int d = Math.round(c);
        return String.valueOf(d);
    }

    public static long getMilliSecondStringFromShutterString(String shuttervalue)
    {
        float a;
        if (shuttervalue.contains("/")) {
            String split[] = shuttervalue.split("/");
            a =(Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f);
        }
        else
            a = (Float.parseFloat(shuttervalue)*1000000f);
        a = Math.round(a);
        return  (long)a;
    }
}
