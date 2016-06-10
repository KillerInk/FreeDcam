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

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by GeorgeKiarie on 6/3/2016.
 */
public class ShutterManualMeizu extends BaseManualParameter
{
    private I_CameraHolder baseCameraHolder;
    private final String TAG = ShutterManualMeizu.class.getSimpleName();

    /**
     * @param parameters
     * @param parametersHandler
     */
    public ShutterManualMeizu(Context context, Parameters parameters, I_CameraHolder baseCameraHolder, ParametersHandler parametersHandler) {
        super(context, parameters, "", "", "", parametersHandler,1);
        this.baseCameraHolder = baseCameraHolder;

            stringvalues = context.getResources().getStringArray(R.array.shutter_values_meizu);

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



    private String setExposureTimeToParameter(String shutterstring)
    {
        parameters.set("shutter-value", shutterstring);
        parametersHandler.SetParametersToCamera(parameters);


        baseCameraHolder.StopPreview();
        baseCameraHolder.StartPreview();

        return shutterstring;
    }
}