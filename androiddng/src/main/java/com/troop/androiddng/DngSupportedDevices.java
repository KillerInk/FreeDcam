package com.troop.androiddng;

import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 11.05.2015.
 */
public class DngSupportedDevices
{

    public final static int Mipi = 0;
    public final static int Qcom = 1;
    public final static int Plain = 2;

    public enum SupportedDevices
    {
        LG_G3,
        LG_G2,
        Lenovo_k910,
        Gione_E7,
        Sony_XperiaL,
        HTC_One_Sv,
        HTC_One_XL,
        HTC_One_m9,
        HTC_One_m8,
        OnePlusOne,
        yureka,
        zteAdv,
        Xiaomi_Redmi_Note,
        Xiaomi_mi_note_pro,
        Xiaomi_mi3,
        Meizu_Mx4,
        Meizu_Mx5,
        THL5000,
        Alcatel_Idol3,
        Vivo_Xplay3s,
        I_Mobile_I_StyleQ6

    }


    public static DngSupportedDevices.SupportedDevices getDevice()
    {
        if (DeviceUtils.isYureka())
            return DngSupportedDevices.SupportedDevices.yureka;
        if (DeviceUtils.isLG_G3())
            return DngSupportedDevices.SupportedDevices.LG_G3;
        if (DeviceUtils.isGioneE7())
            return DngSupportedDevices.SupportedDevices.Gione_E7;
        if (DeviceUtils.isHTC_M8())
            return DngSupportedDevices.SupportedDevices.HTC_One_m8;
        if (DeviceUtils.isHTC_M9())
            return DngSupportedDevices.SupportedDevices.HTC_One_m9;
        if (DeviceUtils.isHtc_One_SV())
            return DngSupportedDevices.SupportedDevices.HTC_One_Sv;
        if (DeviceUtils.isHtc_One_XL())
            return DngSupportedDevices.SupportedDevices.HTC_One_XL;
        if (DeviceUtils.isLenovoK910())
            return DngSupportedDevices.SupportedDevices.Lenovo_k910;
        if(DeviceUtils.isG2())
            return DngSupportedDevices.SupportedDevices.LG_G2;
        if (DeviceUtils.hasIMX135())
            return DngSupportedDevices.SupportedDevices.zteAdv;
        if (DeviceUtils.isXperiaL())
            return DngSupportedDevices.SupportedDevices.Sony_XperiaL;
        if(DeviceUtils.hasIMX214())
            return DngSupportedDevices.SupportedDevices.OnePlusOne;
        if (DeviceUtils.isRedmiNote())
            return DngSupportedDevices.SupportedDevices.Xiaomi_Redmi_Note;
        if (DeviceUtils.isXiaomiMI3W())
            return DngSupportedDevices.SupportedDevices.Xiaomi_mi3;
        if (DeviceUtils.isMeizuMX4())
            return DngSupportedDevices.SupportedDevices.Meizu_Mx4;
        if (DeviceUtils.isMeizuMX5())
            return DngSupportedDevices.SupportedDevices.Meizu_Mx5;
        if (DeviceUtils.isTHL5000())
            return DngSupportedDevices.SupportedDevices.THL5000;
        if (DeviceUtils.isXiaomiMI_Note_Pro())
            return DngSupportedDevices.SupportedDevices.Xiaomi_mi_note_pro;
        if (DeviceUtils.isAlcatel_Idol3())
            return DngSupportedDevices.SupportedDevices.Alcatel_Idol3;
        if(DeviceUtils.isVivo_Xplay3s())
            return DngSupportedDevices.SupportedDevices.Vivo_Xplay3s;
        if (DeviceUtils.isI_Mobile_I_StyleQ6())
            return SupportedDevices.I_Mobile_I_StyleQ6;
        return null;
    }

    /*
    Matrixes.Nex6CCM1,
    Matrixes.Nex6CCM2,
    Matrixes.Nex6NM,
    Matrixes.Nexus6_foward_matrix1,
    Matrixes.Nexus6_foward_matrix2,
    Matrixes.Nexus6_reduction_matrix1,
    Matrixes.Nexus6_reduction_matrix2,
    Matrixes.Nexus6_noise_3x1_matrix);
     */

