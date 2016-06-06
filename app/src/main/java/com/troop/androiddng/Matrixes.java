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

package com.troop.androiddng;

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
                    0.8853462669953089f, 0f,  0.8853462669953089f, 0f, 0.8853462669953089f,0f
            };

        //FRONT CAM
    public static float[] neutral_light_front = {0.230904f, 0.20558f,0.266458f};

    public static float[] CC_D65_FRONT = {1.51605f,-0.53394002f,1.1425781f,-0.22262f,1.415243f,-0.19262999f,1.1601563f,-0.52266997f,1.50258f };
    public static float[] CC_A_FRONT = {1.487381f, -0.50427997f,1.1347656f,-0.2105f, 1.392174f,-0.18192001f, 1.1513672f,-0.49362999f,1.474659f };

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
}
