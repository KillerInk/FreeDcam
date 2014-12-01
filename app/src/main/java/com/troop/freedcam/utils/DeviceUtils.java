package com.troop.freedcam.utils;

import android.os.Build;


/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{
    private static int lgeadv = 0;


    public static String Omap4Devices = "Galaxy Nexus,LG-P920,LG-P720,LG-P925,LG-P760,LG-P765,LG-P925,LG-SU760,LG-SU870,Motorola RAZR MAXX,DROID RAZR,DROID 4,GT-I9100G,U9200";
    public static boolean isOmap()
    {
        return Omap4Devices.contains(Build.MODEL);
    }

    public static boolean isQualcomm()
    {
        String s = Build.MODEL;
        return s.equals("LG-D800") || isEvo3d() || s.equals("LG-D802") || s.equals("LG-D803") || s.equals("LG-D820") || s.equals("LG-D821") || s.equals("LG-D801") || s.equals("C6902") || s.equals("C6903") || s.equals("C833") || s.equals("LG803") || s.equals("C6602") || s.equals("C6603") || s.equals("Nexus 4") || s.equals("Nexus 5") || s.equals("SM-N9005") || s.equals("GT-I9505") || s.equals("GT-I9506") || s.equals("LG803") || s.equals("HTC One") || s.equals("LG-F320") || s.equals("LG-F320S") || s.equals("LG-F320K") || s.equals("LG-F320L") || s.equals("LG-VS980") || s.equals("HTC One_M8") || s.equals("NX503A")|| s.equals("Z5S");
    }

    public static boolean isTegra()
    {
        String s = Build.MODEL;
        return s.equals("Nexus 7") || s.equals("LG-P880") || s.equals("ZTE-Mimosa X") || s.equals("HTC One X") || s.equals("HTC One X+") || s.equals("LG-P990") || s.equals("EPAD") || s.equals("GT-P7500") || s.equals("GT-P7300");
    }

    public static boolean isExynos()
    {
        String s = Build.MODEL;
        return s.equals("GT-I9000") || s.equals("GT-I9100") || s.equals("GT-I9300") || s.equals("GT-I9500") || s.equals("SM-905") || s.equals("GT-N7000") || s.equals("GT-N7100")|| s.equals("SM-G900H");
    }

    public static boolean isO3d()
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

    public static boolean isEvo3d()
    {
        return Build.MODEL.equals("HTC EVO 3D X515m") || Build.MODEL.equals("HTC X515d")|| Build.MODEL.equals("HTC ShooterU")|| Build.MODEL.equals("HTC Shooter");
    }

    public static boolean isHTC_M8()
    {
        String s = Build.MODEL;
        return s.equals("HTC One_M8");
    }

    public static boolean isHTC_M7()
    {
        String s = Build.MODEL;
        return s.equals("HTC One_M7");
    }


    public static String G3Models = "LG-D800,LG-D802,LG-D855,LGLS990";
    public static boolean isLGADV()
    {
        return G3Models.contains(Build.MODEL);
    }

    public static boolean getBoolFromInt(int i)
    {
        if (i == 1)
            return true;
        else
            return false;
    }

    public static boolean isZTEADV()
    {
        String s = Build.MODEL;
        return s.equals("NX503A") || s.equals("Z5S") || s.equals("NX403A") || s.equals("Z5Smini") || s.equals("Z5") || s.equals("NX507J");
    }

    public static boolean isXiaomiADV()
    {
        String s = Build.MODEL;
        return s.equals("Aries") || s.equals("cNexus 10");
    }

    public static boolean isSamsungADV()
    {
        String s = Build.MODEL;
        return s.equals("SM-G900") || s.equals("SM-G900H");
    }

    public static boolean isForcedDragon()
    {
        String s = Build.MODEL;
        return s.equals("C6603") || s.equals("C6902") || s.equals("C6602") || s.equals("C6903") || s.equals("HTC One");
    }

    public static boolean isMediaTekTHL5000()
    {
        return Build.MODEL.equals("thl 5000");
    }

    public static boolean isRawSupported()
    {
        return isLGADV() || isHTC_M8() || isZTEADV() || isMediaTekTHL5000();
    }


    static String xperiaLModels = "C2104";
    public static  boolean isXperiaL()
    {
        return xperiaLModels.contains(Build.MODEL);
    }

}
