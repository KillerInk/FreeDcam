package com.troop.freedcam.utils;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.abstractcamera.R;


/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{
    public static Context contex;

    private static boolean isDevice(String[] ar)
    {
        boolean supported = false;
        for (String s : ar)
        {
            if (s.equals(Build.MODEL))
            {
                supported = true;
                break;
            }
        }
        return supported;
    }

    private static boolean containsDevice(String[] ar)
    {
        boolean supported = false;
        for (String s : ar)
        {
            if (Build.MODEL.contains(s))
            {
                supported = true;
                break;
            }
        }
        return supported;
    }

    public static boolean isCamera1DNGSupportedDevice()
    {
        return isLG_G3() || isG2() || isHTC_M8() || isZTEADV() || isZTEADVIMX214() || isZTEADV234() || isHTC_M9() || isHtc_One_SV() || isHtc_One_XL() || isLenovoK910() || isYureka() ||
                isOnePlusOne() || isRedmiNote() || isXiaomiMI3W()|| isXperiaL() ||isXiaomiMI_Note_Pro() || isVivo_Xplay3s();
    }

    public static boolean isMoto_MSM8974()
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX2k14));
    }

    public static boolean isMoto_MSM8982_8994()
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX_Style_Pure_Play));
    }

    public static boolean isG4()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.g4));
    }

    public static boolean isG2()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.g2));
    }

    public static boolean isEvo3d()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.evo3d));
    }

    public static boolean isHTC_M8()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.htc_m8));
    }

    public static boolean isHTC_M9()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.htc_m9));
    }

    public static boolean isHtc_One_SV()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Htc_One_SV));
    }

    public static boolean isHtc_One_XL() { return Build.MODEL.toLowerCase().contains("one xl");}

    public static boolean isLG_G3()
    {
        return isDevice(contex.getResources().getStringArray(R.array.g3));
    }


    public static boolean isZTEADV()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadv));
    }

    public static boolean isZTEADVIMX214()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX214));
    }

    public static boolean isZTEADV234()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX234));
    }

    public static boolean hasIMX135()
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx135));
    }

    public static boolean hasIMX214()
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx214));
    }

    public static boolean isSonyADV()
    {
        return containsDevice(contex.getResources().getStringArray(R.array.sony_adv));
    }

    public static boolean isLenovoK910()
    {
        return isDevice(contex.getResources().getStringArray(R.array.LenovoK910));
    }

    public static boolean isSonyM5_MTK()
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyM5));
    }


    public static  boolean isXperiaL()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Sony_XperiaL));
    }

    public static boolean isYureka()
    {
        return isDevice(contex.getResources().getStringArray(R.array.YuYureka));

    }

    public static boolean isGioneE7() {return isDevice(contex.getResources().getStringArray(R.array.GioneE7));}

    public static boolean isOnePlusOne(){ return isDevice(contex.getResources().getStringArray(R.array.OnePlusOne));}

    public static boolean isRedmiNote() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote));}

    public static boolean isXiaomiMI3W() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi3));}

    public static boolean isXiaomiMI_Note_Pro() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi_Note_Pro));}

    public static boolean isAlcatel_Idol3() { return isDevice(contex.getResources().getStringArray(R.array.Alcatel_Idol_3));}

    public static boolean isVivo_Xplay3s() { return isDevice(contex.getResources().getStringArray(R.array.Vivo_Xplay3s));}

    public static boolean isSamsung_S6_edge_plus() { return isDevice(contex.getResources().getStringArray(R.array.Samsung_S6_edge_plus));}

    ///MTK DEVICES

    static String MTK = "P6Life,thl 5000,Philips W8555,MX4";
    public static boolean isMediaTekDevice()
    {
        return isMeizuMX4() || isTHL5000() || isI_Mobile_I_StyleQ6() || isMeizuMX5() || isSonyM5_MTK();
    }

    public static boolean isI_Mobile_I_StyleQ6() { return isDevice(contex.getResources().getStringArray(R.array.I_Mobile_I_StyleQ6));}

    public static boolean isMeizuMX4()
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx4));
    }
    public static boolean isMeizuMX5()
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx5));
    }
    public static boolean isTHL5000()
    {
        return isDevice(contex.getResources().getStringArray(R.array.thl5000));
    }




}
