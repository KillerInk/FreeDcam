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

import android.text.TextUtils;

import java.nio.ByteBuffer;

/**
 * Created by troop on 02.05.2016.
 */
public class CustomMatrix
{

    static
    {
        System.loadLibrary("freedcam");
    }

    ByteBuffer byteBuffer;

    private native ByteBuffer init();
    private native void clear(ByteBuffer byteBuffer);
    private native void setMatrixes(ByteBuffer buffer, float[] colorMatrix1,float[] colorMatrix2,float[] neutral,float[] fMatrix1,float[] fMatrix2,
                                        float[] rMatrix1,float[] rMatrix2,double[] noise);

    public CustomMatrix()
    {
        byteBuffer = init();
    }

    public CustomMatrix(float[]matrix1, float[] matrix2, float[]neutral,float[]fmatrix1, float[] fmatrix2,float[]rmatrix1, float[] rmatrix2,double[]noise)
    {
        byteBuffer = init();
        setMatrixes(byteBuffer, matrix1, matrix2, neutral,fmatrix1,fmatrix2,rmatrix1,rmatrix2,noise);

    }

    public CustomMatrix(String matrix1, String matrix2, String neutral,String fmatrix1, String fmatrix2, String rmatrix1, String rmatrix2, String noise)
    {
        this(getMatrixFromString(matrix1),
                getMatrixFromString(matrix2),
                getMatrixFromString(neutral),
                getMatrixFromString(fmatrix1),
                getMatrixFromString(fmatrix2),
                getMatrixFromString(rmatrix1),
                getMatrixFromString(rmatrix2),
                        getDoubleMatrixFromString(noise));
    }

    public ByteBuffer getByteBuffer()
    {
        return byteBuffer;
    }


    public static float[] getMatrixFromString(String m)
    {
        if (m.equals("NULL" )|| TextUtils.isEmpty(m))
            return null;
        String[] split = m.split(",");
        float[] ar = new float[split.length];
        for (int i = 0; i< split.length; i++)
        {
            //when we was to lazy for the math and it looks like 46/128
            if (split[i].contains("/"))
            {
                String[] s = split[i].split("/");
                int left = Integer.parseInt(s[0].replace(" ",""));
                int right = Integer.parseInt(s[1].replace(" ",""));
                ar[i] = (float)left/right;
            }
            else
                ar[i] = Float.parseFloat(split[i]);
        }
        return ar;
    }

    public static double[] getDoubleMatrixFromString(String m)
    {
        if (m.equals("NULL" )|| TextUtils.isEmpty(m))
            return null;
        String[] split = m.split(",");
        double[] ar = new double[split.length];
        for (int i = 0; i< split.length; i++)
        {
            //when we was to lazy for the math and it looks like 46/128
            if (split[i].contains("/"))
            {
                String[] s = split[i].split("/");
                int left = Integer.parseInt(s[0].replace(" ",""));
                int right = Integer.parseInt(s[1].replace(" ",""));
                ar[i] = (double)left/right;
            }
            else
                ar[i] = Double.parseDouble(split[i]);
        }
        return ar;
    }

    @Override
    protected void finalize() throws Throwable {
        if (byteBuffer != null)
            clear(byteBuffer);
        byteBuffer = null;
    }
}
