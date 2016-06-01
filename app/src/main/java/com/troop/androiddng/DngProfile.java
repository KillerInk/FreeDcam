package com.troop.androiddng;

/**
 * Created by troop on 01.06.2016.
 */
public class DngProfile
{

    public final static int Mipi = 0;
    public final static int Qcom = 1;
    public final static int Plain = 2;
    public final static int Mipi16 = 3;
    public final static int Mipi12 = 4;

    public static String BGGR = "bggr";
    public static String RGGB = "rggb";
    public static final String GRBG = "grbg";
    public static final String GBRG =  "gbrg";

    public static final String RGBW =  "rgbw";

    public static final int ROWSIZE = 5264;

    public int blacklevel;
    public int widht;
    public int height;
    public int rawType;
    public String BayerPattern;
    public int rowsize;
    public CustomMatrix matrixes;

    public DngProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, CustomMatrix matrixes)
    {
        this.blacklevel = blacklevel;
        this.widht = widht;
        this.height = height;
        this.rawType = rawType;
        this.BayerPattern = bayerPattern;
        this.rowsize = rowsize;
        this.matrixes = matrixes;
    }
}
