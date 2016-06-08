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

package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualShutter;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 21.02.2016.
 */
public class ShutterManualSony extends AbstractManualShutter
{
    final String TAG = ShutterManualSony.class.getSimpleName();
    private ParametersHandler parametersHandler;
    private Parameters parameters;
    /**
     * @param parameters
     * @param maxValue
     * @param MinValue
     * @param parametersHandler
     */
    public ShutterManualSony(Parameters parameters, String maxValue, String MinValue, ParametersHandler parametersHandler) {
        super(parametersHandler);
        this.parametersHandler = parametersHandler;
        this.parameters = parameters;
        try {
            if (!parameters.get("sony-max-shutter-speed").equals(""))
            {
                try {
                    int min = Integer.parseInt(parameters.get("sony-min-shutter-speed"));
                    int max = Integer.parseInt(parameters.get("sony-max-shutter-speed"));
                    stringvalues = getSupportedShutterValues(min, max,true);
                    isSupported = true;
                } catch (NumberFormatException ex) {
                    Logger.exception(ex);
                    isSupported = false;
                }
            }
        }
        catch (NullPointerException ex)
        {
            isSupported = false;
        }
    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        parameters.set("sony-ae-mode", "manual");
        parameters.set("sony-shutter-speed", stringvalues[currentInt]);
        parametersHandler.SetParametersToCamera(parameters);
    }
}
