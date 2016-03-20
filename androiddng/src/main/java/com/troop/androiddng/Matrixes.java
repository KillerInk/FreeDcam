package com.troop.androiddng;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ingo on 02.08.2015.
 */
public class Matrixes
{
    public static final float[] g3_color1 =
            {
                    (float) 0.9218606949, (float) 0.0263967514, (float) -0.1110496521,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.05432224274, (float) 0.2319784164, (float) 0.2338542938
            };

    //Color Matrix 1                  : 0.9218606949 0.0263967514 -0.1110496521 -0.3331432343 1.179347992 0.1260938644 -0.05432224274 0.2319784164 0.2338542938
    //

    public static final float[] g3_color2 =
            {
                    (float) 0.6053285599, (float) 0.0173330307, (float) -0.07291889191,
                    (float) -0.3331432343, (float) 1.179347992, (float) 0.1260938644,
                    (float) -0.0853471756, (float) 0.3644628525, (float) 0.3674106598
            };

    public static final float[] g3_neutral =
            {
                    (float) 0.3566446304, (float) 0.613401413, (float) 0.3468151093
            };

    public static final float[] nocal_color1 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    public static final float[] nocal_color2 =
            {
                    (float) 1.000, (float) 0.000, (float) 0.000,
                    (float) 0.000, (float) 1.000, (float) 0.000,
                    (float) 0.000, (float) 0.000, (float) 1.000
            };

    public static final float[] nocal_nutral =
            {
                    (float) 1.0, (float) 1.0, (float) 1.0
            };

    public static final float[] m9_color1 =
            {
                    0.6484375f, -0.1171875f, -0.0234375f,
                    -0.2265625f,  0.9765625f,  0.2109375f,
                    0.0078125f,  0.171875f,    0.46875f
            };
    public static final float[] m9_color2 =
            {
                    0.96875f, -0.359375f, 0.375f,
                    -0.2578125f, 1.03125f, 0.71875f,
                    0.015625f, 0.078125f, 0.6875f
            };

    public static final float[] m9_neutral =
            {
                    0.515625f, 1f, 0.671875f
            };

    public static final float[] nubia_color1 =
            {
                    1.138859978f, -0.6577000025f, -0.1728000046f,
                    -0.1580000073f, 0.8415899877f, 0.3164100052f,
                    0.001599999611f, 0.1135400013f, 0.5451700094f
            };
    public static final float[] nubia_color2 =
            {
                    0.8400400285f, -0.1959100069f, -0.06722000244f,
                    -0.3785200121f, 1.17735006f, 0.2011699975f,
                    -0.05178000035f, 0.2078399957f, 0.5102199914f
            };

    public static final float[] nubia_neutral =
            {
                    0.6075999738f, 1f, 0.4993000032f
            };

    public  static final float[]  test_matrix1 =
            {
                    1.198400f, -0.692100f,-0.181800f,
                    -0.166300f, 0.885600f, 0.333000f,
                    0.001700f, 0.119500f, 0.573700f
            };
    public static final float[] test_matrix2 =
            {
                    0.858600f, -0.200200f, -0.068700f,
                    -0.386900f, 1.203400f, 0.205600f,
                    -0.052900f, 0.212400f, 0.521500f
            };

    public static final float[] test_neutra =
            {
                    0.8853462669953089f, (float) 1, (float) 0.5438498847101853
            };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public  static final float[]  OnePlus_identity_matrix1 =
            {
                    1.138859996f, -0.6577f, 0.1728f, -0.158f, 0.8415899976f, 0.3164100004f, 0.0016f, 0.11354f, 0.5451699946f
            };
    public static final float[] OnePlus_identity_matrix2 =
            {
                    0.84004f, -0.1959100005f, -0.06722f, -0.37852f, 1.17734999f, 0.2011699991f, -0.05178f, 0.20784f, 0.5102200031f
            };

