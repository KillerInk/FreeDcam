package com.troop.androiddng;

import com.troop.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 11.05.2015.
 */
public class DngSupportedDevices
{
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

    }

    private DngProfile getG3Profile(int filesize)
    {
        DngProfile profile = null;
        switch (filesize)
        {
            case 17326080://qcom g3
                profile= new DngProfile(64, 4164, 3120,false, BGGR, getG3_rowSizeL,G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
                break;
            case 17522688://QCOM
                profile = new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL, G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
                break;
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                profile = new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
                break;
            case 2658304: //g3 front mipi
                profile = new DngProfile(64,1212 ,1096 ,true, BGGR, 2424,G3Device.CC_A_FRONT,G3Device.CC_D65_FRONT,G3Device.neutral_light_front);
                break;
            case 2842624://g3 front qcom
                //TODO somethings wrong with it;
                profile = new DngProfile(64, 1296 ,1096 ,false, BGGR, 0,G3Device.CC_A_FRONT,G3Device.CC_D65_FRONT,G3Device.neutral_light_front);
                break;

        }
        return profile;
    }

    private DngProfile getG2Profile(int filesize)
    {
        switch (filesize)
        {
            case 16224256://MIPI g2
                return new DngProfile(64, 4208, 3082,true, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
            case 2969600://g2 mipi front
                return new DngProfile(64, 1236 ,1200 ,true, BGGR, 2472,g3_color1,g3_color2,g3_neutral);
        }
        return null;
    }

    private DngProfile getLenovoK910Profile(int filesize)
    {
        switch (filesize)
        {
            case 17522688://QCOM
                return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL, G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
            case 6721536: //k910/zte front qcom
                return new DngProfile(64, 2592 ,1296 ,false, BGGR, 0,g3_color1,g3_color2,g3_neutral);
            case 6299648://k910/zte front mipi
                return new DngProfile(16, 2592 ,1944 ,true, BGGR, 0,g3_color1,g3_color2,g3_neutral);

        }
        return null;
    }

    private DngProfile getZTEADVProfile(int filesize)
    {
        switch (filesize)
        {
            case 17522688://QCOM
                return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL, G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
            case 16424960://lenovo k910 mipi , g3 kk mipi, zte
                return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,nubia_color1,nubia_color2,nubia_neutral);
            case 6721536: //k910/zte front qcom
                return new DngProfile(64, 2592 ,1296 ,false, BGGR, 0,g3_color1,g3_color2,g3_neutral);
            case 6299648://k910/zte front mipi
                return new DngProfile(16, 2592 ,1944 ,true, BGGR, 0,g3_color1,g3_color2,g3_neutral);

        }
        return null;
    }

    private DngProfile getGioneeE7Profile(int filesize)
    {
        switch (filesize)
        {
            case 19906560://e7mipi
                return new DngProfile(0, 4608, 3456,true, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
            case  21233664: //e7qcom
                return new DngProfile(0, 4608, 3456,false, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
            case  9990144://e7 front mipi
                return new DngProfile(0, 2040 , 2448,true, BGGR, 4080,nocal_color1,nocal_color2,nocal_nutral);
            case  10653696://e7 front qcom
            //TODO somethings wrong with it;
                return new DngProfile(0,2176 , 2448,false, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
        }
        return null;
    }

    private DngProfile getHTCM9Profile(int filesize)
    {
        switch (filesize)
        {
            case  25677824://m9 mipi
                return new DngProfile(64, 5388, 3752,true, GRBG, 0,m9_color1,m9_color2,m9_neutral);
            case 27127808://m9 qcom
                return new DngProfile(64, 5388, 3752,false, GRBG, 0,m9_color1,m9_color2,m9_neutral);
        }
        return null;
    }

    private DngProfile getHTCM8Profile(int filesize)
    {
        if (filesize< 6000000 && filesize > 5382641)//M8 qcom
            return new DngProfile(0, 2688, 1520,false, GRBG, 0,nocal_color1,nocal_color2,nocal_nutral);
        else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
            return new DngProfile(0, 2688, 1520,true, GRBG, HTCM8_rowSize,nocal_color1,nocal_color2,nocal_nutral);
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
                getGioneeE7Profile(filesize);
                break;
            case Sony_XperiaL:
                return new DngProfile(64, 3282, 2448,false, BGGR, XperiaL_rowSize,nocal_color1,nocal_color2,nocal_nutral);
            case HTC_One_Sv:
                return new DngProfile(0, 2592, 1944,false, GRBG, 0,nocal_color1,nocal_color2,nocal_nutral);
            case HTC_One_XL:
                return new DngProfile(0, 3282, 2448,false, GRBG, XperiaL_rowSize,nocal_color1,nocal_color2,nocal_nutral);
            case HTC_One_m9:
                return getHTCM9Profile(filesize);
            case HTC_One_m8:
                return getHTCM8Profile(filesize);
            case OnePlusOne:
                return new DngProfile(0, 4212, 3082,false, RGGb, getG3_rowSizeL,nocal_color2,nocal_color2,nocal_nutral);
            case yureka:
                return new DngProfile(0, 4212, 3082,false, BGGR, getG3_rowSizeL,nocal_color2,nocal_color2,nocal_nutral);
            case zteAdv:
                return getZTEADVProfile(filesize);
            case Xiaomi_Redmi_Note:
                return new DngProfile(64,4212, 3082,false, BGGR, getG3_rowSizeL, nocal_color1,nocal_color2,nocal_nutral);
        }


        /*if (filesize == 16224256)//MIPI g2
        {
            return new DngProfile(64, 4208, 3082,true, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
        }*/
        //else if (filesize == 17326080)//qcom g3
          //  return new DngProfile(64, 4164, 3120,false, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
/*        else if (filesize == 17522688)//QCOM
        {
            switch (device)
            {
                //case Lenovo_k910:
                //    return new DngProfile(64, 4212, 3120,false, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
                //case LG_G3:
                    //return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL, G3Device.CC_A,G3Device.CC_D65,G3Device.neutral_NormalLight);
                case zteAdv:
                    return new DngProfile(64, 4212, 3082,false, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
                case yureka:
                    return new DngProfile(0, 4212, 3082,false, BGGR, getG3_rowSizeL,nocal_color2,nocal_color2,nocal_nutral);
                case OnePlusOne:
                    return new DngProfile(0, 4212, 3082,false, RGGb, getG3_rowSizeL,nocal_color2,nocal_color2,nocal_nutral);
                case Xiaomi_Redmi_Note:
                    return new DngProfile(64,4212, 3082,false, BGGR, getG3_rowSizeL, nocal_color1,nocal_color2,nocal_nutral);

            }
        }*/
/*        else if (filesize == 16424960)//lenovo k910 mipi , g3 kk mipi, zte
        {
            if(DeviceUtils.isZTEADV())
            {
                return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,nubia_color1,nubia_color2,nubia_neutral);
            }
            else
            {
                return new DngProfile(64, 4208, 3120,true, BGGR, getG3_rowSizeL,g3_color1,g3_color2,g3_neutral);
            }

        }*/
        /*else if (filesize == 10788864)//XperiaL
        {
            return new DngProfile(64, 3282, 2448,false, BGGR, XperiaL_rowSize,nocal_color1,nocal_color2,nocal_nutral);
        }*/
       /* else if (filesize == 6746112)//htc one sv
        {
            return new DngProfile(0, 2592, 1944,false, GRBG, 0,nocal_color1,nocal_color2,nocal_nutral);
        }*/
/*        else if (filesize == 10782464) //htc one xl
        {
            return new DngProfile(0, 3282, 2448,false, GRBG, XperiaL_rowSize,nocal_color1,nocal_color2,nocal_nutral);
        }*/
        /*else if (filesize == 25677824)//m9 mipi
        {
            return new DngProfile(64, 5388, 3752,true, GRBG, 0,m9_color1,m9_color2,m9_neutral);
        }
        else if (filesize == 27127808)//m9 qcom
        {
            return new DngProfile(64, 5388, 3752,false, GRBG, 0,m9_color1,m9_color2,m9_neutral);
        }*/
        /*else if (filesize == 19906560)//e7mipi
        {
            return new DngProfile(0, 4608, 3456,true, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
        }
        else if (filesize == 21233664) //e7qcom
        {
            return new DngProfile(0, 4608, 3456,false, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
        }
        else if (filesize == 9990144)//e7 front mipi
        {
            return new DngProfile(0, 2040 , 2448,true, BGGR, 4080,nocal_color1,nocal_color2,nocal_nutral);
        }
        else if (filesize == 10653696)//e7 front qcom
        {
            //TODO somethings wrong with it;
            return new DngProfile(0,2176 , 2448,false, BGGR, 0,nocal_color1,nocal_color2,nocal_nutral);
        }*/
        //else if (filesize == 2658304) //g3 front mipi
        //{
        //    return new DngProfile(64,1212 ,1096 ,true, BGGR, 2424,g3_color1,g3_color2,g3_neutral);
        //}
        //else if (filesize == 2842624)//g3 front qcom
        //{
        //    //TODO somethings wrong with it;
        //    return new DngProfile(64, 1296 ,1096 ,false, BGGR, 0,g3_color1,g3_color2,g3_neutral);
        //}
        /*else if (filesize == 2969600)//g2 mipi front
        {
            return new DngProfile(64, 1236 ,1200 ,true, BGGR, 2472,g3_color1,g3_color2,g3_neutral);
        }*/
        /*else if (filesize == 6721536) //k910/zte front qcom
        {
            return new DngProfile(64, 2592 ,1296 ,false, BGGR, 0,g3_color1,g3_color2,g3_neutral);
        }
        else if (filesize == 6299648)//k910/zte front mipi
        {
            return new DngProfile(16, 2592 ,1944 ,true, BGGR, 0,g3_color1,g3_color2,g3_neutral);
        }*/
/*        else if (filesize< 6000000 && filesize > 5382641)//M8 qcom
            return new DngProfile(0, 2688, 1520,false, GRBG, 0,nocal_color1,nocal_color2,nocal_nutral);
        else if (filesize <= 5382641 && filesize > 5000000)//M8 mipi
            return new DngProfile(0, 2688, 1520,true, GRBG, HTCM8_rowSize,nocal_color1,nocal_color2,nocal_nutral);*/
        return null;
    }

    public class DngProfile
    {
        public int blacklevel;
        public int widht;
        public int height;
        public boolean isTightRAw;
        public String BayerPattern;
        public int rowsize;
        float[]matrix1;
        float[]matrix2;
        float[]neutral;

        public DngProfile(int blacklevel,int widht, int height, boolean tight, String bayerPattern, int rowsize, float[]matrix1, float[] matrix2, float[]neutral)
        {
            this.blacklevel = blacklevel;
            this.widht = widht;
            this.height = height;
            this.isTightRAw = tight;
            this.BayerPattern = bayerPattern;
            this.rowsize = rowsize;
            this.matrix1 = matrix1;
            this.matrix2 = matrix2;
            this.neutral = neutral;
        }

    }

    private static final float[] g3_color1 =
            {
                    (float) 0.9218606949, (float) 0.0263967514, (float) -0.1110496521,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.05432224274, (float) 0.2319784164, (float) 0.2338542938
            };

    //Color Matrix 1                  : 0.9218606949 0.0263967514 -0.1110496521 -0.3331432343 1.179347992 0.1260938644 -0.05432224274 0.2319784164 0.2338542938
    //

    private static final float[] g3_color2 =
            {
                    (float) 0.6053285599, (float) 0.0173330307, (float) -0.07291889191,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.0853471756, (float) 0.3644628525, (float) 0.3674106598
            };

    private static final float[] g3_neutral =
            {
                    (float) 0.3566446304, (float) 0.613401413, (float) 0.3468151093
            };

    private static final float[] nocal_color1 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    private static final float[] nocal_color2 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    private static final float[] nocal_nutral =
            {
                    (float) 1.0, (float) 1.0, (float) 1.0
            };

    private static final float[] m9_color1 =
            {
                     0.6484375f, -0.1171875f, -0.0234375f,
                    -0.2265625f,  0.9765625f,  0.2109375f,
                     0.0078125f,  0.171875f,    0.46875f
            };
    private static final float[] m9_color2 =
            {
                    0.96875f, -0.359375f, 0.375f,
                    -0.2578125f, 1.03125f, 0.71875f,
                    0.015625f, 0.078125f, 0.6875f
            };

    private static final float[] m9_neutral =
            {
                    0.515625f, 1f, 0.671875f
            };

    private static final float[] nubia_color1 =
            {
                    1.138859978f, -0.6577000025f, -0.1728000046f,
                    -0.1580000073f, 0.8415899877f, 0.3164100052f,
                    0.001599999611f, 0.1135400013f, 0.5451700094f
            };
    private static final float[] nubia_color2 =
            {
                    0.8400400285f, -0.1959100069f, -0.06722000244f,
                    -0.3785200121f, 1.17735006f, 0.2011699975f,
                    -0.05178000035f, 0.2078399957f, 0.5102199914f
            };

    public static final float[] nubia_neutral =
            {
                    0.6075999738f, 1f, 0.4993000032f
            };

    public static final float[] g3_tl84 =
            {
                    1.921158f,-1.110443f,0.189285f,
                    -0.255173f,1.376129f,-0.120956f,
                    1f,-0.85160798f,1.846222f
            };

    public static final float[] g3_neutral_lowlight =
            {     //R        G           B
                0.230904f, 0.20558f, 0.266458f
    };


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


    static abstract class Chromatix
    {
        static public float[] neutral_LowLight;
        static float[] neutral_NormalLight;
        static float[] neutral_BrightLight;

        static float[] CC_TL84;
        static float[] CC_LOWLIGHT;
        static float[] CC_D65;
        static float[] CC_A;
        static float[] CC_OUTDOOR;
    }

    static class G3Device
    {
        //BACK CAM
        static public float[] neutral_LowLight = {0.230904f, 0.20558f, 0.266458f};
        static float[] neutral_NormalLight = {0.230904f,0.20558f, 0.266458f};
        static float[] neutral_BrightLight= {0.230904f,0.20558f, 0.266458f};

        static float[] CC_TL84 =
        {
                1.921158f   ,-1.110443f     ,0.189285f,
                -0.255173f  ,1.376129f      ,-0.120956f,
                -1f         ,-0.85160798f   ,1.846222f
        };
        static float[] CC_LOWLIGHT={
                1.84596f    ,-1.02423f      ,0.17828f,
                -0.24259f   ,1.41108f       ,-0.16850001f,
                -1f         ,-0.90310001f   ,1.91504f
        };
        static float[] CC_D65 = {
                1.820541f       ,-1.001724f     ,0.181183f,
                -0.21112099f   ,1.388733f      ,-0.17761201f,
                -1             ,-0.84127802f   ,1.839066f
        };;
        static float[] CC_A = {1.72401f,-0.8574f,0.13338999f,-0.29269999f,1.43779f,-0.14509f,-0.97021484f,-1.17881f,2.2327001f};
        static float[] CC_OUTDOOR = {1.892731f,-0.98947197f,1.4433594f,-0.27654999f,1.601531f,-0.324981f,0.98632813f,-0.84582603f,1.838852f};

        //FRONT CAM
        static float[] neutral_light_front = {0.230904f, 0.20558f,0.266458f};

        static float[] CC_D65_FRONT = {1.51605f,-0.53394002f,1.1425781f,-0.22262f,1.415243f,-0.19262999f,1.1601563f,-0.52266997f,1.50258f };
        static float[] CC_A_FRONT = {1.487381f, -0.50427997f,1.1347656f,-0.2105f, 1.392174f,-0.18192001f, 1.1513672f,-0.49362999f,1.474659f };
    }
}
