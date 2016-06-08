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
import com.freedcam.utils.Logger;

/**
 * Created by GeorgeKiarie on 02/04/2016.
 */
public class FocusManualHuawei extends BaseFocusManual {
    public FocusManualHuawei(Parameters parameters, String maxValue, String MinValue, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, KEYS.HW_MANUAL_FOCUS_STEP_VALUE, KEYS.HW_VCM_END_VALUE, KEYS.HW_VCM_START_VALUE, KEYS.KEY_FOCUS_MODE_MANUAL, parametersHandler, (float) 10, 0);
    }

    public FocusManualHuawei(Parameters parameters, String value, int min, int max, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, value, min, max, manualFocusModeString, parametersHandler, step, manualFocusType);
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
            parameters.set(KEYS.HW_HWCAMERA_FLAG,KEYS.ON);
            parameters.set(KEYS.HW_MANUAL_FOCUS_MODE,KEYS.OFF);
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !parametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                parametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.set(KEYS.HW_HWCAMERA_FLAG,KEYS.ON);
            parameters.set(KEYS.HW_MANUAL_FOCUS_MODE,KEYS.ON);
            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set " + key_value + " to : " + stringvalues[currentInt]);
            parametersHandler.SetParametersToCamera(parameters);
        }
    }
}