    public static final float[] OnePlus_identity_neutra =
            {
                    0.6295f, 1f, 0.5108f
            };
    public static final float[] OnePlus_foward_matrix1 =
            {
                    0.6648f, 0.2566f, 0.0429f, 0.197f, 0.9994f, -0.1964f, -0.0894f, -0.2304f, 1.145f
            };

    public static final float[] OnePlus_foward_matrix2 =
            {
                    0.6617f, 0.3849f, -0.0823f, 0.24f, 1.1138f, -0.3538f, -0.0062f, -0.1147f, 0.946f
            };

    public static final float[] OnePlus_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] OnePlus_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] OnePlus_noise_3x1_matrix =
            {
                    0.5438498847101853f, (float) 0, (float) 0.5438498847101853 ,0f,0.5438498847101853f,0f
            };

//////////////////////////////end 1+ ////////////////////////////////////////////////////////////////
    public  static final float[]  Nexus6_identity_matrix1 =
            {
                    1.140700f, -0.402200f, -0.234000f, -0.431400f, 1.404000f, 0.014600f, -0.043900f, 0.204700f, 0.570400f
            };
    public static final float[] Nexus6_identity_matrix2 =
            {
                    0.722800f, -0.089300f, -0.097500f, -0.479200f, 1.348100f, 0.138100f, -0.113700f, 0.268000f, 0.560400f
            };

    public static final float[] Nexus6_identity_neutra =
            {
                    0.5391f, 1.0000f, 0.6641f
            };
    public static final float[] Nexus6_foward_matrix1 =
            {
                    0.6328f, 0.0469f, 0.2813f, 0.1641f, 0.7578f, 0.0781f, -0.0469f, -0.6406f, 1.5078f
            };

    public static final float[] Nexus6_foward_matrix2 =
            {
                    0.7578f, 0.0859f, 0.1172f, 0.2734f, 0.8281f, -0.1016f, 0.0156f, -0.2813f, 1.0859f
            };

    public static final float[] Nexus6_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] Nexus6_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] Nexus6_noise_3x1_matrix =
            {
                    0.00051471478f, 0f, 0.00051471478f, 0f, 0.00051471478f, 0f
            };


    //////////////////////////////////eND nEXUS 6//////////////////////////////////////////////////
    //////////////////////////////   Omnivision ////////////////////////////////////////////////////////////////
    public  static final float[]  OV_matrix1 =
            {
                    1.15625f, -0.421875f, -0.328125f, -0.265625f, 1.3359375f, -0.125f, 0f, 0.1640625f, 0.6328125f
            };
    public static final float[] OV_matrix2 =
            {
                    0.671875f, -0.125f, -0.1015625f, -0.34375f, 1.15625f, 0.15625f, -0.0390625f, 0.1953125f, 0.5234375f
            };

    public static final float[] OV_ASSHOT =
            {
                    0.5546875f, 1f, 0.515625f
            };
    public static final float[] OV_Foward =
            {
                    0.5703125f, 0.078125f, 0.3203125f, 0.0625f, 0.8046875f, 0.1328125f, -0.0625f, -0.5390625f, 1.4296875f
            };

    public static final float[] OV_Foward2 =
            {
                    0.671875f, 0.171875f, 0.1171875f, 0.2109375f, 0.953125f, -0.1640625f, -0.0234375f, -0.25f, 1.09375f
            };

    public static final float[] OV_REDUCTION =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] OV_REDUCTION2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] OV_NREDUCTION_Matrix =
            {
                    0.003127599148f, 3.56840528e-005f, 0.003127599148f, 3.56840528e-005f, 0.003127599148f, 3.56840528e-005f
            };


    //////////////////////////////////eND nEXUS 6//////////////////////////////////////////////////

    public  static final float[]  G4_identity_matrix1 =
            {
                    1.15625f, -0.2890625f, -0.3203125f, -0.53125f, 1.5625f,.0625f, -0.078125f, 0.28125f, 0.5625f
            };
    public static final float[] G4_identity_matrix2 =
            {
                    0.5859375f, 0.0546875f, -0.125f, -0.6484375f, 1.5546875f, 0.0546875f, -0.2421875f, 0.5625f, 0.390625f
            };

    public static final float[] G4_identity_neutra =
            {
                    0.53125f, 1f, 0.640625f
            };
    public static final float[] G4_foward_matrix1 =
            {
                    0.820300f, -0.218800f, 0.359400f, 0.343800f, 0.570300f,0.093800f, 0.015600f, -0.726600f, 1.539100f
            };

    public static final float[] G4_foward_matrix2 =
            {
                    0.679700f, -0.078100f, 0.359400f, 0.210900f, 0.703100f,0.085900f, -0.046900f, -0.828100f, 1.695300f
            };

    public static final float[] G4_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] G4_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] G4_noise_3x1_matrix =
            {
                    0.8853462669953089f, (float) 0, (float) 0.8853462669953089f, 0f, 0.8853462669953089f,0f
            };



    ////////////////////////////////   END G4 /////////////////////////////////////////////////////



    ////////////////////////////////////////////IMX230 ////////////////////////////////////////////////////

    public  static final float[]  imx230_identity_matrix1 =
            {
                    0.609375f, 0.015625f, -0.1328125f, -0.9296875f, 1.828125f, 0.0546875f, -0.4375f, 0.6015625f, 0.5f
            };
    public static final float[] imx230_identity_matrix2 =
            {
                    0.875f, -0.0390625f, -0.421875f, -0.890625f, 2.1328125f, -0.4140625f, -0.1875f, 0.421875f, 0.453125f
            };

    public static final float[] imx230_identity_neutra =
            {
                    0.6295f, 1f, 0.5108f
            };
    public static final float[] imx230_foward_matrix1 =
            {
                    0.8046875f, -0.140625f, 0.296875f, 0.3984375f, 0.484375f, 0.1171875f, 0.15625f, -0.5390625f, 1.2109375f
            };

    public static final float[] imx230_foward_matrix2 =
            {
                    0.765625f, -0.2734375f, 0.46875f, 0.3203125f, 0.34375f, 0.3359375f, 0.0546875f, -0.9375f, 1.703125f
            };

    public static final float[] imx230_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] imx230_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] imx230_3x1_matrix =
            {
                    0.00072030654f, 0f, 0.00072030654f, 0f,0.00072030654f,0f
            };

    //////////////////////////////end 1+ ////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////IMX214 Adobe Standard ////////////////////////////////////////////////////

    public  static final float[]  imx214_identity_matrix1 =
            {
                    1.198400f, -0.692100f, 0.181800f, -0.166300f, 0.885600f, 0.333000f, 0.001700f, 0.119500f, 0.573700f
            };
    public static final float[] imx214_identity_matrix2 =
            {
                    0.858600f, -0.200200f, -0.068700f, -0.386900f, 1.203400f, 0.205600f, -0.052900f, 0.212400f, 0.521500f
            };

    public static final float[] imx214_identity_neutra =
            {
                    0.6295f, 1f, 0.5108f
            };
    public static final float[] imx214_foward_matrix1 =
            {
                    0.664800f, 0.256600f, 0.042900f, 0.197000f, 0.999400f, -0.196400f, -0.089400f, -0.230400f, 1.145000f
            };

    public static final float[] imx214_foward_matrix2 =
            {
                    0.661700f, 0.384900f, -0.082300f, 0.240000f, 1.113800f, -0.353800f, -0.006200f, -0.114700f, 0.946000f
            };

    public static final float[] imx214_reduction_matrix1 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] imx214_reduction_matrix2 =
            {
                    1, -1, -1,
                    -1, 1, 1,
                    -1, 1, 1
            };

    public static final float[] imx214_3x1_matrix =
            {
                    0.00072030654f, (float) 0, (float) 0.00072030654, 0f,0.00072030654f,0f
            };

    //////////////////////////////end 1+ ////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////
    static class G3Device
    {
        //BACK CAM
        /*
 00015544 float AWB_golden_module_R_Gr_ratio[12]		48
 00015544 float AWB_golden_module_R_Gr_ratio[0]	 0.56738299	 4
 00015548 float AWB_golden_module_R_Gr_ratio[1]	 0.56738299	 4
 00015552 float AWB_golden_module_R_Gr_ratio[2]	 0.86523402	 4
 00015556 float AWB_golden_module_R_Gr_ratio[3]	 0.56738299	 4
 00015560 float AWB_golden_module_R_Gr_ratio[4]	 0.56738299	 4
 00015564 float AWB_golden_module_R_Gr_ratio[5]	 0.86523402	 4
 00015568 float AWB_golden_module_R_Gr_ratio[6]	 0.56738299	 4
 00015572 float AWB_golden_module_R_Gr_ratio[7]	 0.56738299	 4
 00015576 float AWB_golden_module_R_Gr_ratio[8]	 0.56738299	 4
 00015580 float AWB_golden_module_R_Gr_ratio[9]	 0.56738299	 4
 00015584 float AWB_golden_module_R_Gr_ratio[10]	 0.86523402	 4
 00015588 float AWB_golden_module_R_Gr_ratio[11]	 0.86523402	 4

 00015592 float AWB_golden_module_Gb_Gr_ratio[12]		48
 00015592 float AWB_golden_module_Gb_Gr_ratio[0]	 1.	 4
 00015596 float AWB_golden_module_Gb_Gr_ratio[1]	 1.	 4
 00015600 float AWB_golden_module_Gb_Gr_ratio[2]	 1.	 4
 00015604 float AWB_golden_module_Gb_Gr_ratio[3]	 1.	 4
 00015608 float AWB_golden_module_Gb_Gr_ratio[4]	 1.	 4
 00015612 float AWB_golden_module_Gb_Gr_ratio[5]	 1.	 4
 00015616 float AWB_golden_module_Gb_Gr_ratio[6]	 1.	 4
 00015620 float AWB_golden_module_Gb_Gr_ratio[7]	 1.	 4
 00015624 float AWB_golden_module_Gb_Gr_ratio[8]	 1.	 4
 00015628 float AWB_golden_module_Gb_Gr_ratio[9]	 1.	 4
 00015632 float AWB_golden_module_Gb_Gr_ratio[10]	 1.	 4
 00015636 float AWB_golden_module_Gb_Gr_ratio[11]	 1.	 4


 00015640 float AWB_golden_module_B_Gr_ratio[12]		48
 00015640 float AWB_golden_module_B_Gr_ratio[0]	 0.55468798	 4
 00015644 float AWB_golden_module_B_Gr_ratio[1]	 0.55468798	 4
 00015648 float AWB_golden_module_B_Gr_ratio[2]	 0.390625	 4
 00015652 float AWB_golden_module_B_Gr_ratio[3]	 0.55468798	 4
 00015656 float AWB_golden_module_B_Gr_ratio[4]	 0.55468798	 4
 00015660 float AWB_golden_module_B_Gr_ratio[5]	 0.390625	 4
 00015664 float AWB_golden_module_B_Gr_ratio[6]	 0.55468798	 4
 00015668 float AWB_golden_module_B_Gr_ratio[7]	 0.55468798	 4
 00015672 float AWB_golden_module_B_Gr_ratio[8]	 0.55468798	 4
 00015676 float AWB_golden_module_B_Gr_ratio[9]	 0.55468798	 4
 00015680 float AWB_golden_module_B_Gr_ratio[10]	 0.390625	 4
 00015684 float AWB_golden_module_B_Gr_ratio[11]	 0.390625	 4

         */
        static public float[] wb_neutral = {0.56738299f, 1f, 0.55468798f};

        static float[] CC_TL84 =
                {
                        1.921158f   ,-1.110443f     ,0.189285f,
                        -0.255173f  ,1.376129f      ,-0.120956f,
                        0.9609375f         ,-0.85160798f   ,1.846222f
                };
        static float[] CC_LOWLIGHT={
                1.84596f    ,-1.02423f      ,0.17828f,
                -0.24259f   ,1.41108f       ,-0.16850001f,
                -1.0654297f ,-0.90310001f   ,1.91504f
        };
        static float[] CC_D65 = {
                1.820541f       ,-1.001724f     ,0.181183f,
                -0.21112099f   ,1.388733f      ,-0.17761201f,
                0.88330078f   ,-0.84127802f   ,1.839066f
        };

        static float[] CC_A = {1.72401f,-0.8574f,0.13338999f,-0.29269999f,1.43779f,-0.14509f,-0.97021484f,-1.17881f,2.2327001f};

        static float[] CC_OUTDOOR = {1.892731f,-0.98947197f,1.4433594f,-0.27654999f,1.601531f,-0.324981f,0.98632813f,-0.84582603f,1.838852f};

        //FRONT CAM
        static float[] neutral_light_front = {0.230904f, 0.20558f,0.266458f};

        static float[] CC_D65_FRONT = {1.51605f,-0.53394002f,1.1425781f,-0.22262f,1.415243f,-0.19262999f,1.1601563f,-0.52266997f,1.50258f };
        static float[] CC_A_FRONT = {1.487381f, -0.50427997f,1.1347656f,-0.2105f, 1.392174f,-0.18192001f, 1.1513672f,-0.49362999f,1.474659f };
    }

    static class HTC_M8Device
    {
        static public float[] wb_neutral = {1f, 1f, 1f};
        /*
        00005716 chromatix_color_correction_type A_color_correction	{...}	44
 00005716 float c0	 1.8358001	 4
 00005720 float c1	 -0.1674	 4
 00005724 float c2	 -0.66839999	 4
 00005728 float c3	 -0.1037	 4
 00005732 float c4	 0.94169998	 4
 00005736 float c5	 0.162	 4
 00005740 float c6	 -5.0799999e-002	 4
 00005744 float c7	 -1.0015	 4
 00005748 float c8	 1.9507	 4
 00005752 int16 k0	 0	 2
 00005754 int16 k1	 0	 2
 00005756 int16 k2	 0	 2
 00005758 char q_factor	 0	 1
 00005759 _char_empty empty	 {...}	 1
         */
        static public float[] CC_A = {
                1.8358001f,         -0.1674f,       -0.66839999f,
                -0.1037f,           0.94169998f,    0.162f,
                -5.0799999e-002f,   -1.0015f,       1.9507f};



        /*
        00005672 chromatix_color_correction_type D65_color_correction	{...}	44
     00005672 float c0	 1.9644001	 4
     00005676 float c1	 -0.85769999	 4
     00005680 float c2	 -0.1067	 4
     00005684 float c3	 -0.2656	 4
     00005688 float c4	 1.4262	 4
     00005692 float c5	 -0.1603	 4
     00005696 float c6	 0.1106	 4
     00005700 float c7	 -0.50870001	 4
     00005704 float c8	 1.3981	 4
     00005708 int16 k0	 0	 2
     00005710 int16 k1	 0	 2
     00005712 int16 k2	 0	 2
     00005714 char q_factor	 0	 1
     00005715 _char_empty empty	 {...}	 1

         */
        static public float[] CC_D65 = {
                1.9644001f,         -0.85769999f,       -0.1067f,
                -0.2656f,           1.4262f,            -0.1603f,
                0.1106f,            -0.50870001f,       1.3981f};

    }

    static class Redmi_Note {
        static public float[] wb_neutral = {0.54589802f, 1f, 0.57421899f};

        /*
        00005716 chromatix_color_correction_type A_color_correction	{...}	44
 00005716 float c0	 1.8487	 4
 00005720 float c1	 -7.6800004e-002	 4
 00005724 float c2	 -0.7719	 4
 00005728 float c3	 -0.1401	 4
 00005732 float c4	 1.1341	 4
 00005736 float c5	 6.0000001e-003	 4
 00005740 float c6	 -0.1131	 4
 00005744 float c7	 -1.0296	 4
 00005748 float c8	 2.1427	 4
 00005752 int16 k0	 0	 2
 00005754 int16 k1	 0	 2
 00005756 int16 k2	 0	 2
 00005758 char q_factor	 0	 1
 00005759 _char_empty empty	 {...}	 1

         */
        static public float[] CC_A_Back = {
                1.8487f,         -7.6800004e-002f,       -0.7719f,
                -0.1401f,           1.1341f,    6.0000001e-00f,
                -0.1131f,   -1.0296f,       2.1427f};

        /*
        00005672 chromatix_color_correction_type D65_color_correction	{...}	44
 00005672 float c0	 1.7847	 4
 00005676 float c1	 -0.92360002	 4
 00005680 float c2	 0.1389	 4
 00005684 float c3	 -0.1108	 4
 00005688 float c4	 1.4247	 4
 00005692 float c5	 -0.31389999	 4
 00005696 float c6	 0.1314	 4
 00005700 float c7	 -0.77179998	 4
 00005704 float c8	 1.6403	 4
 00005708 int16 k0	 0	 2
 00005710 int16 k1	 0	 2
 00005712 int16 k2	 0	 2
 00005714 char q_factor	 0	 1
 00005715 _char_empty empty	 {...}	 1

         */
        static public float[] CC_D65_Back = {
                1.7847f,         -0.92360002f,       0.1389f,
                -0.1108f,           1.4247f,            -0.31389999f,
                0.1314f,            -0.77179998f,       1.6403f};
    }


    public static final float[]  onePCCM1 =
            {
                    1.138859996f, -0.6577f, 0.1728f, -0.158f, 0.8415899976f, 0.3164100004f, 0.0016f, 0.11354f, 0.5451699946f
            };
    public static final float[] onePCCM2 =
            {
                    0.84004f, -0.1959100005f, -0.06722f, -0.37852f, 1.17734999f, 0.2011699991f, -0.05178f, 0.20784f, 0.5102200031f
            };

    public static final float[] onePNM =
            {
                    0.6295f, 1f, 0.5108f
            };

    public static final float[]  Nex6CCM1 =
            {
                    1.140700f, -0.402200f, -0.234000f, -0.431400f, 1.404000f, 0.014600f, -0.043900f, 0.204700f, 0.570400f
            };
    public static final float[] Nex6CCM2 =
            {
                    0.722800f, -0.089300f, -0.097500f, -0.479200f, 1.348100f, 0.138100f, -0.113700f, 0.268000f, 0.560400f
            };

    public static final float[] Nex6NM =
            {
                    0.5391f, 1.0000f, 0.6641f
            };

    public static final float[]  G4CCM1 =
            {
                    1.15625f, -0.2890625f, -0.3203125f, -0.53125f, 1.5625f,.0625f, -0.078125f, 0.28125f, 0.5625f
            };
    public static final float[] G4CCM2 =
            {
                    0.5859375f, 0.0546875f, -0.125f, -0.6484375f, 1.5546875f, 0.0546875f, -0.2421875f, 0.5625f, 0.390625f
            };

    public static final float[] G4NM =
            {
                    0.53125f, 1f, 0.640625f
            };

    public static HashMap<String, int[]> RGB_CCT_LIST = new HashMap<String, int[]>()
    {
        {
            put("1500",new int[]{255,108,0});
            put("1600",new int[]{255,115,0});
            put("1700",new int[]{255,121,0});
            put("1800",new int[]{255,126,0});
            put("1900",new int[]{255,132,0});

            put("2000",new int[]{255,137,14});
            put("2100",new int[]{255,142,27});
            put("2200",new int[]{255,146,39});
            put("2300",new int[]{255,151,50});
            put("2400",new int[]{255,155,61});
            put("2500",new int[]{255,159,70});
            put("2600",new int[]{255,163,79});
            put("2700",new int[]{255,167,87});
            put("2800",new int[]{255,170,95});
            put("2900",new int[]{255,174,103});

            put("3000",new int[]{255,177,110});
            put("3100",new int[]{255,180,117});
            put("3200",new int[]{255,184,123});
            put("3300",new int[]{255,187,129});
            put("3400",new int[]{255,190,135});
            put("3500",new int[]{255,193,141});
            put("3600",new int[]{255,195,146});
            put("3700",new int[]{255,198, 151});
            put("3800",new int[]{255,201,157});
            put("3900",new int[]{255,203, 161});

            put("4000",new int[]{255,206, 166});
            put("4100",new int[]{255,208, 171});
            put("4200",new int[]{255,211, 175});
            put("4300",new int[]{255,213, 179});
            put("4400",new int[]{255,215, 183});
            put("4500",new int[]{255,218, 187});
            put("4600",new int[]{255,220, 191});
            put("4700",new int[]{255,222, 195});
            put("4800",new int[]{255,224, 199});
            put("4900",new int[]{255,226, 202});

            put("5000",new int[]{255,228, 206});
            put("5100",new int[]{255,230, 209});
            put("5200",new int[]{255,232, 213});
            put("5300",new int[]{255,234, 216});
            put("5400",new int[]{255,236, 219});
            put("5500",new int[]{255,237, 222});
            put("5600",new int[]{255,239, 225});
            put("5700",new int[]{255,241, 228});
            put("5800",new int[]{255,243, 231});
            put("5900",new int[]{255,244, 234});

            put("6000",new int[]{255,246, 237});
            put("6100",new int[]{255,248, 240});
            put("6200",new int[]{255,249, 242});
            put("6300",new int[]{255,251, 245});
            put("6400",new int[]{255,253, 248});
            put("6500",new int[]{255,254, 250});
            put("6600",new int[]{255,255, 255});
            put("6700",new int[]{254,249, 255});
            put("6800",new int[]{250,246, 255});
            put("6900",new int[]{246,244, 255});

            put("7000",new int[]{243,242, 255});
            put("7100",new int[]{240,240, 255});
            put("7200",new int[]{237,239, 255});
            put("7300",new int[]{234,237, 255});
            put("7400",new int[]{232,236, 255});
            put("7500",new int[]{230,235, 255});
            put("7600",new int[]{228,234, 255});
            put("7700",new int[]{226,233, 255});
            put("7800",new int[]{224,232, 255});
            put("7900",new int[]{223,231, 255});

            put("8000",new int[]{221,230, 255});
            put("8100",new int[]{220,229, 255});
            put("8200",new int[]{218,228, 255});
            put("8300",new int[]{217,227, 255});
            put("8400",new int[]{216,227, 255});
            put("8500",new int[]{215,226, 255});
            put("8600",new int[]{214,225, 255});
            put("8700",new int[]{213,225, 255});
            put("8800",new int[]{212,224, 255});
            put("8900",new int[]{211,223, 255});

            put("9000",new int[]{210,223, 255});
            put("9100",new int[]{209,222, 255});
            put("9200",new int[]{208,222, 255});
            put("9300",new int[]{207,221, 255});
            put("9400",new int[]{206,221, 255});
            put("9500",new int[]{205,220, 255});
            put("9600",new int[]{205,220, 255});
            put("9700",new int[]{204,219, 255});
            put("9800",new int[]{203,219, 255});
            put("9900",new int[]{202,218, 255});

            put("10000",new int[]{201,218, 255});
        }


    };

}
