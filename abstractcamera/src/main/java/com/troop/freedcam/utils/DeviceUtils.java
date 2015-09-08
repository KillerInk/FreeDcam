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

    public static String G2Models = "LG-D800,LG-D801,LG-D802,LG-D803,LG-D804,LG-D805,LG-D820,LG-F320,LG-F320S,LG-F320L,F320K,LG-VS980";
    public static boolean isG2()
    {
        boolean supported = Build.DEVICE.equals("g2");
        if (!supported)
            supported = G2Models.contains(Build.MODEL);
        return supported;
    }

    public static boolean isEvo3d()
    {
        return Build.MODEL.equals("HTC EVO 3D X515m") || Build.MODEL.equals("HTC X515d")|| Build.MODEL.equals("HTC ShooterU")|| Build.MODEL.equals("HTC Shooter");
    }

    public static String M8Models = "HTC One_M8, One M8,HTC One M8,htc_m8";
    public static boolean isHTC_M8()
    {
        boolean supported = Build.DEVICE.equals("htc_m8");
        if (!supported)
            supported = Build.MODEL.equals("HTC One_M8") || Build.MODEL.equals("One M8") || Build.MODEL.equals("HTC One M8") || Build.MODEL.equals("htc_m8");
        return supported;
    }

    public static boolean isHTC_M9()
    {
        String s = Build.MODEL;
        return s.equals("HTC One_M9") || s.equals("HTC One M9") ;
    }

    public static boolean isHTC_M7()
    {
        String s = Build.MODEL;
        return s.equals("HTC One_M7");
    }

    public static boolean isHtc_One_SV()
    {
        return Build.MODEL.contains("HTC One SV");
    }

    public static boolean isHtc_One_XL() { return Build.MODEL.toLowerCase().contains("one xl");}


    public static String G3Models = "LG-D855,LGLS990,LG VS985,LG-D851";
    public static boolean isLG_G3()
    {
        boolean supported = Build.DEVICE.equals("g3");
        if (!supported)
            supported = G3Models.contains(Build.MODEL);
        return supported;
    }


    public static boolean isZTEADV()
    {
        String s = Build.MODEL;
        return s.equals("NX503A") || s.contains("NX") || s.equals("NX403A") || s.contains("Z5s") || s.equals("Z5") || s.contains("NX505") || s.contains("NX506") || s.contains("NX507");
    }

    public static boolean hasIMX135()
    {
        String s = Build.MODEL;
        return s.equals("NX503A") || s.contains("NX501") || s.equals("NX403A") || s.equals("Z5Smini") || s.equals("Z5") || s.contains("NX601");
    }

    public static boolean hasIMX214()
    {
        String s = Build.MODEL;
        return s.contains("NX505") || s.contains("NX506") || s.contains("NX507") || s.contains("A0001");
    }

    public static boolean isXiaomiADV()
    {
        String s = Build.MODEL;
        return s.equals("Aries") || s.equals("cNexus 10");
    }

    static String samsung = "SM-G900V,SM-G900,SM-G900H";
    public static boolean isSamsungADV()
    {
        return samsung.contains(Build.MODEL);
    }

    public static boolean isSonyADV()
    {
        String s = Build.MODEL;
        return s.contains("C66") || s.contains("C69") || s.contains("C65") || s.contains("C64") || s.contains("D65")|| s.contains("D66");
    }

    static String MTK = "P6Life,thl 5000,Philips W8555,MX4";
    public static boolean isMediaTekDevice()
    {
        return MTK.contains(Build.MODEL);
    }

    public static boolean isMeizuMX4()
    {
        return Build.MODEL.equals("MX4");
    }
    public static boolean isTHL5000()
    {
        return Build.MODEL.equals("thl 5000");
    }

    public static boolean isLenovoK910()
    {
        return Build.MODEL.contains("Lenovo K910");
    }

    static String xperiaLModels = "C2104";
    public static  boolean isXperiaL()
    {
        return xperiaLModels.contains(Build.MODEL);
    }

    public static String Yureka = "YUREKA,AO5510,Yureka,A05510";
    public static boolean isYureka()
    {
        boolean supported = Build.DEVICE.equals("YUREKA");
        if (!supported)
            supported = Yureka.contains(Build.MODEL);
        return supported;
    }

    public static boolean isGioneE7() { return Build.MODEL.contains("E7");}

    public static boolean isOnePlusOne(){ return Build.MODEL.equals("A0001");}

    public static boolean isRedmiNote() { return Build.MODEL.equals("HM NOTE 1LTE");}

    public static boolean isXiaomiMI3W() { return Build.MODEL.equals("MI 3W");}


    public static boolean isCamera1DNGSupportedDevice()
    {
        return isLG_G3() || isG2() || isHTC_M8() || isZTEADV() || isHTC_M9() || isHtc_One_SV() || isHtc_One_XL() || isLenovoK910() || isYureka() ||
                isOnePlusOne() || isRedmiNote() || isXiaomiMI3W()|| isXperiaL()||isMeizuMX4() || isTHL5000();
    }

}
