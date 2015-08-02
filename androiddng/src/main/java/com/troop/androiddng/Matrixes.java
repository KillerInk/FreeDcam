package com.troop.androiddng;

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

    static class G3Device
    {
        //BACK CAM
        static public float[] neutral_LowLight = {0.230904f, 0.20558f, 0.266458f};
        static float[] neutral_NormalLight = {0.230904f,0.20558f, 0.266458f};
        static float[] neutral_BrightLight= {0.230904f,0.20558f, 0.266458f};

        static float[] xyzNeutral = {0.0452807761683715f,0.0677777898588087f,0.0343627874252381f};

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
        };;
        static float[] CC_A = {1.72401f,-0.8574f,0.13338999f,-0.29269999f,1.43779f,-0.14509f,-0.97021484f,-1.17881f,2.2327001f};
        static float[] CC_OUTDOOR = {1.892731f,-0.98947197f,1.4433594f,-0.27654999f,1.601531f,-0.324981f,0.98632813f,-0.84582603f,1.838852f};

        //FRONT CAM
        static float[] neutral_light_front = {0.230904f, 0.20558f,0.266458f};

        static float[] CC_D65_FRONT = {1.51605f,-0.53394002f,1.1425781f,-0.22262f,1.415243f,-0.19262999f,1.1601563f,-0.52266997f,1.50258f };
        static float[] CC_A_FRONT = {1.487381f, -0.50427997f,1.1347656f,-0.2105f, 1.392174f,-0.18192001f, 1.1513672f,-0.49362999f,1.474659f };
    }

    private float[] RGBtoXYZMatrix = {  0.4124564f,  0.3575761f,  0.1804375f,
            0.2126729f,  0.7151522f,  0.0721750f,
            0.0193339f,  0.1191920f,  0.9503041f};

    private float[] MoveRgbMatrixToXYZ(float[] colormatrix)
    {
        float[] xyzmat = new float[9];
        for (int i = 0; i < 9; i++)
        {
            xyzmat[i] = colormatrix[i] * RGBtoXYZMatrix[i];
        }
        return xyzmat;
    }

    public float[] MoveRgbNeutralMatrixToXYZ(float[] neutralmatrix)
    {
        float[] xyzmat = new float[3];
        float[] neutralRGB01 = scaleRGBtoRGB01(neutralmatrix[0],neutralmatrix[1],neutralmatrix[2]);
        xyzmat[0] = neutralRGB01[0] * RGBtoXYZMatrix[0];
        xyzmat[1] = neutralRGB01[1] * RGBtoXYZMatrix[4];
        xyzmat[2] = neutralRGB01[2] * RGBtoXYZMatrix[8];
        return xyzmat;
    }

    public float[] scaleRGBtoRGB01(float r_scale, float g_scale, float b_scale)
    {
        float[] rgbM = new float[3];
        float max_wb_factor = r_scale;
        if (b_scale > r_scale)
            max_wb_factor = b_scale;
        rgbM[0] = (float)r_scale / max_wb_factor;
        rgbM[1] = (float)g_scale / max_wb_factor;
        rgbM[2] = (float)b_scale / max_wb_factor;
        return rgbM;
    }

    /**
     * Convert RGB to XYZ
     * @param R
     * @param G
     * @param B
     * @return XYZ in double array.
     */
    public float[] RGBtoXYZ(int R, int G, int B)
    {
        float[] result = new float[3];

        // convert 0..255 into 0..1
        float r = R / 255.0f;
        float g = G / 255.0f;
        float b = B / 255.0f;

        // assume sRGB
        if (r <= 0.04045)
        {
            r = r / 12.92f;
        }
        else
        {
            r = (float)Math.pow(((r + 0.055) / 1.055), 2.4);
        }
        if (g <= 0.04045)
        {
            g = g / 12.92f;
        }
        else
        {
            g = (float)Math.pow(((g + 0.055) / 1.055), 2.4);
        }
        if (b <= 0.04045)
        {
            b = b / 12.92f;
        }
        else
        {
            b = (float)Math.pow(((b + 0.055) / 1.055), 2.4);
        }

        r *= 100.0;
        g *= 100.0;
        b *= 100.0;

        // [X Y Z] = [r g b][M]
        result[0] = (r * RGBtoXYZMatrix[0]) + (g * RGBtoXYZMatrix[1]) + (b * RGBtoXYZMatrix[2]);
        result[1] = (r * RGBtoXYZMatrix[3]) + (g * RGBtoXYZMatrix[4]) + (b * RGBtoXYZMatrix[5]);
        result[2] = (r * RGBtoXYZMatrix[6]) + (g * RGBtoXYZMatrix[7]) + (b * RGBtoXYZMatrix[8]);

        result[0] /= 100;
        result[1] /= 100;
        result[2] /= 100;

        return result;
    }
}
