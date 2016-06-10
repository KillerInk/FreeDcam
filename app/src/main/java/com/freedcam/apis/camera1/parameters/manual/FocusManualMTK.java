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
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 28.03.2016.
 */
public class FocusManualMTK extends BaseFocusManual {

    public FocusManualMTK(Context context,Parameters parameters, ParametersHandler parametersHandler) {
        //TODO check if AFENG_FI_MIN/MAX can get used
        super(context, parameters, KEYS.AFENG_POS, 0, 1023, KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler, (float) 10, 1);
        isSupported = true;
        isVisible = true;
        manualFocusModeString = KEYS.KEY_FOCUS_MODE_MANUAL;
        stringvalues = createStringArray(0, 1023, (float) 10);
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !parametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                parametersHandler.FocusMode.SetValue(manualFocusModeString, false);

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            parametersHandler.SetParametersToCamera(parameters);
        }
    }
}
