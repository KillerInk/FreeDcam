package com.troop.androiddng;

import java.util.HashMap;

/**
 * Created by Ingo on 02.08.2015.
 */
public class Matrixes
{
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

        //FRONT CAM
        static float[] neutral_light_front = {0.230904f, 0.20558f,0.266458f};

        static float[] CC_D65_FRONT = {1.51605f,-0.53394002f,1.1425781f,-0.22262f,1.415243f,-0.19262999f,1.1601563f,-0.52266997f,1.50258f };
        static float[] CC_A_FRONT = {1.487381f, -0.50427997f,1.1347656f,-0.2105f, 1.392174f,-0.18192001f, 1.1513672f,-0.49362999f,1.474659f };

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
