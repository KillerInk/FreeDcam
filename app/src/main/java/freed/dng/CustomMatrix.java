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

/**
 * Created by troop on 02.05.2016.
 */
public class CustomMatrix
{
    public final float[] ColorMatrix1;
    public final float[] ColorMatrix2;
    public final float[] NeutralMatrix;
    public final float[] ForwardMatrix1;
    public final float[] ForwardMatrix2;
    public final float[] ReductionMatrix1;
    public final float[] ReductionMatrix2;
    public final double[] NoiseReductionMatrix;

    public CustomMatrix(float[]matrix1, float[] matrix2, float[]neutral,float[]fmatrix1, float[] fmatrix2,float[]rmatrix1, float[] rmatrix2,double[]noise)
    {
        ColorMatrix1 = matrix1;
        ColorMatrix2 = matrix2;
        NeutralMatrix = neutral;
        ForwardMatrix1 = fmatrix1;
        ForwardMatrix2 = fmatrix2;
        ReductionMatrix1 = rmatrix1;
        ReductionMatrix2 = rmatrix2;
        NoiseReductionMatrix = noise;
    }

    public CustomMatrix(String matrix1, String matrix2, String neutral,String fmatrix1, String fmatrix2, String rmatrix1, String rmatrix2, String noise)
    {
        ColorMatrix1 = getMatrixFromString(matrix1);
        ColorMatrix2 = getMatrixFromString(matrix2);
        NeutralMatrix = getMatrixFromString(neutral);
        ForwardMatrix1 = getMatrixFromString(fmatrix1);
        ForwardMatrix2 = getMatrixFromString(fmatrix2);
        ReductionMatrix1 = getMatrixFromString(rmatrix1);
        ReductionMatrix2 = getMatrixFromString(rmatrix2);
        NoiseReductionMatrix = getDoubleMatrixFromString(noise);
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
}
