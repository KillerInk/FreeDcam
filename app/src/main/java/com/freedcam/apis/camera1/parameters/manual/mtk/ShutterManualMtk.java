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

package com.freedcam.apis.camera1.parameters.manual.mtk;


import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK.AeManual;
import com.freedcam.apis.camera1.parameters.manual.mtk.AE_Handler_MTK.AeManualEvent;

/**
 * Created by troop on 28.03.2016.
 */
public class ShutterManualMtk extends BaseManualParameter
{
    private final String TAG = ShutterManualMtk.class.getSimpleName();
    private final AeManualEvent manualevent;

    private final String MTKShutter = "1/6000,1/4000,1/2000,1/1000,1/500,1/250,1/125,1/60,1/30,1/15,1/8,1/4,1/2,1,2";

    public ShutterManualMtk(Parameters parameters, CameraWrapperInterface cameraUiWrapper, AeManualEvent manualevent) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = true;
        stringvalues = MTKShutter.split(",");
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
        if (valueToSet == 0)
        {
            manualevent.onManualChanged(AeManual.shutter, true, valueToSet);
        }
        else
        {
            manualevent.onManualChanged(AeManual.shutter, false, valueToSet);
        }

    }

    public void setValue(int value)
    {

        if (value == 0)
        {
            parameters.set("m-ss", "0");
        }
        else
        {
            String shutterstring = stringvalues[value];
            if (shutterstring.contains("/")) {
                String[] split = shutterstring.split("/");
                Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
                shutterstring = "" + a;
            }
            currentInt = value;
            parameters.set("m-ss", FLOATtoThirty(shutterstring));
        }
        ThrowCurrentValueStringCHanged(stringvalues[value]);
    }


    public Double getMicroSec(String shutterString)
    {
        Double a = Double.parseDouble(shutterString);

        return a * 1000;

    }

    public String FLOATtoSixty4(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000000;
        return String.valueOf(c);
    }

    private String FLOATtoThirty(String a)
    {
        Float b =  Float.parseFloat(a);
        float c = b * 1000;
        return String.valueOf(c);
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
