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

import android.content.Context;
import android.hardware.Camera.Parameters;
import android.os.Handler;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class ShutterManualG2pro extends BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG = ShutterManualG2pro.class.getSimpleName();

    private final String G2Pro ="1/2,1,2,4,8,16,32,64";

    /**
     * @param parameters
     * @param cameraUiWrapper
     */
    public ShutterManualG2pro(Parameters parameters, I_CameraUiWrapper cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        this.baseCameraHolder = baseCameraHolder;

        stringvalues = G2Pro.split(",");

        isSupported = true;
    }

    @Override
    public boolean IsSupported() {
        return super.IsSupported();
    }

    @Override
    public boolean IsVisible() {
        return  IsSupported();
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        String shutterstring = stringvalues[currentInt];
        if (shutterstring.contains("/")) {
            String[] split = shutterstring.split("/");
            Double a = Double.parseDouble(split[0]) / Double.parseDouble(split[1]);
            shutterstring = "" + a*1000000;
        }
        if(!stringvalues[currentInt].equals(KEYS.AUTO))
        {
            try {
                shutterstring = setExposureTimeToParameter(shutterstring);
            }
            catch (Exception ex)
            {
                Logger.d("Freedcam", "Shutter Set FAil");
            }
        }
        else
        {
            shutterstring = setExposureTimeToParameter("0");
        }
        Logger.e(TAG, shutterstring);
    }



    private String setExposureTimeToParameter(final String shutterstring)
    {
        try {

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {

                    parameters.set("exposure-time", shutterstring);
                    ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
                }
            };
            handler.post(r);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        return shutterstring;
    }
}