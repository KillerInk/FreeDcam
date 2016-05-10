package com.freedcam.utils;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.R;


/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{
    private static Devices currentdevice;

    public static Devices DEVICE()
    {
        return currentdevice;
    }

    public static boolean IS(Devices device)
    {
        return currentdevice == device;
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

    public static void CheckAndSetDevice(Context context)
    {
        currentdevice = getDevice(context);
    }

    public static void DESTROY()
    {
        currentdevice = null;
    }


    final public static Devices[] AlcatelIdol3_Moto_MSM8982_8994 = {Devices.Moto_MSM8982_8994, Devices.Alcatel_Idol3 };
    final public static Devices[] MOTOX = {Devices.Moto_MSM8982_8994, Devices.Moto_MSM8974 };
    final public static Devices[] MI3_4 = {Devices.XiaomiMI4W, Devices.XiaomiMI3W };
    final public static Devices[] LG_G2_3 = {Devices.LG_G2, Devices.LG_G3};
    final public static Devices[] HTC_m8_9 = {Devices.Htc_M8, Devices.Htc_M9,Devices.HTC_OneA9};
    final public static Devices[] ZTE_DEVICES = {Devices.ZTE_ADV,Devices.ZTEADVIMX214,Devices.ZTEADV234};
    final public static Devices[] Sony_DEVICES = {Devices.SonyM4_QC,Devices.SonyC5_MTK,Devices.SonyM5_MTK};
    final public static Devices[] Krillin_DEVICES = {Devices.p8,Devices.p8lite,Devices.honor6};

    /**
    *devices with the new qc hal
    *com.freedcam.apis.camera1.camera.parameters.manual.AE_Handler_QcomM.java
     */
    final public static Devices[] QC_Manual_New =
            {
                    Devices.SonyM4_QC,
                    Devices.Alcatel_Idol3,
                    Devices.Moto_MSM8982_8994,
                    Devices.Lenovo_VibeP1,
                    Devices.XiaomiMI5,
                    Devices.Xiaomi_Redmi_Note3,
                    Devices.Aquaris_E5,
                    Devices.Huawei_GX8
            };

    /*Devices for that the opcode is added to download*/
    final public static Devices[] OpCodeRdyToDL =
            {
                    Devices.Aquaris_E5,
                    Devices.Htc_M8,
                    Devices.Htc_M9,
                    Devices.HTC_OneA9,
                    Devices.Jiayu_S3,
                    Devices.LG_G2,
                    Devices.LG_G3,
                    Devices.LG_G4,
                    Devices.OnePlusOne,
                    Devices.SonyC5_MTK,
                    Devices.SonyM4_QC,
                    Devices.SonyM5_MTK,
                    Devices.XiaomiMI3W,
                    Devices.XiaomiMI4W,
                    Devices.Xiaomi_RedmiNote2_MTK,
                    Devices.ZTE_ADV,
                    Devices.ZTEADV234,
            };



    /*Holds all added Devices*/
    public enum Devices
    {
        UNKNOWN,
        Alcatel_985n,
        p8,
        p8lite,
        honor6,
        Aquaris_E5,
        Alcatel_Idol3,
        Alcatel_Idol3_small,
        Asus_Zenfon2,
        GioneE7,
        ForwardArt_MTK,
        Htc_Evo3d,
        Htc_M8,
        Htc_M9,
        Htc_M10,
        Htc_One_Sv,
        Htc_One_Xl,
        HTC_OneA9,
        Huawei_GX8,
        I_Mobile_I_StyleQ6,
        Jiayu_S3,
        LenovoK910,
        LenovoK920,
        Lenovo_K4Note_MTK,
        Lenovo_K50_MTK,
        Lenovo_VibeP1,
        LG_G2,
        LG_G3,
        LG_G4,
        MeizuMX4_MTK,
        MeizuMX5_MTK,
        Meizu_m2Note_MTK,
        Moto_MSM8974,
        Moto_MSM8982_8994,
        Nexus4,
        OnePlusOne,
        OnePlusTwo,
        Xiaomi_RedmiNote,
        Xiaomi_RedmiNote2_MTK,
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
        XiaomiMI4C,
        XiaomiMI5,
        XiaomiMI_Note_Pro,
        XiaomiMI_Note3_Pro,
        Xiaomi_Redmi_Note3,
        Yu_Yureka,
        ZTE_ADV,
        ZTEADVIMX214,
        ZTEADV234,
    }

    /*Identfiy LG_G3 Marshmallow*/
    public static boolean IsMarshMallowG3()
    {
        return currentdevice == Devices.LG_G3 && Build.VERSION.SDK_INT == Build.VERSION_CODES.M;
    }

    public static boolean isCyanogenMod() {
        try {
            return Class.forName("cyanogenmod.os.Build") != null;
        } catch (Exception ignored) {
        }
        return false;
    }



    public static boolean isCamera1DNGSupportedDevice()
    {
        return IS_DEVICE_ONEOF(camera1DNGsupported);
    }

    /**
     * holds all Devices that are added
     * @com.troop.androiddng.DngSupportedDevices.java
     */
    private static Devices[] camera1DNGsupported = {
            Devices.Alcatel_Idol3,
            Devices.Alcatel_Idol3_small,
            Devices.Alcatel_985n,
            Devices.Aquaris_E5,
            Devices.ForwardArt_MTK,
            Devices.GioneE7,
            Devices.Htc_M8,
            Devices.Htc_M9,
            Devices.Htc_One_Sv,
            Devices.Htc_One_Xl,
            Devices.HTC_OneA9,
            Devices.Huawei_GX8,
            Devices.I_Mobile_I_StyleQ6,
            Devices.Jiayu_S3,
            Devices.LenovoK910,
            Devices.LenovoK920,
            Devices.Lenovo_VibeP1,
            Devices.Lenovo_K4Note_MTK,
            Devices.Lenovo_K50_MTK,
            Devices.LG_G3,
            Devices.LG_G2,
            Devices.LG_G4,
            Devices.MeizuMX4_MTK,
            Devices.MeizuMX5_MTK,
            Devices.Meizu_m2Note_MTK,
            Devices.Moto_MSM8982_8994,
            Devices.Moto_MSM8974,
            Devices.OnePlusOne,
            Devices.OnePlusTwo,
            Devices.Retro_MTK,
            Devices.Sony_XperiaL,
            Devices.SonyM5_MTK,
            Devices.SonyC5_MTK,
            Devices.SonyM4_QC,
            Devices.THL5000_MTK,
            Devices.Vivo_Xplay3s,
            Devices.Xiaomi_RedmiNote,
            Devices.XiaomiMI3W,
            Devices.XiaomiMI4W,
            Devices.XiaomiMI4C,
            Devices.XiaomiMI_Note_Pro,
            Devices.XiaomiMI_Note3_Pro,
            Devices.Xiaomi_RedmiNote2_MTK,
            Devices.Xiaomi_Redmi_Note3,
            Devices.Yu_Yureka,
            Devices.ZTE_ADV,
            Devices.ZTEADVIMX214,
            Devices.ZTEADV234,
            //,Devices.XiaomiMI5 Unknown Raw Failure need more MI5 user input alternate switch to HDR Scene for raw dump
    };


    public static boolean isCamera1NO_RAW_STREM()
    {
        return IS_DEVICE_ONEOF(camera1NO_RAW_STREAM);
    }

    /**
     * all devices that are test to have non working raw stream
     */
    public static Devices[] camera1NO_RAW_STREAM = {
            Devices.Asus_Zenfon2,
            Devices.Nexus4,
            Devices.Htc_Evo3d,

    };


    /**
     * identify the current device
     */
    private static Devices getDevice(Context context)
    {
        if (isAlcatel_Idol3(context))
            return Devices.Alcatel_Idol3;
        if (isAlcatelIdol3small(context))
            return Devices.Alcatel_Idol3_small;
        else if (isAsus_Zenfon2(context))
            return Devices.Asus_Zenfon2;
        else if (isGioneE7(context))
            return Devices.GioneE7;
        else if (isEvo3d(context))
            return Devices.Htc_Evo3d;
        else if (isHTC_M8(context))
            return Devices.Htc_M8;
        else if (isHTC_M9(context))
            return Devices.Htc_M9;
        else if(isHTC_M10(context))
            return Devices.Htc_M10;
        else if (isHtc_One_SV(context))
            return Devices.Htc_One_Sv;
        else if (isHtc_One_XL(context))
            return Devices.Htc_One_Xl;
        else if (isI_Mobile_I_StyleQ6(context))
            return Devices.I_Mobile_I_StyleQ6;
        else if (isLenovoK910(context))
            return Devices.LenovoK910;
        else if (isLenovoK920(context))
            return Devices.LenovoK920;
        else if (isLenovoK4NOTE(context))
            return Devices.Lenovo_K4Note_MTK;
        else if (isG2(context))
            return Devices.LG_G2;
        else if (isLG_G3(context))
            return Devices.LG_G3;
        else if (isG4(context))
            return Devices.LG_G4;
        else if (isMeizuMX4(context))
            return Devices.MeizuMX4_MTK;
        else if (isMeizuMX5(context))
            return Devices.MeizuMX5_MTK;
        else if(isMeizum2Note(context))
            return Devices.Meizu_m2Note_MTK;
        else if (isMoto_MSM8974(context))
            return Devices.Moto_MSM8974;
        else if (isMoto_MSM8982_8994(context))
            return Devices.Moto_MSM8982_8994;
        else if (isNexus4(context))
            return Devices.Nexus4;
        else if (isOnePlusOne(context))
            return Devices.OnePlusOne;
        else if (isOnePlusTwo(context))
            return Devices.OnePlusTwo;
        else if (isRedmiNote(context))
            return Devices.Xiaomi_RedmiNote;
        else if (isRedmiNote2(context))
            return Devices.Xiaomi_RedmiNote2_MTK;
        else if (isRetro(context))
            return Devices.Retro_MTK;
        else if (isSamsung_S6_edge(context))
            return Devices.Samsung_S6_edge;
        else if (isSamsung_S6_edge_plus(context))
            return Devices.Samsung_S6_edge_plus;
        else if (isSonyADV(context))
            return Devices.SonyADV;
        else if (isSonyM5_MTK(context))
            return Devices.SonyM5_MTK;
        else if(isSonyM4_QC(context))
            return Devices.SonyM4_QC;
        else if(isSonyC5_MTK(context))
            return Devices.SonyC5_MTK;
        else if (isXperiaL(context))
            return Devices.Sony_XperiaL;
        else if (isTHL5000(context))
            return Devices.THL5000_MTK;
        else if (isVivo_Xplay3s(context))
            return Devices.Vivo_Xplay3s;
        else if (isXiaomiMI3W(context))
            return Devices.XiaomiMI3W;
        else if (isXiaomiMI4W(context))
            return Devices.XiaomiMI4W;
        else if(isXiaomiMI4C(context))
            return Devices.XiaomiMI4C;
        else if(isXiaomiMI5(context))
            return Devices.XiaomiMI5;
        else if (isXiaomiMI_Note_Pro(context))
            return Devices.XiaomiMI_Note_Pro;
        else if (isXiaomiMI_Note3_Pro(context))
            return Devices.XiaomiMI_Note3_Pro;
        else if(isXiaomi_Redmi_Note3(context))
            return Devices.Xiaomi_Redmi_Note3;
        else if (isYureka(context))
            return Devices.Yu_Yureka;
        else if (isZTEADV(context))
            return Devices.ZTE_ADV;
        else if (isZTEADVIMX214(context))
            return Devices.ZTEADVIMX214;
        else if (isZTEADV234(context))
            return Devices.ZTEADV234;
        else if(isHTCA9_QC(context))
            return Devices.HTC_OneA9;
        else if(isLenovo_K50(context))
            return Devices.Lenovo_K50_MTK;
        else if(isForwardArt(context))
            return Devices.ForwardArt_MTK;
        else if(isHuawei_P8_lite(context))
            return Devices.p8lite;
        else if(isHuawei_P8(context))
            return Devices.p8;
        else if(isHuawei_Honor_6(context))
            return Devices.honor6;
        else if(is985N(context))
            return Devices.Alcatel_985n;
        else if(isJiayu_S3(context))
            return Devices.Jiayu_S3;
        else if(isAquaris_E5(context))
            return Devices.Aquaris_E5;
        else if(isLenovoVibeP1(context))
            return Devices.Lenovo_VibeP1;
        else if(isHuawei_GX8(context))
            return Devices.Huawei_GX8;
        else
            return Devices.UNKNOWN;

    }

    /*
     * Here start the private stuff
     * that loads the build models from Resources
     */
    /***
     * Checks if the build model is contained in the array
     * @param ar the Device BuildModel Array
     * @return true if the BuildModel is contained
     */
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

    private static boolean isMoto_MSM8974(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX2k14));
    }

    private static boolean isMoto_MSM8982_8994(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.isX_Style_Pure_Play));
    }

    private static boolean isG4(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.g4));
    }

    private static boolean isLG_G3(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.g3));
    }

    private static boolean isG2(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.g2));
    }

    private static boolean isEvo3d(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.evo3d));
    }

    private static boolean isAlcatelIdol3small(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Alcatel_Idol_3_small));
    }

    private static boolean isAquaris_E5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.AQ_E5));
    }

    private static boolean isHuawei_P8_lite(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.p8Lite));
    }

    private static boolean isHuawei_P8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.p8));
    }

    private static boolean isHuawei_Honor_6(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Honor6));
    }

    private static boolean isHuawei_GX8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.GX8));
    }

    private static boolean isHTC_M8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.htc_m8));
    }

    private static boolean isHTC_M9(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.htc_m9));
    }
    private static boolean isHTC_M10(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.htc_m10));
    }

    private static boolean isHtc_One_SV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Htc_One_SV));
    }

    private static boolean isHtc_One_XL(Context contex) { return Build.MODEL.toLowerCase().contains("one xl");}

    private static boolean isZTEADV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadv));
    }

    private static boolean isZTEADVIMX214(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX214));
    }

    private static boolean isZTEADV234(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.zteadvIMX234));
    }

    private static boolean hasIMX135(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx135));
    }

    private static boolean hasIMX214(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.hasImx214));
    }

    private static boolean isSonyADV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.sony_adv));
    }

    private static boolean isLenovoK910(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.LenovoK910));
    }
    private static boolean isLenovoK920(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.LenovoK920));
    }

    private static boolean isLenovoK4NOTE(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Lenovo_K4Note));
    }

    private static boolean isLenovoVibeP1(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Lenovo_VibeP1));
    }

    private static boolean isSonyM5_MTK(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyM5));
    }
    private static boolean isSonyC5_MTK(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyC5));
    }

    private static boolean isSonyM4_QC(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.SonyM4));
    }

    private static boolean isHTCA9_QC(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.OneA9));
    }

    private static boolean isRetro(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Retro));
    }

    private static boolean isJiayu_S3(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Jiayu_S3));
    }


    private static  boolean isXperiaL(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Sony_XperiaL));
    }

    public static boolean isYureka(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.YuYureka));

    }

    private static boolean isNexus4(Context contex) {return isDevice(contex.getResources().getStringArray(R.array.Nex4));}

    private static boolean isGioneE7(Context contex) {return isDevice(contex.getResources().getStringArray(R.array.GioneE7));}

    private static boolean isOnePlusOne(Context contex){ return isDevice(contex.getResources().getStringArray(R.array.OnePlusOne));}

    private static boolean isOnePlusTwo(Context contex){ return isDevice(contex.getResources().getStringArray(R.array.OnePlusTwo));}

    private static boolean isRedmiNote(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote));}
    private static boolean isRedmiNote2(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote2));}

    private static boolean isXiaomiMI3W(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi3));}
    private static boolean isXiaomiMI4W(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi4));}
    private static boolean isXiaomiMI4C(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi4C));
    }
    private static boolean isXiaomiMI5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_MI5));
    }

    private static boolean isXiaomiMI_Note_Pro(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi_Note_Pro));}

    private static boolean isXiaomiMI_Note3_Pro(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_Mi_Note3_Pro));}
    private static boolean isXiaomi_Redmi_Note3(Context contex){ return isDevice(contex.getResources().getStringArray(R.array.Xiaomi_RedmiNote3));}

    private static boolean isAlcatel_Idol3(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Alcatel_Idol_3));}

    private static boolean isVivo_Xplay3s(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Vivo_Xplay3s));}

    private static boolean isSamsung_S6_edge_plus(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Samsung_S6_edge_plus));}
    private static boolean isSamsung_S6_edge(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.Samsung_S6_edge));}

    ///MTK DEVICES


    private static boolean isI_Mobile_I_StyleQ6(Context contex) { return isDevice(contex.getResources().getStringArray(R.array.I_Mobile_I_StyleQ6));}

    private static boolean isMeizuMX4(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx4));
    }

    private static boolean isMeizum2Note(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_m2note));
    }

    private static boolean is985N(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Alcatel_985N));
    }

    private static boolean isMeizuMX5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.meizu_mx5));
    }
    private static boolean isTHL5000(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.thl5000));
    }

    private static boolean isLenovo_K50(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.K50));
    }
    private static boolean isForwardArt(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Forward_Art));
    }
    private static boolean isAsus_Zenfon2(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(R.array.Asus_Zenfon2));
    }
}
