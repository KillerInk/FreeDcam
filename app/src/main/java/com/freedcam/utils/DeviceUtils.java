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

package com.freedcam.utils;

import android.content.Context;
import android.os.Build;

import com.troop.freedcam.R.array;


/**
 * Created by troop on 22.12.13.
 */
public class DeviceUtils
{

    /*Holds all added Devices*/
    public enum Devices
    {
        UNKNOWN,
        Alcatel_985n,
        Blackberry_Priv,
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
        HTC_OneE8,
        HTC_Desire500,
        Huawei_GX8,
        Huawei_HONOR5x,
        honor6,
        I_Mobile_I_StyleQ6,
        Jiayu_S3,
        LenovoK910,
        LenovoK920,
        Lenovo_K4Note_MTK,
        Lenovo_K50_MTK,
        Lenovo_VibeP1,
        LG_G2,
        LG_G2pro,
        LG_G3,
        LG_G4,
        MeizuMX4_MTK,
        MeizuMX5_MTK,
        Meizu_m2Note_MTK,
        Moto_MSM8974,
        Moto_MSM8982_8994,
        MotoG3,
        MotoG_Turbo,
        Nexus4,
        OnePlusOne,
        OnePlusTwo,
        p8,
        p8lite,
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
        Xiaomi_Redmi_Note3,
        Yu_Yureka,
        ZTE_ADV,
        ZTEADVIMX214,
        ZTEADV234,
    }

    public static boolean isCyanogenMod() {
        try {
            return Class.forName("cyanogenmod.os.Build") != null;
        } catch (Exception ignored) {
        }
        return false;
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
    public Devices getDevice(Context context)
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
        else if (isG2pro(context))
            return Devices.LG_G2pro;
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
        else if(isHuawei_Honor_5x(context))
            return Devices.Huawei_HONOR5x;
        else if(HTC_ONE_E8(context))
            return Devices.HTC_OneE8;
        else if(isMotoG3(context))
            return Devices.MotoG3;
        else if(isMotoGTurbo(context))
            return Devices.MotoG_Turbo;
        else if(isHTC_Desire500(context))
            return Devices.HTC_Desire500;
        else if(isBlackBerryPriv(context))
            return Devices.Blackberry_Priv;
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
    private boolean isDevice(String[] ar)
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


    private boolean isMotoG3(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Moto_G3));
    }

    private boolean isMotoGTurbo(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.G_Turbo));
    }

    private boolean HTC_ONE_E8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.One_E8));
    }

    private boolean isMoto_MSM8974(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.isX2k14));
    }

    private boolean isMoto_MSM8982_8994(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.isX_Style_Pure_Play));
    }

    private boolean isG4(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.g4));
    }

    private boolean isLG_G3(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.g3));
    }

    private boolean isG2(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.g2));
    }

    private boolean isG2pro(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.g2pro));
    }

    private boolean isHTC_Desire500(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Desire500));
    }

    private boolean isEvo3d(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.evo3d));
    }

    private boolean isAlcatelIdol3small(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Alcatel_Idol_3_small));
    }

    private boolean isAquaris_E5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.AQ_E5));
    }

    private boolean isHuawei_P8_lite(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.p8Lite));
    }

    private boolean isHuawei_P8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.p8));
    }

    private boolean isHuawei_Honor_6(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Honor6));
    }

    private boolean isHuawei_Honor_5x(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Honor5X));
    }

    private boolean isHuawei_GX8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.GX8));
    }

    private boolean isHTC_M8(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.htc_m8));
    }

    private boolean isBlackBerryPriv(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.BB_PRIV));
    }

    private boolean isHTC_M9(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.htc_m9));
    }
    private boolean isHTC_M10(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.htc_m10));
    }

    private boolean isHtc_One_SV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Htc_One_SV));
    }

    private boolean isHtc_One_XL(Context contex) { return Build.MODEL.toLowerCase().contains("one xl");}

    private boolean isZTEADV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.zteadv));
    }

    private boolean isZTEADVIMX214(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.zteadvIMX214));
    }

    private boolean isZTEADV234(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.zteadvIMX234));
    }

    private boolean hasIMX135(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.hasImx135));
    }

    private boolean hasIMX214(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.hasImx214));
    }

    private boolean isSonyADV(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.sony_adv));
    }

    private boolean isLenovoK910(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.LenovoK910));
    }
    private boolean isLenovoK920(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.LenovoK920));
    }

    private boolean isLenovoK4NOTE(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Lenovo_K4Note));
    }

    private boolean isLenovoVibeP1(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Lenovo_VibeP1));
    }

    private boolean isSonyM5_MTK(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.SonyM5));
    }
    private boolean isSonyC5_MTK(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.SonyC5));
    }

    private boolean isSonyM4_QC(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.SonyM4));
    }

    private boolean isHTCA9_QC(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.OneA9));
    }

    private boolean isRetro(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Retro));
    }

    private boolean isJiayu_S3(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Jiayu_S3));
    }


    private  boolean isXperiaL(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Sony_XperiaL));
    }

    public boolean isYureka(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.YuYureka));

    }

    private boolean isNexus4(Context contex) {return isDevice(contex.getResources().getStringArray(array.Nex4));}

    private boolean isGioneE7(Context contex) {return isDevice(contex.getResources().getStringArray(array.GioneE7));}

    private boolean isOnePlusOne(Context contex){ return isDevice(contex.getResources().getStringArray(array.OnePlusOne));}

    private boolean isOnePlusTwo(Context contex){ return isDevice(contex.getResources().getStringArray(array.OnePlusTwo));}

    private boolean isRedmiNote(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_RedmiNote));}
    private boolean isRedmiNote2(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_RedmiNote2));}

    private boolean isXiaomiMI3W(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_Mi3));}
    private boolean isXiaomiMI4W(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_Mi4));}
    private boolean isXiaomiMI4C(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Xiaomi_Mi4C));
    }
    private boolean isXiaomiMI5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Xiaomi_MI5));
    }

    private boolean isXiaomiMI_Note_Pro(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_Mi_Note_Pro));}

    private boolean isXiaomiMI_Note3_Pro(Context contex) { return isDevice(contex.getResources().getStringArray(array.Xiaomi_Mi_Note3_Pro));}

    private boolean isAlcatel_Idol3(Context contex) { return isDevice(contex.getResources().getStringArray(array.Alcatel_Idol_3));}

    private boolean isVivo_Xplay3s(Context contex) { return isDevice(contex.getResources().getStringArray(array.Vivo_Xplay3s));}

    private boolean isSamsung_S6_edge_plus(Context contex) { return isDevice(contex.getResources().getStringArray(array.Samsung_S6_edge_plus));}
    private boolean isSamsung_S6_edge(Context contex) { return isDevice(contex.getResources().getStringArray(array.Samsung_S6_edge));}

    ///MTK DEVICES


    private boolean isI_Mobile_I_StyleQ6(Context contex) { return isDevice(contex.getResources().getStringArray(array.I_Mobile_I_StyleQ6));}

    private boolean isMeizuMX4(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.meizu_mx4));
    }

    private boolean isMeizum2Note(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.meizu_m2note));
    }

    private boolean is985N(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Alcatel_985N));
    }

    private boolean isMeizuMX5(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.meizu_mx5));
    }
    private boolean isTHL5000(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.thl5000));
    }

    private boolean isLenovo_K50(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.K50));
    }
    private boolean isForwardArt(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Forward_Art));
    }
    private boolean isAsus_Zenfon2(Context contex)
    {
        return isDevice(contex.getResources().getStringArray(array.Asus_Zenfon2));
    }
}
