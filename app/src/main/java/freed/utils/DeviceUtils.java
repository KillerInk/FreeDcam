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

import android.content.res.Resources;
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
        Elephone_P9000,
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
        huawei_honor6,
        I_Mobile_I_StyleQ6,
        Jiayu_S3,
        LenovoK910,
        LenovoK920,
        Lenovo_K4Note_MTK,
        Lenovo_K50_MTK,
        Lenovo_VibeP1,
        Lenovo_VibeShot_Z90,
        LG_G2,
        LG_G2pro,
        LG_G3,
        LG_G4,
        Lumigon_T3,
        MeizuMX4_MTK,
        MeizuMX5_MTK,
        Meizu_m2Note_MTK,
        Mlais_M52_Red_Note_MTK,
        Moto_X2k14,
        Moto_X_Style_Pure_Play,
        MotoG3,
        MotoG_Turbo,
        Nexus4,
        OnePlusOne,
        OnePlusTwo,
        p8,
        p8lite,
        Prestigio_Multipad_Color,
        Xiaomi_RedmiNote,
        Xiaomi_Redmi2,
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
        Umi_Rome_X,
        Vivo_Xplay3s,
        Wileyfox_Swift,
        Mi_Max,
        XiaomiMI3W,
        XiaomiMI4W,
        XiaomiMI4C,
        XiaomiMI5,
        XiaomiMI_Note_Pro,
        Xiaomi_Redmi_Note3,
        Xiaomi_Redmi3,
        Yu_Yureka,
        ZTE_ADV,
        ZTEADVIMX214,
        ZTEADV234,
        Zoppo_8speed,
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
    public Devices getDevice(Resources res)
    {
        if (isDevice(res.getStringArray(array.Alcatel_Idol_3)))
            return Devices.Alcatel_Idol3;
        if (isDevice(res.getStringArray(array.Alcatel_Idol_3_small)))
            return Devices.Alcatel_Idol3_small;
        else if (isDevice(res.getStringArray(array.Asus_Zenfon2)))
            return Devices.Asus_Zenfon2;
        else if(isDevice(res.getStringArray(array.Elephone9000)))
            return Devices.Elephone_P9000;
        else if (isDevice(res.getStringArray(array.GioneE7)))
            return Devices.GioneE7;
        else if (isDevice(res.getStringArray(array.htc_evo3d)))
            return Devices.Htc_Evo3d;
        else if (isDevice(res.getStringArray(array.htc_m8)))
            return Devices.Htc_M8;
        else if (isDevice(res.getStringArray(array.htc_m9)))
            return Devices.Htc_M9;
        else if(isDevice(res.getStringArray(array.htc_m10)))
            return Devices.Htc_M10;
        else if (isDevice(res.getStringArray(array.Htc_One_SV)))
            return Devices.Htc_One_Sv;
        else if (Build.MODEL.toLowerCase().contains("one xl"))
            return Devices.Htc_One_Xl;
        else if (isDevice(res.getStringArray(array.I_Mobile_I_StyleQ6)))
            return Devices.I_Mobile_I_StyleQ6;
        else if (isDevice(res.getStringArray(array.LenovoK910)))
            return Devices.LenovoK910;
        else if (isDevice(res.getStringArray(array.LenovoK920)))
            return Devices.LenovoK920;
        else if (isDevice(res.getStringArray(array.Lenovo_K4Note)))
            return Devices.Lenovo_K4Note_MTK;
        else if (isDevice(res.getStringArray(array.lg_g2)))
            return Devices.LG_G2;
        else if (isDevice(res.getStringArray(array.lg_g2pro)))
            return Devices.LG_G2pro;
        else if (isDevice(res.getStringArray(array.lg_g3)))
            return Devices.LG_G3;
        else if (isDevice(res.getStringArray(array.lg_g4)))
            return Devices.LG_G4;
        else if(isDevice(res.getStringArray(array.Lumigon_T3)))
            return Devices.Lumigon_T3;
        else if (isDevice(res.getStringArray(array.meizu_mx4)))
            return Devices.MeizuMX4_MTK;
        else if (isDevice(res.getStringArray(array.meizu_mx5)))
            return Devices.MeizuMX5_MTK;
        else if(isDevice(res.getStringArray(array.meizu_m2note)))
            return Devices.Meizu_m2Note_MTK;
        else if (isDevice(res.getStringArray(array.Moto_X2k14)))
            return Devices.Moto_X2k14;
        else if (isDevice(res.getStringArray(array.Moto_X_Style_Pure_Play)))
            return Devices.Moto_X_Style_Pure_Play;
        else if (isDevice(res.getStringArray(array.Nexus4)))
            return Devices.Nexus4;
        else if (isDevice(res.getStringArray(array.OnePlusOne)))
            return Devices.OnePlusOne;
        else if (isDevice(res.getStringArray(array.OnePlusTwo)))
            return Devices.OnePlusTwo;
        else if (isDevice(res.getStringArray(array.Xiaomi_RedmiNote)))
            return Devices.Xiaomi_RedmiNote;
        else if(isDevice(res.getStringArray(array.Xiaomi_Redmi2)))
            return Devices.Xiaomi_Redmi2;
        else if (isDevice(res.getStringArray(array.Xiaomi_RedmiNote2)))
            return Devices.Xiaomi_RedmiNote2_MTK;
        else if (isDevice(res.getStringArray(array.Retro)))
            return Devices.Retro_MTK;
        else if (isDevice(res.getStringArray(array.Samsung_S6_edge)))
            return Devices.Samsung_S6_edge;
        else if (isDevice(res.getStringArray(array.Samsung_S6_edge_plus)))
            return Devices.Samsung_S6_edge_plus;
        else if (isDevice(res.getStringArray(array.sony_adv)))
            return Devices.SonyADV;
        else if (isDevice(res.getStringArray(array.SonyM5)))
            return Devices.SonyM5_MTK;
        else if(isDevice(res.getStringArray(array.SonyM4)))
            return Devices.SonyM4_QC;
        else if(isDevice(res.getStringArray(array.SonyC5)))
            return Devices.SonyC5_MTK;
        else if (isDevice(res.getStringArray(array.Sony_XperiaL)))
            return Devices.Sony_XperiaL;
        else if (isDevice(res.getStringArray(array.thl5000)))
            return Devices.THL5000_MTK;
        else if (isDevice(res.getStringArray(array.umi_rome_x)))
            return Devices.Umi_Rome_X;
        else if (isDevice(res.getStringArray(array.Vivo_Xplay3s)))
            return Devices.Vivo_Xplay3s;
        else if(isDevice(res.getStringArray(array.wilexfox_swift)))
            return Devices.Wileyfox_Swift;
        else if (isDevice(res.getStringArray(array.mi_max)))
            return Devices.Mi_Max;
        else if (isDevice(res.getStringArray(array.Xiaomi_Mi3)))
            return Devices.XiaomiMI3W;
        else if (isDevice(res.getStringArray(array.Xiaomi_Mi4)))
            return Devices.XiaomiMI4W;
        else if(isDevice(res.getStringArray(array.Xiaomi_Mi4C)))
            return Devices.XiaomiMI4C;
        else if(isDevice(res.getStringArray(array.Xiaomi_MI5)))
            return Devices.XiaomiMI5;
        else if (isDevice(res.getStringArray(array.Xiaomi_Mi_Note_Pro)))
            return Devices.XiaomiMI_Note_Pro;
        else if (isDevice(res.getStringArray(array.Xiaomi_Mi_Note3_Pro)))
            return Devices.Xiaomi_Redmi_Note3;
        else if (isDevice(res.getStringArray(array.xiaomi_redmi3)))
            return Devices.Xiaomi_Redmi3;
        else if (isDevice(res.getStringArray(array.YuYureka)))
            return Devices.Yu_Yureka;
        else if (isDevice(res.getStringArray(array.zteadv)))
            return Devices.ZTE_ADV;
        else if (isDevice(res.getStringArray(array.zteadvIMX214)))
            return Devices.ZTEADVIMX214;
        else if (isDevice(res.getStringArray(array.zteadvIMX234)))
            return Devices.ZTEADV234;
        else if(isDevice(res.getStringArray(array.HTC_OneA9)))
            return Devices.HTC_OneA9;
        else if(isDevice(res.getStringArray(array.Lenovo_K50)))
            return Devices.Lenovo_K50_MTK;
        else if(isDevice(res.getStringArray(array.Forward_Art)))
            return Devices.ForwardArt_MTK;
        else if(isDevice(res.getStringArray(array.Huawei_p8Lite)))
            return Devices.p8lite;
        else if(isDevice(res.getStringArray(array.Huawei_p8)))
            return Devices.p8;
        else if (isDevice(res.getStringArray(array.prestigio_multipad_color)))
            return Devices.Prestigio_Multipad_Color;
        else if(isDevice(res.getStringArray(array.Huawei_Honor6)))
            return Devices.huawei_honor6;
        else if(isDevice(res.getStringArray(array.Alcatel_985N)))
            return Devices.Alcatel_985n;
        else if(isDevice(res.getStringArray(array.Jiayu_S3)))
            return Devices.Jiayu_S3;
        else if(isDevice(res.getStringArray(array.Aquaris_E5)))
            return Devices.Aquaris_E5;
        else if(isDevice(res.getStringArray(array.Lenovo_VibeP1)))
            return Devices.Lenovo_VibeP1;
        else if(isDevice(res.getStringArray(array.Huawei_GX8)))
            return Devices.Huawei_GX8;
        else if(isDevice(res.getStringArray(array.Huawei_Honor5X)))
            return Devices.Huawei_HONOR5x;
        else if(isDevice(res.getStringArray(array.HTC_One_E8)))
            return Devices.HTC_OneE8;
        else if(isDevice(res.getStringArray(array.Moto_G3)))
            return Devices.MotoG3;
        else if(isDevice(res.getStringArray(array.MotoG_Turbo)))
            return Devices.MotoG_Turbo;
        else if(isDevice(res.getStringArray(array.HTC_Desire500)))
            return Devices.HTC_Desire500;
        else if(isDevice(res.getStringArray(array.Blackberry_Priv)))
            return Devices.Blackberry_Priv;
        else if(isDevice(res.getStringArray(array.Lenovo_VibeShot_Z90)))
            return Devices.Lenovo_VibeShot_Z90;
        else if(isDevice(res.getStringArray(array.Zoppo_8speed)))
            return Devices.Zoppo_8speed;
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

}
