package com.troop.androiddng;

import com.troop.freedcam.utils.DeviceUtils.Devices;

/**
 * Created by troop on 11.05.2015.
 */
public class DngSupportedDevices
{

    public final static int Mipi = 0;
    public final static int Qcom = 1;
    public final static int Plain = 2;
    public final static int Mipi16 = 3;

    public DngProfile getProfile(Devices device, int filesize)
    {
        switch (filesize) {
            case 9830400: //NGM Forward Art
				return new DngProfile(16, 2560, 1920, Plain, BGGR, 0,
						Matrixes.Nexus6_identity_matrix1,
                        Matrixes.Nexus6_identity_matrix2,
                        Matrixes.Nexus6_identity_neutra,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 2658304: //g3 front mipi
                return new DngProfile(64, 1212, 1096, Mipi, BGGR, 2424,
                        Matrixes.G3Device.CC_A_FRONT,
                        Matrixes.G3Device.CC_D65_FRONT,
                        Matrixes.G3Device.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 2842624://g3 front qcom
                //TODO somethings wrong with it;
                return new DngProfile(64, 1296, 1096, Qcom, BGGR, 0,
                        Matrixes.G3Device.CC_A_FRONT,
                        Matrixes.G3Device.CC_D65_FRONT,
                        Matrixes.G3Device.neutral_light_front,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 2969600:
                switch (device) {
                    case XiaomiMI3W:
                        return new Nexus6Profile(64, 1976, 1200, Mipi16, RGGB, 0);
                    default:
                        return new Nexus6Profile(64, 1236, 1200, Mipi, BGGR, 2472);//g2 mipi front
                }
            case 3170304://Xiaomi_mi3 front Qcom
                        return new Nexus6Profile(0, 1976, 1200, Qcom, RGGB, 0);

            case 42923008://Moto_MSM8982_8994
                return new DngProfile(64,5344 ,4016 ,Plain, RGGB, 0,
                        Matrixes.imx230_identity_matrix1,
                        Matrixes.imx230_identity_matrix2,
                        Matrixes.imx230_identity_neutra,
                        Matrixes.imx230_foward_matrix1,
                        Matrixes.imx230_foward_matrix2,
                        Matrixes.imx230_reduction_matrix1,
                        Matrixes.imx230_reduction_matrix2,
                        Matrixes.imx230_3x1_matrix);
            case 26257920://SOny C5 probable imx 214 rggb
                return new DngProfile(64, 4208, 3120, Plain, RGGB, 0, Matrixes.imx214_identity_matrix1, Matrixes.imx214_identity_matrix2, Matrixes.Nexus6_identity_neutra,
                        Matrixes.imx214_foward_matrix1,
                        Matrixes.imx214_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 25958400://Jiayu_S3
                return new DngProfile(64, 4160, 3120, Plain, RGGB, 0, Matrixes.imx214_identity_matrix1, Matrixes.imx214_identity_matrix2, Matrixes.Nexus6_identity_neutra,
                        Matrixes.imx214_foward_matrix1,
                        Matrixes.imx214_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 26357760: //oneplus
                return new DngProfile(16,4224 ,3120 ,Plain, BGGR, 0,
                        Matrixes.Nexus6_identity_matrix1,
                        Matrixes.Nexus6_identity_matrix2,
                        Matrixes.Nexus6_identity_neutra,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 16473600: //oneplus
                return new DngProfile(16,4224 ,3120 ,Mipi, BGGR, 5280,
                        Matrixes.Nexus6_identity_matrix1,
                        Matrixes.Nexus6_identity_matrix2,
                        Matrixes.Nexus6_identity_neutra,
                        Matrixes.Nexus6_foward_matrix1,
                        Matrixes.Nexus6_foward_matrix2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.Nexus6_noise_3x1_matrix);
            case 6299648: {
                return new DngProfile(16, 2592, 1944, Mipi, BGGR, 0,
                        Matrixes.OV_matrix1,
                        Matrixes.OV_matrix2,
                        Matrixes.OV_ASSHOT,
                        Matrixes.OV_Foward,
                        Matrixes.OV_Foward2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.OV_NREDUCTION_Matrix);
            }
            case 6746112:// Htc One SV
                return new Nexus6Profile(64, 2592, 1944, Qcom, GRBG, 0);
            case (6721536): {
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new Nexus6Profile(64, 2592, 1296, Qcom, BGGR, 0);
                    case ZTE_ADV:
                        return new Nexus6Profile(64, 2592, 1296, Qcom, BGGR, 0);
                    case LenovoK910:
                        return new DngProfile(64, 2592, 1296, Qcom, BGGR, 0,
                                Matrixes.nocal_color1,
                                Matrixes.nocal_color2,
                                Matrixes.nocal_nutral,
                                Matrixes.Nexus6_foward_matrix1,
                                Matrixes.Nexus6_foward_matrix2,
                                Matrixes.Nexus6_reduction_matrix1,
                                Matrixes.Nexus6_reduction_matrix2,
                                Matrixes.Nexus6_noise_3x1_matrix);
                    default:
                        return new Nexus6Profile(64, 2592, 1296, Qcom, BGGR, 0);
                }
            }
            case 3763584: //I_Mobile_I_StyleQ6
                return new Nexus6Profile(0, 1584, 1184, Plain, GRBG, 0);
            case 9631728: //I_Mobile_I_StyleQ6
            return new DngProfile(0, 2532, 1902, Plain, GRBG, 0,
                    Matrixes.OV_matrix1,
                    Matrixes.OV_matrix2,
                    Matrixes.OV_ASSHOT,
                    Matrixes.OV_Foward,
                    Matrixes.OV_Foward2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.OV_NREDUCTION_Matrix);
            case 9990144://e7 front mipi
                return new Nexus6Profile(16, 3264, 2448, Mipi, BGGR, 4080);
            case 10782464: //HTC one xl
                return new Nexus6Profile(64, 2592, 1944, Qcom, GRBG, 0);
            case 10788864: //xperia L
                return new Nexus6Profile(64, 3282, 2448, Qcom, BGGR, XperiaL_rowSize);
            case 10653696://e7 front qcom
            {
                //TODO somethings wrong with it;
                return new Nexus6Profile(16, 3264, 2448, Qcom, BGGR, 0);
            }
            case 16224256://MIPI g2
            {
                switch (device) {
                    case LG_G2:
                        return new Nexus6Profile(64, 4208, 3082, Mipi, BGGR, getG3_rowSizeL);
                    case LG_G3:
                        return new Nexus6Profile(64, 4208, 3082, Mipi, BGGR, getG3_rowSizeL);
                    default:
                        return new Nexus6Profile(64, 4208, 3082, Mipi, BGGR, getG3_rowSizeL);
                }
            }
            case (16424960): {
                switch (device) {
                    case Vivo_Xplay3s:
                        return new Nexus6Profile(64, 4212, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case Aquaris_E5:
                    case RedmiNote:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case Lenovo_VibeP1: //Says GRBG unsure if correct to be confirmed
                        return new Nexus6Profile(64, 4208, 3120, Mipi, GRBG, getG3_rowSizeL);
                    case XiaomiMI3W:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case XiaomiMI4W:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case Alcatel_Idol3:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, 0);
                    case Alcatel_Idol3_small:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case OnePlusOne:
                        return new Nexus6Profile(64, 4212, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case SonyM4_QC:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case ZTE_ADV:
                       // return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                        return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case ZTEADVIMX214:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    case LenovoK910:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case LG_G2:
                        return new Nexus6Profile(64, 4212, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case LG_G3:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case Yu_Yureka:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, BGGR, getG3_rowSizeL);
                    case HTC_OneA9:
                        return new Nexus6Profile(64, 4208, 3120, Mipi, RGGB, getG3_rowSizeL);
                    default:
                        return new Nexus6Profile(64, 4212, 3082, Mipi, BGGR, getG3_rowSizeL);
                }
            }
            case (16510976)://mi 4c
                return new Nexus6Profile(64,4208,3120,Mipi16,BGGR,0);
            case (16560128): {
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new Nexus6Profile(64, 4208, 3120, Mipi16, RGGB, 0);
                    default:
                        return new Nexus6Profile(64, 4212, 3120, Mipi, RGGB, 0);
                }
            }
            case 17326080://qcom g3
                return new Nexus6Profile(64, 4164, 3120, Qcom, BGGR, getG3_rowSizeL);
            case 17522688: {
                switch (device) {
                    case Vivo_Xplay3s:
                        return new Nexus6Profile(64, 4208, 3120, Qcom, BGGR, getG3_rowSizeL);
                    case RedmiNote:
                        return new Nexus6Profile(64, 4212, 3082, Qcom, RGGB, getG3_rowSizeL);
                    case XiaomiMI3W:
                        return new Nexus6Profile(0, 4212, 3120, Qcom, RGGB, getG3_rowSizeL);
                    case XiaomiMI4W:
                        return new Nexus6Profile(0, 4212, 3120, Qcom, RGGB, getG3_rowSizeL);
                    case Alcatel_Idol3:
                        return new Nexus6Profile(64, 4208, 3120, Qcom, RGGB, 0);
                    case OnePlusOne:
                        return new Nexus6Profile(64, 4212, 3082, Qcom, RGGB, getG3_rowSizeL);
                    case ZTE_ADV:
                        return new DngProfile(64, 4212, 3120, Qcom, BGGR, getG3_rowSizeL, Matrixes.G4CCM1, Matrixes.G4CCM2, Matrixes.G4NM,
                                Matrixes.G4_foward_matrix1,
                                Matrixes.G4_foward_matrix2,
                                Matrixes.G4_reduction_matrix1,
                                Matrixes.G4_reduction_matrix2,
                                Matrixes.G4_noise_3x1_matrix);
                    case ZTEADVIMX214:
                        return new DngProfile(64, 4212, 3120, Qcom, RGGB, getG3_rowSizeL, Matrixes.Nexus6_identity_matrix1, Matrixes.Nexus6_identity_matrix2, Matrixes.Nexus6_identity_neutra,
                                Matrixes.Nexus6_foward_matrix1,
                                Matrixes.Nexus6_foward_matrix2,
                                Matrixes.Nexus6_reduction_matrix1,
                                Matrixes.Nexus6_reduction_matrix2,
                                Matrixes.Nexus6_noise_3x1_matrix);
                    case LenovoK910:
                        return new DngProfile(64, 4212, 3120, Qcom, BGGR, getG3_rowSizeL, Matrixes.G4CCM1, Matrixes.G4CCM2, Matrixes.G4NM,
                                Matrixes.G4_foward_matrix1,
                                Matrixes.G4_foward_matrix2,
                                Matrixes.G4_reduction_matrix1,
                                Matrixes.G4_reduction_matrix2,
                                Matrixes.G4_noise_3x1_matrix);
                    case LG_G3:
                        return new Nexus6Profile(64, 4212, 3082, Qcom, BGGR, getG3_rowSizeL);
                    case Yu_Yureka:
                        return new Nexus6Profile(0, 4212, 3082, Qcom, BGGR, getG3_rowSizeL);
                    default:
                        return new Nexus6Profile(64, 4208, 3120, Qcom, BGGR, getG3_rowSizeL);
                }
            }
            case 17612800: {
                switch (device) {
                    case XiaomiMI_Note_Pro:
                        return new Nexus6Profile(64, 4212, 3120, Qcom, RGGB, 0);
                    default:
                        return new Nexus6Profile(64, 4212, 3120, Qcom, RGGB, 0);
                }
            }
            case 19906560://e7mipi
                //return new Nexus6Profile(0, 4608, 3456, Mipi, BGGR, 0);
            return new DngProfile(16, 4608, 3456, Mipi, BGGR, 0,
                    Matrixes.OV_matrix1,
                    Matrixes.OV_matrix2,
                    Matrixes.OV_ASSHOT,
                    Matrixes.OV_Foward,
                    Matrixes.OV_Foward2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.OV_NREDUCTION_Matrix);
            case 19992576:  //lenovo k920
                return new Nexus6Profile(64, 5328,3000, Mipi, GBRG, 0);
            case 19976192: //g4 bayer mipi camera1
                return new DngProfile(64, 5312,2988,Mipi16, BGGR,0,
                Matrixes.G4_identity_matrix1,
                        Matrixes.G4_identity_matrix2,
                        Matrixes.G4_identity_neutra,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 20389888: //xiaomi note3 pro
                return new Nexus6Profile(64, 4632, 3480, Mipi16, GRBG, 0);
            case 21233664: //e7qcom
                return new DngProfile(16, 4608, 3456, Qcom, BGGR, 0,
                        Matrixes.OV_matrix1,
                        Matrixes.OV_matrix2,
                        Matrixes.OV_ASSHOT,
                        Matrixes.OV_Foward,
                        Matrixes.OV_Foward2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.OV_NREDUCTION_Matrix);
               // return new Nexus6Profile(0, 4608, 3456, Qcom, BGGR, 0);
            case 25677824://m9 mipi
                return new Nexus6Profile(64, 5388, 3752, Mipi16, GRBG, 0);
            case 20041728:
                return new DngProfile(64, 5344,3000,Mipi16, RGGB,0,
                        Matrixes.G4_identity_matrix1,
                        Matrixes.G4_identity_matrix2,
                        Matrixes.G4_identity_neutra,
                        Matrixes.G4_foward_matrix1,
                        Matrixes.G4_foward_matrix2,
                        Matrixes.G4_reduction_matrix1,
                        Matrixes.G4_reduction_matrix2,
                        Matrixes.G4_noise_3x1_matrix);
            case 26023936: //THL 5000 MTK, Redmi note2
                switch (device)
                {
                    case THL5000_MTK:
                        return new Nexus6Profile(64, 4192, 3104, Plain, RGGB, 0);
                    case RedmiNote2_MTK:
                        return new Nexus6Profile(64, 4192, 3104, Plain, GBRG, 0);
                    case Lenovo_K50_MTK:
                        return new Nexus6Profile(16, 4192, 3104, Plain, BGGR, 0);
                    case Lenovo_K4Note_MTK:
                        return new Nexus6Profile(16, 4192, 3104, Plain, GBRG, 0);
                    default:
                        return new Nexus6Profile(64, 4192, 3104, Plain, RGGB, 0);

                }
            case 27127808: //HTC M9 QCom
                return new Nexus6Profile(64, 5388, 3752, Qcom, GRBG, 0);
            case 41312256: // Meizu MX4/5
                return new Nexus6Profile(64, 5248, 3936, Plain, BGGR, 0);
            case 5364240: //testing matrix
                return new DngProfile(0, 2688, 1520, Mipi, GRBG, HTCM8_rowSize,
                        Matrixes.OV_matrix1,
                        Matrixes.OV_matrix2,
                        Matrixes.OV_ASSHOT,
                        Matrixes.OV_Foward,
                        Matrixes.OV_Foward2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.OV_NREDUCTION_Matrix);


        }
        if (device == Devices.LG_G4)
            return new DngProfile(64, 5312,2988,Mipi, BGGR,0,
                    Matrixes.G4_identity_matrix1,
                    Matrixes.G4_identity_matrix2,
                    Matrixes.G4_identity_neutra,
                    Matrixes.G4_foward_matrix1,
                    Matrixes.G4_foward_matrix2,
                    Matrixes.G4_reduction_matrix1,
                    Matrixes.G4_reduction_matrix2,
                    Matrixes.G4_noise_3x1_matrix);
        if (device == Devices.Htc_M8)
        {
            if (filesize < 6000000 && filesize > 5382641)
            return new DngProfile(0, 2688, 1520, Qcom, GRBG, 0,
                    Matrixes.OV_matrix1,
                    Matrixes.OV_matrix2,
                    Matrixes.OV_ASSHOT,
                    Matrixes.OV_Foward,
                    Matrixes.OV_Foward2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.OV_NREDUCTION_Matrix);
            else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
                return new DngProfile(0, 2688, 1520, Mipi16, GRBG, HTCM8_rowSize,
                        Matrixes.OV_matrix1,
                        Matrixes.OV_matrix2,
                        Matrixes.OV_ASSHOT,
                        Matrixes.OV_Foward,
                        Matrixes.OV_Foward2,
                        Matrixes.Nexus6_reduction_matrix1,
                        Matrixes.Nexus6_reduction_matrix2,
                        Matrixes.OV_NREDUCTION_Matrix);
            return null;
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
        public float[]matrix1;
        public float[]matrix2;
        public float[]neutral;
        public float[]fowardmatrix1;
        public float[]fowardmatrix2;
        public float[]reductionmatrix1;
        public float[]reductionmatrix2;
        public float[]noiseprofile;

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

    public DngProfile getProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, float[]matrix1, float[] matrix2, float[]neutral,float[]fmatrix1, float[] fmatrix2,float[]rmatrix1, float[] rmatrix2,float[]noise)
    {
        return new DngProfile(blacklevel,widht,height,rawType,bayerPattern,rowsize,matrix1,matrix2,neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise);
    }

    public class Nexus6Profile extends DngProfile
    {
        public Nexus6Profile(int blacklevel, int widht, int height, int rawType, String bayerPattern, int rowsize) {
            super(blacklevel, widht, height, rawType, bayerPattern, rowsize, Matrixes.Nex6CCM1,
                    Matrixes.Nex6CCM2,
                    Matrixes.Nex6NM,
                    Matrixes.Nexus6_foward_matrix1,
                    Matrixes.Nexus6_foward_matrix2,
                    Matrixes.Nexus6_reduction_matrix1,
                    Matrixes.Nexus6_reduction_matrix2,
                    Matrixes.Nexus6_noise_3x1_matrix);
        }
    }

    public DngProfile GetEmptyProfile()
    {
        return new DngProfile(0,0,0,0,"bggr",0,
            Matrixes.Nex6CCM1,
            Matrixes.Nex6CCM2,
            Matrixes.Nex6NM,
            Matrixes.Nexus6_foward_matrix1,
            Matrixes.Nexus6_foward_matrix2,
            Matrixes.Nexus6_reduction_matrix1,
            Matrixes.Nexus6_reduction_matrix2,
            Matrixes.Nexus6_noise_3x1_matrix);
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
    public static String RGGB = "rggb";
    public static final String GRBG = "grbg";
    public static final String GBRG =  "gbrg";
}