    /*
    Matrixes.G4CCM1,
    Matrixes.G4CCM2,
    Matrixes.G4NM,
    Matrixes.G4_foward_matrix1,
    Matrixes.G4_foward_matrix2,
    Matrixes.G4_reduction_matrix1,
    Matrixes.G4_reduction_matrix2,
    Matrixes.G4_noise_3x1_matrix);
     */

    private DngProfile getG3Profile(int filesize)
    {
        DngProfile profile = null;
        switch (filesize)
        {
            case 17326080://qcom g3
                profile= new DngProfile(64, 4164, 3120,Qcom, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                break;
            case 17522688://QCOM
                profile = new DngProfile(64, 4212, 3082,Qcom, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                break;
            case 16224256:
                profile = new DngProfile(64, 4208, 3082,Mipi, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                break;
            case 16424960:
                return new DngProfile(64, 4208, 3120,Mipi, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 2658304: //g3 front mipi
                profile = new DngProfile(64,1212 ,1096 ,Mipi, BGGR, 2424,
                        Matrixes.G3Device.CC_A_FRONT,
                        Matrixes.G3Device.CC_D65_FRONT,
                        Matrixes.G3Device.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
                break;
            case 2842624://g3 front qcom
                //TODO somethings wrong with it;
                profile = new DngProfile(64, 1296 ,1096 ,Qcom, BGGR, 0,
                        Matrixes.G3Device.CC_A_FRONT,
                        Matrixes.G3Device.CC_D65_FRONT,
                        Matrixes.G3Device.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
                break;

        }
        return profile;
    }

    private DngProfile getG2Profile(int filesize)
    {
        switch (filesize)
        {
            case 16224256://MIPI g2
                return new DngProfile(64, 4208, 3082,Mipi, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 2969600://g2 mipi front
                return new DngProfile(64, 1236 ,1200 ,Mipi, BGGR, 2472,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getLenovoK910Profile(int filesize)
    {
        switch (filesize)
        {
            case 17522688://QCOM
                return new DngProfile(64, 4212, 3082,Qcom, BGGR, getG3_rowSizeL, Matrixes.G4CCM1,Matrixes.G4CCM2,Matrixes.G4NM,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
                /*return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL,
                        Matrixes.g3_color1,
                        Matrixes.g3_color2,
                        Matrixes.g3_neutral);*/
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                return new DngProfile(64, 4208, 3120,Mipi, BGGR, getG3_rowSizeL,Matrixes.Nex6CCM1,Matrixes.Nex6CCM2,Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                /*return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,
                        Matrixes.g3_color1,
                        Matrixes.g3_color2,
                        Matrixes.g3_neutral);*/
            case 6721536: //k910/zte front qcom
                return new DngProfile(64, 2592 ,1296 ,Qcom, BGGR, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 6299648://k910/zte front mipi
                return new DngProfile(16, 2592 ,1944 ,Mipi, BGGR, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);

        }
        return null;
    }

    private DngProfile getZTEADVProfile(int filesize)
    {
        switch (filesize)
        {
            case 17522688://QCOM
                return new DngProfile(64, 4212, 3120,Qcom, BGGR, getG3_rowSizeL, Matrixes.G4CCM1,Matrixes.G4CCM2,Matrixes.G4NM,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
                /*return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL,
                        Matrixes.g3_color1,
                        Matrixes.g3_color2,
                        Matrixes.g3_neutral);*/
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                return new DngProfile(64, 4208, 3120,Mipi, BGGR, getG3_rowSizeL,Matrixes.Nex6CCM1,Matrixes.Nex6CCM2,Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                /*return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,
                        Matrixes.g3_color1,
                        Matrixes.g3_color2,
                        Matrixes.g3_neutral);*/
            case 6721536: //k910/zte front qcom
                return new DngProfile(64, 2592 ,1296 ,Qcom, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 6299648://k910/zte front mipi
                return new DngProfile(16, 2592 ,1944 ,Mipi, BGGR, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);

        }
        return null;
    }

    private DngProfile getGioneeE7Profile(int filesize)
    {
        switch (filesize)
        {
            case 19906560://e7mipi
                return new DngProfile(0, 4608, 3456,Mipi, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case  21233664: //e7qcom
                return new DngProfile(0, 4608, 3456,Qcom, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case  9990144://e7 front mipi
                return new DngProfile(0, 2040 , 2448,Mipi, BGGR, 4080,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case  10653696://e7 front qcom
            //TODO somethings wrong with it;
                return new DngProfile(0,2176 , 2448,Qcom, BGGR, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getHTCM9Profile(int filesize)
    {
        switch (filesize)
        {
            case  25677824://m9 mipi
                return new DngProfile(64, 5388, 3752,Mipi, GRBG, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 27127808://m9 qcom
                return new DngProfile(64, 5388, 3752,Qcom, GRBG, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getHTCM8Profile(int filesize)
    {
        if (filesize< 6000000 && filesize > 5382641)//M8 qcom
            return new DngProfile(0, 2688, 1520,Qcom, GRBG, 0,
                    Matrixes.Nex6CCM1,
                    Matrixes.Nex6CCM2,
                    Matrixes.Nex6NM,
                    Matrixes.Nexus6_foward_matrix1,
                    Matrixes.Nexus6_foward_matrix2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.Nexus6_noise_3x1_matrix);
        else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
            return new DngProfile(0, 2688, 1520,Mipi, GRBG, HTCM8_rowSize,
                    Matrixes.Nex6CCM1,
                    Matrixes.Nex6CCM2,
                    Matrixes.Nex6NM,
                    Matrixes.Nexus6_foward_matrix1,
                    Matrixes.Nexus6_foward_matrix2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.Nexus6_noise_3x1_matrix);
        return null;
    }


    private DngProfile getXiamoi_mi3WProfile(int filesize)
    {
        switch (filesize)
        {
            case 17522688://QCOM
                return new DngProfile(0, 4212, 3120,Qcom, RGGb, getG3_rowSizeL,Matrixes.Nex6CCM1,Matrixes.Nex6CCM2,Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
                /*return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL,
                        Matrixes.g3_color1,
                        Matrixes.g3_color2,
                        Matrixes.g3_neutral);*/
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                return new DngProfile(64, 4208, 3120,Mipi, RGGb, getG3_rowSizeL,Matrixes.Nex6CCM1,Matrixes.Nex6CCM2,Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getOnePlusOneProfile(int filesize)
    {
        switch (filesize)
        {
            case 17522688: //qcom
                return new DngProfile(64, 4212, 3082,Qcom, RGGb, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 16424960: //mipi
                return new DngProfile(64, 4212, 3082,Mipi, RGGb, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getXiaomi_mi_note_proProfile(int filesize)
    {
        switch (filesize)
        {
            case 17612800: //qcom
                return new DngProfile(64,4208,3120,Qcom, RGGb, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 16560128: //mipi
                return new DngProfile(64,4208,3120,Mipi, RGGb, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getAlcatel_Idol3Profile(int filesize)
    {
        switch (filesize)
        {
            case 17522688: //qcom
                return new DngProfile(64,4208,3120,Qcom, RGGb, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 16424960: //mipi
                return new DngProfile(64,4208,3120,Mipi, RGGb, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
        }
        return null;
    }

    private DngProfile getXiamoi_Redmi_Note_Profile(int filesize)
    {
        switch (filesize)
        {
            //TODO find correct imageSize
            case 16424960:
                return new DngProfile(64,4208, 3120,Mipi, RGGb, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 17522688:
                return new DngProfile(64,4212, 3082,Qcom, RGGb, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 6299648:
                return new DngProfile(16, 2592 ,1944 ,Mipi, BGGR, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 6721536:
                return new DngProfile(64, 2592 ,1296 ,Qcom, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);


        }
        return null;
    }

    private DngProfile getVivo_Xplay3s_Profile(int filesize)
    {
        switch (filesize)
        {
            //TODO find correct imageSize
            case 16424960:
                return new DngProfile(64,4208, 3120,Mipi, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 17522688:
                return new DngProfile(64,4212, 3082,Qcom, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);


        }
        return null;
    }

    public DngProfile getProfile(SupportedDevices device, int filesize)
    {
        switch (device) {
            case LG_G3:
                return getG3Profile(filesize);
            case LG_G2:
                return getG2Profile(filesize);
            case Lenovo_k910:
                return getLenovoK910Profile(filesize);
            case Gione_E7:
                return getGioneeE7Profile(filesize);
            case Sony_XperiaL:
                return new DngProfile(64, 3282, 2448,Qcom, BGGR, XperiaL_rowSize,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case HTC_One_Sv:
                return new DngProfile(64, 2592, 1944,Qcom, GRBG, 0,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case HTC_One_XL:
                return new DngProfile(0, 3282, 2448,Qcom, GRBG, XperiaL_rowSize,
                        Matrixes.nocal_color1,
                        Matrixes.nocal_color2,
                        Matrixes.nocal_nutral,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case HTC_One_m9:
                return getHTCM9Profile(filesize);
            case HTC_One_m8:
                return getHTCM8Profile(filesize);
            case OnePlusOne:
                return getOnePlusOneProfile(filesize);
            case yureka:
                return new DngProfile(0, 4212, 3082,Qcom, BGGR, getG3_rowSizeL,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case zteAdv:
                return getZTEADVProfile(filesize);
            case Xiaomi_Redmi_Note:
                return getXiamoi_Redmi_Note_Profile(filesize);
            case Xiaomi_mi3:
                return getXiamoi_mi3WProfile(filesize);
            case Meizu_Mx4:
                return new DngProfile(64,5248, 3936,Plain, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case Meizu_Mx5:
                return new DngProfile(64,5248, 3936,Plain, BGGR, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case THL5000:
                return new DngProfile(64,4192, 3104,Plain, RGGb, 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case I_Mobile_I_StyleQ6:
                return new DngProfile(64,2532, 1902,Plain,GRBG , 0,
                        Matrixes.Nex6CCM1,
                        Matrixes.Nex6CCM2,
                        Matrixes.Nex6NM,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case Xiaomi_mi_note_pro:
                return getXiaomi_mi_note_proProfile(filesize);
            case Alcatel_Idol3:
                return getAlcatel_Idol3Profile(filesize);
            case Vivo_Xplay3s:
                return getVivo_Xplay3s_Profile(filesize);

        }
        return null;
    }

    public class DngProfile
    {


        public int blacklevel;
        public int widht;
        public int height;
        public int rawType;
        public String BayerPattern;
        public int rowsize;
        float[]matrix1;
        float[]matrix2;
        float[]neutral;
        float[]fowardmatrix1;
        float[]fowardmatrix2;
        float[]reductionmatrix1;
        float[]reductionmatrix2;
        float[]noiseprofile;

        public DngProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, float[]matrix1, float[] matrix2, float[]neutral,float[]fmatrix1, float[] fmatrix2,float[]rmatrix1, float[] rmatrix2,float[]noise)
        {
            this.blacklevel = blacklevel;
            this.widht = widht;
            this.height = height;
            this.rawType = rawType;
            this.BayerPattern = bayerPattern;
            this.rowsize = rowsize;
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
            this.neutral = neutral;
            this.fowardmatrix1 = fmatrix1;
            this.fowardmatrix2 = fmatrix2;
            this.reductionmatrix1 = rmatrix1;
            this.reductionmatrix2 = rmatrix2;
            this.noiseprofile = noise;

        }

    }
    private static final int g3_blacklevel = 64;
    //16424960,4208,3120
    private static final int g3_rowSizeKitKat = 5264;
    //16224256,4152,3072
    private static final int getG3_rowSizeL = 5264;
    private static final int HTCM8_rowSize = 3360;
    private static String HTCM8_Size= "2688x1520";
    //Rawsize =  10788864
    //RealSize = 10712448
    private static final int XperiaL_rowSize = 4376;
    public static String SonyXperiaLRawSize = "3282x2448";
    public static String Optimus3DRawSize = "2608x1944";
    public static String BGGR = "bggr";
    public static String RGGb = "rggb";
    private static final String GRBG = "grbg";
    private static final String gbrg =  "gbrg";
}
