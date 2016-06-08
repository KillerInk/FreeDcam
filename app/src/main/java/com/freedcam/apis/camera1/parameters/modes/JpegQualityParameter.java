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

package com.freedcam.apis.camera1.parameters.modes;
//defcomg was here

import android.hardware.Camera.Parameters;

import com.freedcam.apis.camera1.CameraHolder;

/**
 * Created by troop on 24.08.2014.
 */
public class JpegQualityParameter extends BaseModeParameter {
    public JpegQualityParameter(Parameters parameters, CameraHolder parameterChanged, String values) {
        super(parameters, parameterChanged, "jpeg-quality", "");
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public String[] GetValues()
    {
        String[] valuetoreturn = new String[20];
        for (int i = 1; i < 21; i++)
        {
            valuetoreturn[i-1] = "" + i*5;
        }
        return valuetoreturn;
    }
}
