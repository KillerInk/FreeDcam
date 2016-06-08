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


import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class ShutterManualKrillin extends BaseManualParameter {

    private static String TAG = "freedcam.ShutterManualKrillin";
    private I_CameraHolder baseCameraHolder;
    //AE_Handler_LGG4.AeManualEvent manualevent;
    private String KrillinShutterValues = "Auto,1/30000,1/15000,1/10000,1/8000,1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,2,4,8,15,30,32";

    public ShutterManualKrillin(Parameters parameters, I_CameraHolder baseCameraHolder, ParametersHandler parametersHandler) {
        super(parameters, "", "", "", parametersHandler, 1);

        this.baseCameraHolder = baseCameraHolder;
        isSupported = true;
        stringvalues = KrillinShutterValues.split(",");
        // this.manualevent =manualevent;
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return super.IsSupported();
    }


    @Override
    public int GetValue() {
        return currentInt;
    }

    @Override
    public void SetValue(int valueToSet) {
        if (valueToSet == 0) {
            parameters.set("hw-hwcamera-flag", "on");
            parameters.set("hw-manual-exposure-value", "auto");
        } else {
            currentInt = valueToSet;
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