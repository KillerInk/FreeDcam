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

package com.freedcam.apis.camera1.parameters.manual.lg;


import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.lg.AE_Handler_LGG4.AeManual;
import com.freedcam.apis.camera1.parameters.manual.lg.AE_Handler_LGG4.AeManualEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by troop on 17.08.2014.
 */
public class ShutterManualParameterG4 extends BaseManualParameter
{
    private static String TAG = ShutterManualParameterG4.class.getSimpleName();
    private AeManualEvent manualevent;

    public ShutterManualParameterG4(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AeManualEvent manualevent)
    {
        super(parameters, cameraUiWrapper,1);
        isSupported = true;
        stringvalues = parameters.get(KEYS.LG_SHUTTER_SPEED_VALUES).replace(",0","").split(",");
        stringvalues[0] = KEYS.AUTO;
        ArrayList<String> l = new ArrayList(Arrays.asList(stringvalues));
        l.remove(0);
        stringvalues = new String[l.size()];
        l.toArray(stringvalues);

        this.manualevent =manualevent;
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
    public void SetValue(int valueToSet)
    {
        manualevent.onManualChanged(AeManual.shutter, false, valueToSet);
    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.set(KEYS.LG_SHUTTER_SPEED, "0");
        }
        else
        {
            currentInt = value;
            parameters.set(KEYS.LG_SHUTTER_SPEED, stringvalues[value]);
        }
        ThrowCurrentValueStringCHanged(stringvalues[value]);
    }

    @Override
    public String GetStringValue()
    {
        return stringvalues[currentInt];
    }

    @Override
    public String[] getStringValues()
    {
        return stringvalues;
    }

}