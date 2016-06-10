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

import com.freedcam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 12.04.2015.
 */
public class SkintoneManualPrameter extends BaseManualParameter {
    /**
     * @param parameters
     * @param parametersHandler
     */
    public SkintoneManualPrameter(Context context, Parameters parameters, ParametersHandler parametersHandler)
    {
        super(context, parameters, "", "", "", parametersHandler,1);
        try
        {
            isSupported = true;
            if (isSupported)
            {
                stringvalues = createStringArray(-100,100,1);
            }
        }
        catch (Exception ex)
        {
            isSupported = false;

        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public void SetValue(int valueToSet) {
        parametersHandler.SceneMode.SetValue("portrait", true);
        parameters.set("skinToneEnhancement",valueToSet + "");
        if (valueToSet == 0)
            parametersHandler.SceneMode.SetValue("auto", true);
    }
}
