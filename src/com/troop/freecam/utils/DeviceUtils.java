package com.troop.freecam.utils;

import android.os.Build;

/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{
    public static boolean isOmap()
    {
        String s = Build.MODEL;
        return s.equals("Galaxy Nexus") || s.equals("LG-P920") || s.equals("LG-P720") || s.equals("LG-P925") || s.equals("LG-P760") || s.equals("LG-P765") || s.equals("LG-P925") || s.equals("LG-SU760") || s.equals("LG-SU870") || s.equals("Motorola RAZR MAXX") || s.equals("DROID RAZR") || s.equals("DROID 4") || s.equals("GT-I9100G") || s.equals("U9200");
    }

    public static boolean isQualcomm()
    {
        String s = Build.MODEL;
        return s.equals("LG-D800") || s.equals("LG-D802") || s.equals("LG-D803") || s.equals("LG-D820") || s.equals("LG-D821") || s.equals("LG-D801") || s.equals("C6902") || s.equals("C6903") || s.equals("C833") || s.equals("LG803") || s.equals("C6602") || s.equals("C6603") || s.equals("Nexus 4") || s.equals("Nexus 5") || s.equals("SM-N9005") || s.equals("GT-I9505") || s.equals("GT-I9506") || s.equals("LG803") || s.equals("HTC One") || s.equals("LG-F320") || s.equals("LG-F320S") || s.equals("LG-F320K") || s.equals("LG-F320L") || s.equals("LG-VS980") || s.equals("LG-D805");
    }

    public static boolean isTegra()
    {
        String s = Build.MODEL;
        return s.equals("Nexus 7") || s.equals("LG-P880") || s.equals("ZTE-Mimosa X") || s.equals("HTC One X") || s.equals("HTC One X+") || s.equals("LG-P990") || s.equals("EPAD") || s.equals("GT-P7500") || s.equals("GT-P7300");
    }

    public static boolean isExynos()
    {
        String s = Build.MODEL;
        return s.equals("GT-I9000") || s.equals("GT-I9100") || s.equals("GT-I9300") || s.equals("GT-I9500") || s.equals("SM-905") || s.equals("GT-N7000") || s.equals("GT-N7100");
    }

    public static boolean is3d()
    {
        String s = Build.MODEL;
        return s.equals("LG-P920") || s.equals("LG-P720") || s.equals("LG-P925") || s.equals("LG-P925") || s.equals("LG-SU760") || s.equals("LG-SU870");
    }

    public static boolean isTablet()
    {
        String s = Build.MODEL;
        return s.equals("Nexus 7") || s.equals("Nexus 10");
    }

    public static boolean isG2()
    {
        String s = Build.MODEL;
        return s.equals("LG-D800") || s.equals("LG-D801") || s.equals("LG-D802") || s.equals("LG-D803") || s.equals("LG-D804") || s.equals("LG-D805") || s.equals("LG-D820") || s.equals("LG-F320") || s.equals("LG-F320S") || s.equals("LG-F320L") || s.equals("F320K") || s.equals("LG-VS980");
    }
}
