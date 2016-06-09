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

package com.freedcam.apis.camera1.parameters.manual;


import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 01.09.2014.
 */
public class ZoomManualParameter extends  BaseManualParameter
{
    public ZoomManualParameter(Parameters parameters, ParametersHandler parametersHandler)
    {
        super(parameters,parametersHandler,1);
        key_value = KEYS.ZOOM;
        isSupported = false;
        if (parameters.get(KEYS.ZOOM_SUPPORTED)!= null)
            if (parameters.get(KEYS.ZOOM_SUPPORTED).equals(KEYS.TRUE)) {
                isSupported = true;
                isVisible = true;
                Set_Default_Value(GetValue());
                stringvalues = createStringArray(0,Integer.parseInt(parameters.get(KEYS.MAX_ZOOM)),1);
                currentInt = Integer.parseInt(parameters.get(key_value));
            }
    }

    @Override
    public void SetValue(int valueToset) {
        currentInt = valueToset;
        parameters.set(key_value, valueToset);
        parametersHandler.SetParametersToCamera(parameters);
    }
}
