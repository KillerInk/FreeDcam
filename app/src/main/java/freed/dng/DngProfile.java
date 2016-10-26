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

package freed.dng;

/**
 * Created by troop on 01.06.2016.
 */
public class DngProfile
{

    public static final int Mipi = 0;
    public static final int Qcom = 1;
    public static final int Plain = 2;
    public static final int Mipi16 = 3;
    public static final int Mipi12 = 4;

    public static final String BGGR = "bggr";
    public static final String RGGB = "rggb";
    public static final String GRBG = "grbg";
    public static final String GBRG =  "gbrg";

    public static final String RGBW =  "rgbw";

    public static final int ROWSIZE = 5264;
    public static final int HTCM8_rowSize = 3360;
    public static final int XperiaL_rowSize = 4376;

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
        BayerPattern = bayerPattern;
        this.rowsize = rowsize;
        this.matrixes = matrixes;
    }

    public static DngProfile getProfile(int blacklevel, int widht, int height,int rawFormat, String bayerPattern, int rowsize, float[] matrix1, float[] matrix2, float[] neutral, float[] fmatrix1, float[] fmatrix2, float[] rmatrix1, float[] rmatrix2, double[] noise)
    {
        return new DngProfile(blacklevel,widht,height, rawFormat,bayerPattern, 0,new CustomMatrix(matrix1,matrix2,neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise));
    }
}
