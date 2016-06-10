/*
 *
 *     Copyright (C) 2015 George Kiarie
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

package com.freedcam.apis.camera1.parameters.manual;


import android.content.Context;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.troop.freedcam.R;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class ShutterManualKrillin extends BaseManualParameter {

    private static String TAG = ShutterManualKrillin.class.getSimpleName();

    public ShutterManualKrillin(Context context, Parameters parameters, I_CameraHolder baseCameraHolder, ParametersHandler parametersHandler) {
        super(context, parameters, "", "", "", parametersHandler, 1);
        isSupported = true;
        isVisible = isSupported;
        stringvalues = context.getResources().getStringArray(R.array.shutter_values_krillin);
    }

    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-manual-exposure-value", "auto");
        } else {

            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-manual-exposure-value", stringvalues[currentInt]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[valueToSet]);
    }

    @Override
    public String GetStringValue() {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }
}