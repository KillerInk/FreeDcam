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
    public String bayerPattern;
    public int rowsize;
    public CustomMatrix matrixes;
    public String matrixName;

    public DngProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, String matrixName)
    {
        this.blacklevel = blacklevel;
        this.widht = widht;
        this.height = height;
        this.rawType = rawType;
        this.bayerPattern = bayerPattern;
        this.rowsize = rowsize;
        this.matrixName = matrixName;
    }

    public DngProfile(int blacklevel,int widht, int height, int rawType, String bayerPattern, int rowsize, CustomMatrix matrixes, String matrixName)
    {
        this(blacklevel,widht,height,rawType,bayerPattern,rowsize, matrixName);
        this.matrixes = matrixes;
    }


    public static DngProfile getProfile(int blacklevel, int widht, int height,int rawFormat, String bayerPattern, int rowsize, float[] matrix1, float[] matrix2, float[] neutral, float[] fmatrix1, float[] fmatrix2, float[] rmatrix1, float[] rmatrix2, double[] noise, String name)
    {
        return new DngProfile(blacklevel,widht,height, rawFormat,bayerPattern, 0,new CustomMatrix(matrix1,matrix2,neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise), name);
    }

    public String getXmlString(long filesize)
    {
        /*
        <filesize size= "XXX">
            <width>2560</width>
            <height>1920</height>
            <rawtype>0</rawtype> // 0 = Mipi, 1 = qcom, 2 = Plain, 3 = mipi16, 4 = mipi12
            <colorpattern>grbg</colorpattern>
            <rowsize>0</rowsize>
            <matrixset>Imx135</matrixset>
        </filesize>*/
        String t = new String();
        t += "<filesize size= " +String.valueOf("\"") +String.valueOf(filesize) +String.valueOf("\"")  +">" + "\r\n";
            t += "<blacklvl>" + blacklevel + "</blacklvl>" + "\r\n";
            t += "<width>" + widht + "</width>" + "\r\n";
            t += "<height>" + widht + "</height>" + "\r\n";
            t += "<rawtype>" + rawType + "</rawtype>" + "\r\n";
            t += "<colorpattern>" + bayerPattern + "</colorpattern>" + "\r\n";
            t += "<rowsize>" + rowsize + "</rowsize>" + "\r\n";
            t += "<matrixset>" + matrixName + "</matrixset>" + "\r\n";
        t += "</filesize>"  + "\r\n";


        return t;
    }
}
