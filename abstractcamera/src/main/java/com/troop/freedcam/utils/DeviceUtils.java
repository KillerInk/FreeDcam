package com.troop.freedcam.utils;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.abstractcamera.R;


/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{
    private static Context contex;
    private static Devices currentdevice;

    public static Devices DEVICE()
    {
        return currentdevice;
    }

    public static boolean IS(Devices device)
    {
        if (currentdevice == device)
            return  true;
        else
            return false;
    }

    public static boolean IS_DEVICE_ONEOF(Devices[] device)
    {
        for (Devices d : device)
        {
            if (currentdevice == d)
                return true;
        }
        return false;
    }

    public static void SETCONTEXT(Context context)
    {
        contex = context;
        currentdevice = getDevice();
    }

    public static void RELEASE()
    {
        if (contex != null)
            contex =null;

    }

    public static boolean HAS_CONTEXT()
    {
        if (contex == null)
            return false;
        else
            return true;
    }

    final public static Devices[] AlcatelIdol3_Moto_MSM8982_8994 = {Devices.Moto_MSM8982_8994, Devices.Alcatel_Idol3 };
    final public static Devices[] MOTOX = {Devices.Moto_MSM8982_8994, Devices.Moto_MSM8974 };
    final public static Devices[] MI3_4 = {Devices.XiaomiMI4W, Devices.XiaomiMI3W };
    final public static Devices[] LG_G2_3 = {Devices.LG_G2, Devices.LG_G3};
    final public static Devices[] HTC_m8_9 = {Devices.Htc_M8, Devices.Htc_M9};
    final public static Devices[] ZTE_DEVICES = {Devices.ZTE_ADV,Devices.ZTEADVIMX214,Devices.ZTEADV234};

    private static Devices getDevice()
    {
        if (isAlcatel_Idol3())
            return Devices.Alcatel_Idol3;
        else if (isAsus_Zenfon2())
            return Devices.Asus_Zenfon2;
        else if (isGioneE7())
            return Devices.GioneE7;
        else if (isEvo3d())
            return Devices.Htc_Evo3d;
        else if (isHTC_M8())
            return Devices.Htc_M8;
        else if (isHTC_M9())
            return Devices.Htc_M9;
        else if (isHtc_One_SV())
            return Devices.Htc_One_Sv;
        else if (isHtc_One_XL())
            return Devices.Htc_One_Xl;
        else if (isI_Mobile_I_StyleQ6())
            return Devices.I_Mobile_I_StyleQ6;
        else if (isLenovoK910())
            return Devices.LenovoK910;
        else if (isLenovoK920())
            return Devices.LenovoK920;
        else if (isG2())
            return Devices.LG_G2;
        else if (isLG_G3())
            return Devices.LG_G3;
        else if (isG4())
            return Devices.LG_G4;
        else if (isMeizuMX4())
            return Devices.MeizuMX4_MTK;
        else if (isMeizuMX5())
            return Devices.MeizuMX5_MTK;
        else if (isMoto_MSM8974())
            return Devices.Moto_MSM8974;
        else if (isMoto_MSM8982_8994())
            return Devices.Moto_MSM8982_8994;
        else if (isNexus4())
            return Devices.Nexus4;
        else if (isOnePlusOne())
            return Devices.OnePlusOne;
        else if (isOnePlusTwo())
            return Devices.OnePlusTwo;
        else if (isRedmiNote())
            return Devices.RedmiNote;
        else if (isRedmiNote2())
            return Devices.RedmiNote2_MTK;
        else if (isRetro())
            return Devices.Retro_MTK;
        else if (isSamsung_S6_edge())
            return Devices.Samsung_S6_edge;
        else if (isSamsung_S6_edge_plus())
            return Devices.Samsung_S6_edge_plus;
        else if (isSonyADV())
            return Devices.SonyADV;
        else if (isSonyM5_MTK())
            return Devices.SonyM5_MTK;
        else if(isSonyM4_QC())
            return Devices.SonyM4_QC;
        else if(isSonyC5_MTK())
            return Devices.SonyC5_MTK;
        else if (isXperiaL())
            return Devices.Sony_XperiaL;
        else if (isTHL5000())
            return Devices.THL5000_MTK;
        else if (isVivo_Xplay3s())
            return Devices.Vivo_Xplay3s;
        else if (isXiaomiMI3W())
            return Devices.XiaomiMI3W;
        else if (isXiaomiMI4W())
            return Devices.XiaomiMI4W;
        else if (isXiaomiMI_Note_Pro())
            return Devices.XiaomiMI_Note_Pro;
        else if (isYureka())
            return Devices.Yu_Yureka;
        else if (isZTEADV())
            return Devices.ZTE_ADV;
        else if (isZTEADVIMX214())
            return Devices.ZTEADVIMX214;
        else if (isZTEADV234())
            return Devices.ZTEADV234;
        else
            return Devices.UNKNOWN;

    }

    public enum Devices
    {
        UNKNOWN,
        Alcatel_Idol3,
        Asus_Zenfon2,
        GioneE7,
        ForwardArt_MTK,
        Htc_Evo3d,
        Htc_M8,
        Htc_M9,
        Htc_One_Sv,
        Htc_One_Xl,
        I_Mobile_I_StyleQ6,
        LenovoK910,
        LenovoK920,
        LG_G2,
        LG_G3,
        LG_G4,
        MeizuMX4_MTK,
        MeizuMX5_MTK,
        Moto_MSM8974,
        Moto_MSM8982_8994,
        Nexus4,
        OnePlusOne,
        OnePlusTwo,
        RedmiNote,
        RedmiNote2_MTK,
        Retro_MTK,
        Samsung_S6_edge,
        Samsung_S6_edge_plus,
        SonyADV,
        SonyM5_MTK,
        SonyM4_QC,
        SonyC5_MTK,
        Sony_XperiaL,
        THL5000_MTK,
        Vivo_Xplay3s,
        XiaomiMI3W,
        XiaomiMI4W,
        XiaomiMI_Note_Pro,
        Yu_Yureka,
        ZTE_ADV,
        ZTEADVIMX214,
        ZTEADV234,
    }

    public static boolean IsMarshMallowG3()
    {
        if (currentdevice == Devices.LG_G3 && Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
            return true;
        else
            return false;
    }

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

    private static Devices[] camera1DNGsupported = {
            Devices.LG_G3, Devices.LG_G2, Devices.LG_G4,
            Devices.Htc_M8, Devices.Htc_M9, Devices.Htc_One_Sv,Devices.Htc_One_Xl,
            Devices.ZTE_ADV, Devices.ZTEADVIMX214, Devices.ZTEADV234,
            Devices.LenovoK910,Devices.LenovoK920,
            Devices.Yu_Yureka,
            Devices.OnePlusOne, Devices.OnePlusTwo,
            Devices.RedmiNote, Devices.XiaomiMI3W, Devices.XiaomiMI4W, Devices.XiaomiMI_Note_Pro, Devices.RedmiNote2_MTK,
            Devices.Vivo_Xplay3s,
            Devices.GioneE7,
            Devices.Sony_XperiaL,
            Devices.SonyM5_MTK,
            Devices.SonyC5_MTK,
            Devices.Alcatel_Idol3,
            Devices.MeizuMX4_MTK, Devices.MeizuMX5_MTK,
            Devices.Moto_MSM8982_8994, Devices.Moto_MSM8974,
            Devices.Retro_MTK,
            Devices.THL5000_MTK,
            Devices.SonyM4_QC
    };

    public static Devices[] camera1NO_RAW_STREAM = {
            Devices.Asus_Zenfon2,
            Devices.Nexus4,
            Devices.Htc_Evo3d,

    };
    public static boolean isCamera1DNGSupportedDevice()
    {
        return IS_DEVICE_ONEOF(camera1DNGsupported);
        /*isLG_G3() || isG2() || isG4() || isHTC_M8() || isZTEADV() || isZTEADVIMX214() || isZTEADV234() || isHTC_M9() || isHtc_One_SV() || isHtc_One_XL() || isLenovoK910() || isYureka() ||
                isOnePlusOne() || isRedmiNote() || isXiaomiMI3W()||isXiaomiMI4W()|| isXperiaL() ||isXiaomiMI_Note_Pro() || isVivo_Xplay3s();*/
    }

    public static boolean isCamera1NO_RAW_STREM()
    {
        return IS_DEVICE_ONEOF(camera1NO_RAW_STREAM);
    }

    private static boolean isMoto_MSM8974()
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX2k14));
    }

    private static boolean isMoto_MSM8982_8994()
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX_Style_Pure_Play));
    }

    private static boolean isG4()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.g4));
    }

    private static boolean isLG_G3()
    {
        return isDevice(contex.getResources().getStringArray(R.array.g3));
    }

    private static boolean isG2()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.g2));
    }

    private static boolean isEvo3d()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.evo3d));
    }

    private static boolean isHTC_M8()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.htc_m8));
    }

    private static boolean isHTC_M9()
    {
        return isDevice(contex.getResources().getStringArray(com.troop.freedcam.abstractcamera.R.array.htc_m9));
    }

    private static boolean isHtc_One_SV()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Htc_One_SV));
    }

    private static boolean isHtc_One_XL() { return Build.MODEL.toLowerCase().contains("one xl");}

    private static boolean isZTEADV()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadv));
    }

    private static boolean isZTEADVIMX214()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX214));
    }

    private static boolean isZTEADV234()
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX234));
    }

    private static boolean hasIMX135()
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx135));
    }

    private static boolean hasIMX214()
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx214));
    }

    private static boolean isSonyADV()
    {
        return containsDevice(contex.getResources().getStringArray(R.array.sony_adv));
    }

    public static boolean isLenovoK910()
    {
        return isDevice(contex.getResources().getStringArray(R.array.LenovoK910));
    }
    public static boolean isLenovoK920()
    {
        return isDevice(contex.getResources().getStringArray(R.array.LenovoK920));
    }

    public static boolean isSonyM5_MTK()
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyM5));
    }
    public static boolean isSonyC5_MTK()
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyC5));
    }

    public static boolean isSonyM4_QC()
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyM5));
    }

    public static boolean isRetro()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Retro));
    }


    public static  boolean isXperiaL()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Sony_XperiaL));
    }

    public static boolean isYureka()
    {
        return isDevice(contex.getResources().getStringArray(R.array.YuYureka));

    }

    private static boolean isNexus4() {return isDevice(contex.getResources().getStringArray(R.array.Nex4));}

    private static boolean isGioneE7() {return isDevice(contex.getResources().getStringArray(R.array.GioneE7));}

    private static boolean isOnePlusOne(){ return isDevice(contex.getResources().getStringArray(R.array.OnePlusOne));}

    private static boolean isOnePlusTwo(){ return isDevice(contex.getResources().getStringArray(R.array.OnePlusTwo));}

    private static boolean isRedmiNote() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote));}
    private static boolean isRedmiNote2() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote2));}

    private static boolean isXiaomiMI3W() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi3));}
    private static boolean isXiaomiMI4W() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi4));}

    private static boolean isXiaomiMI_Note_Pro() { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi_Note_Pro));}

    private static boolean isAlcatel_Idol3() { return isDevice(contex.getResources().getStringArray(R.array.Alcatel_Idol_3));}

    private static boolean isVivo_Xplay3s() { return isDevice(contex.getResources().getStringArray(R.array.Vivo_Xplay3s));}

    private static boolean isSamsung_S6_edge_plus() { return isDevice(contex.getResources().getStringArray(R.array.Samsung_S6_edge_plus));}
    private static boolean isSamsung_S6_edge() { return isDevice(contex.getResources().getStringArray(R.array.Samsung_S6_edge));}

    ///MTK DEVICES

    static String MTK = "P6Life,thl 5000,Philips W8555,MX4";
    public static boolean isMediaTekDevice()
    {
        return isForwardArt() || isMeizuMX4() || isTHL5000() || isI_Mobile_I_StyleQ6() || isMeizuMX5() || isSonyM5_MTK()|| isRedmiNote2() || isRetro();
    }

    private static boolean isI_Mobile_I_StyleQ6() { return isDevice(contex.getResources().getStringArray(R.array.I_Mobile_I_StyleQ6));}

    private static boolean isMeizuMX4()
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx4));
    }
    private static boolean isMeizuMX5()
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx5));
    }
    private static boolean isTHL5000()
    {
        return isDevice(contex.getResources().getStringArray(R.array.thl5000));
    }
    private static boolean isForwardArt()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Forward_Art));
    }
    private static boolean isAsus_Zenfon2()
    {
        return isDevice(contex.getResources().getStringArray(R.array.Asus_Zenfon2));
    }


    /*public static boolean isLGFrameWork()
    {
        return isLG_G3() || isG2() || isG4() ;
    }*/




}
