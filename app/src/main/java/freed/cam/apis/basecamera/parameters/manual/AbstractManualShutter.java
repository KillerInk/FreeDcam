/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.basecamera.parameters.manual;

import com.troop.freedcam.R;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;

/**
 * Created by troop on 19.05.2016.
 */
public abstract class AbstractManualShutter extends AbstractParameter
{
    protected AbstractManualShutter(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
    }

    public String[] getSupportedShutterValues(int minMillisec, long maxMiliisec, boolean withautomode)
    {
        String[] allvalues = cameraUiWrapper.getContext().getResources().getStringArray(R.array.shutter_values_autocreate);
        boolean foundmin = false;
        boolean foundmax = false;
        ArrayList<String> tmp = new ArrayList<>();
        if (withautomode)
            tmp.add(cameraUiWrapper.getResString(R.string.auto_));
        for (int i = 1; i< allvalues.length; i++ )
        {
            String s = allvalues[i];

            float a;
            if (s.contains("/")) {
                String[] split = s.split("/");
                a = Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f;
            }
            else
                a = Float.parseFloat(s)*1000000f;

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
            String[] split = shutterstring.split("/");
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
        return Double.parseDouble(shutterString) * 1000 +"";
    }

    public String FLOATtoSixty4(String a)
    {
        float b =  Float.parseFloat(a);
        float c = b * 1000000;
        int d = Math.round(c);
        return String.valueOf(d);
    }

    public String OnePlus(String a)
    {
        float b =  Float.parseFloat(a);
        float c = b * 1000;
        int d = Math.round(c);
        return String.valueOf(d);
    }

    public static long getMilliSecondStringFromShutterString(String shuttervalue)
    {
        float a;
        if (shuttervalue.contains("/")) {
            String[] split = shuttervalue.split("/");
            a = Float.parseFloat(split[0]) / Float.parseFloat(split[1])*1000000f;
        }
        else
            a = Float.parseFloat(shuttervalue)*1000000f;
        a = Math.round(a);
        return  (long)a;
    }
}
