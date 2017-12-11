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

package freed.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ingo on 04.10.2014.
 */
public class StringUtils
{


    public static String VoLP = "Vol+";
    public static String VoLM = "Vol-";
    public static String Hook = "Hook";

    private StringUtils(){}

    public static String GetExternalSDCARD() throws NullPointerException
    {
        return System.getenv("SECONDARY_STORAGE");
    }

    public static String GetInternalSDCARD()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String TrimmFloatString4Places(String toTrim)
    {
        return String.format("%01.4f", Float.parseFloat(toTrim));
    }

    public static String freedcamFolder = "/DCIM/FreeDcam/";
    public static String DCIMFolder = "/DCIM/";

    public static String GetFreeDcamConfigFolder = GetInternalSDCARD()+ freedcamFolder +"config/";

    public class FileEnding
    {
        public static final String RAW = "raw";
        public static final String DNG = "dng";
        public static final String JPG = "jpg";
        public static final String JPS = "jps";
        public static final String BAYER = "bayer";
        public static final String MP4 = "mp4";

        public String GetWithDot()
        {
            return "."+ BAYER;
        }
    }

    private void EstimatedRecordingTimeLeft(int VB, int AB)
    {
        int i = VB / 2;

        long l2 = (i + AB >> 3) / 1000;
        // long l3 = Environment.getExternalStorageDirectory().getUsableSpace() / l2;
        Log.d("VideoCamera Remaing", getTimeString(Environment.getExternalStorageDirectory().getUsableSpace() / l2)) ;

    }

    private String getTimeString(long paramLong)
    {
        long l1 = paramLong / 1000L;
        long l2 = l1 / 60L;
        long l3 = l2 / 60L;
        long l4 = l2 - 60L * l3;
        String str1 = Long.toString(l1 - 60L * l2);
        if (str1.length() < 2) {
            str1 = "0" + str1;
        }
        String str2 = Long.toString(l4);
        if (str2.length() < 2) {
            str2 = "0" + str2;
        }
        String str3 = str2 + ":" + str1;
        if (l3 > 0L)
        {
            String str4 = Long.toString(l3);
            if (str4.length() < 2) {
                str4 = "0" + str4;
            }
            str3 = str4 + ":" + str3;
        }
        return str3;
    }


    public static String[] IntHashmapToStringArray(HashMap<String, Integer> hashMap)
    {
        String[] t = new String[hashMap.entrySet().size()];
        int i = 0;
        for (Map.Entry set:hashMap.entrySet())
        {
            t[i++] = set.getKey()+";"+set.getValue();
        }
        return t;
    }

    public static HashMap<String, Integer> StringArrayToIntHashmap(String[] t)
    {
        try {
            HashMap<String, Integer> out = new HashMap<>();
            for (String e : t)
            {
                if (!TextUtils.isEmpty(e)) {
                    String[] en = e.split(";");
                    out.put(en[0], Integer.parseInt(en[1]));
                }
            }
            return out;
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            Log.WriteEx(ex);
            return null;
        }
    }

    public static String getMeterString(float f)
    {
        String ret = "";
        f= f*1000;
        int t = (int)f;
        if (t > 1000) {
            int meter = t / 1000;
            int cm = t - meter*1000;
            if (meter > 1000)
                ret = "∞";
            else if (meter > 3)
                ret = meter +"m";
            else
                ret = meter + "." + cm +"m";
        }
        else {
            int cm = t /10;
            int mm = t -cm*10;
            ret = cm + "." + mm+ "cm";
        }

        return ret;
    }

    public static boolean arrayContainsString(String[] ar, String name)
    {
        for (int i =0; i< ar.length;i++)
        {
            if (name.equals(ar[i]))
                return true;
        }
        return false;
    }

}
