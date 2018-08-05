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

import java.nio.ByteBuffer;

/**
 * Created by troop on 01.06.2016.
 */
public class DngProfile
{

    //shift 10bit tight data into readable bitorder
    public static final int Mipi = 0;
    //shift 10bit loose data into readable bitorder
    public static final int Qcom = 1;
    //drops the 6 first bit from pure 16bit data(mtk soc, Camera2 RAW_SENSOR)
    public static final int Plain = 2;
    //convert and shift 10bit tight data into 16bit pure
    public static final int Mipi16 = 3;
    //shift 12bit data into readable bitorder
    public static final int Mipi12 = 4;
    //shift 16bit data into readable 12bit order
    public static final int Pure16bit_To_12bit = 5;

    public static final String BGGR = "bggr";
    public static final String RGGB = "rggb";
    public static final String GRBG = "grbg";
    public static final String GBRG =  "gbrg";

    public static final String RGBW =  "rgbw";

    public CustomMatrix matrixes;
    public String matrixName;

    public ToneMapProfile toneMapProfile;

    private ByteBuffer byteBuffer;

    private DngProfile(int blacklevel,int whitelevel,int widht, int height, int rawType, String bayerPattern, int rowsize, String matrixName)
    {
        byteBuffer = init();
        setDngInfo(byteBuffer,blacklevel,whitelevel,widht,height,rawType,bayerPattern,rowsize);
        this.matrixName = matrixName;
    }

    public DngProfile(int blacklevel,int whitelevel,int widht, int height, int rawType, String bayerPattern, int rowsize, CustomMatrix matrixes, String matrixName)
    {
        this(blacklevel,whitelevel,widht,height,rawType,bayerPattern,rowsize, matrixName);
        this.matrixes = matrixes;
    }


    public static DngProfile getProfile(int blacklevel,int whitelevel, int widht, int height,int rawFormat, String bayerPattern, int rowsize, float[] matrix1, float[] matrix2, float[] neutral, float[] fmatrix1, float[] fmatrix2, float[] rmatrix1, float[] rmatrix2, double[] noise, String name)
    {
        return new DngProfile(blacklevel,whitelevel,widht,height, rawFormat,bayerPattern, 0,new CustomMatrix(matrix1,matrix2,neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise), name);
    }

    /*
       <filesize size= "XXX">
           <width>2560</width>
           <height>1920</height>
           <rawtype>0</rawtype> // 0 = Mipi, 1 = qcom, 2 = Plain, 3 = mipi16, 4 = mipi12
           <colorpattern>grbg</colorpattern>
           <rowsize>0</rowsize>
           <matrixset>Imx135</matrixset>
       </filesize>*/
    public String getXmlString(long filesize)
    {
        String t = "";
        t += "<filesize size= " +String.valueOf("\"") +String.valueOf(filesize) +String.valueOf("\"")  +">" + "\r\n";
            t += "<blacklvl>" + getBlacklvl(byteBuffer) + "</blacklvl>" + "\r\n";
            t += "<whitelvl>" + getWhitelvl(byteBuffer) + "</whitelvl>" + "\r\n";
            t += "<width>" + getWidth(byteBuffer) + "</width>" + "\r\n";
            t += "<height>" + getHeight(byteBuffer) + "</height>" + "\r\n";
            t += "<rawtype>" + getRawType(byteBuffer) + "</rawtype>" + "\r\n";
            t += "<colorpattern>" + getBayerPattern(byteBuffer) + "</colorpattern>" + "\r\n";
            t += "<rowsize>" + getRowSize(byteBuffer) + "</rowsize>" + "\r\n";
            t += "<matrixset>" + matrixName + "</matrixset>" + "\r\n";
        t += "</filesize>"  + "\r\n";


        return t;
    }

    static
    {
        System.loadLibrary("freedcam");
    }

    public void setActiveArea(int[] activeArea)
    {
        if (byteBuffer == null)
            return;
        setActiveArea(byteBuffer,activeArea);
    }

    private native ByteBuffer init();
    private native void clear(ByteBuffer byteBuffer);
    private native void setDngInfo(ByteBuffer javaHandler, int blacklevel,int whitelevel,int widht, int height, int rawType, String bayerPattern, int rowsize);
    private native int getWhitelvl(ByteBuffer byteBuffer);
    private native int getBlacklvl(ByteBuffer byteBuffer);
    private native int getRawType(ByteBuffer byteBuffer);
    private native int getWidth(ByteBuffer byteBuffer);
    private native int getHeight(ByteBuffer byteBuffer);
    private native int getRowSize(ByteBuffer byteBuffer);
    private native String getBayerPattern(ByteBuffer byteBuffer);

    public int getWhitelvl()
    {
        return getWhitelvl(byteBuffer);
    }
    public int getBlacklvl()
    {
        return getBlacklvl(byteBuffer);
    }
    public int getRawType()
    {
        return getRawType(byteBuffer);
    }
    public int getWidth(){
        return getWidth(byteBuffer);
    }

    public int getHeight()
    {
        return getHeight(byteBuffer);
    }
    public int getRowSize()
    {
        return getRowSize(byteBuffer);
    }

    public String getBayerPatter()
    {
        return getBayerPattern(byteBuffer);
    }

    public ByteBuffer getByteBuffer()
    {
        return byteBuffer;
    }

    private native void setActiveArea(ByteBuffer byteBuffer, int[] activearea);

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (byteBuffer != null)
            clear(byteBuffer);
    }
}
