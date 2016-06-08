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

package com.freedcam.apis.camera1.camera.parameters.manual;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 05.03.2016.
 */
public class BaseFocusManual extends BaseManualParameter
{
    static final String TAG = BaseFocusManual.class.getSimpleName();
    protected String manualFocusModeString;
    private int manualFocusType = 0;

    /**
     * checks if the key_value maxvalue and minvalues are contained in the cameraparameters
     * and creates depending on it the stringarray
     * NOTE:if super fails the parameter is unsupported
     * @param parameters
     * @param value
     * @param maxValue
     * @param MinValue
     * @param parametersHandler
     * @param step
     */
    public BaseFocusManual(Parameters parameters, String value, String maxValue, String MinValue, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, value, maxValue, MinValue, parametersHandler, step);
        this.manualFocusModeString = manualFocusModeString;
        this.manualFocusType = manualFocusType;
    }

    /**
     * this allows to hardcode devices wich support manual focus but the parameters are messed up.
     * @param parameters
     * @param value
     * @param min
     * @param max
     * @param manualFocusModeString
     * @param parametersHandler
     * @param step
     * @param manualFocusType
     */
    public BaseFocusManual(Parameters parameters, String value, int min, int max, String manualFocusModeString, ParametersHandler parametersHandler, float step, int manualFocusType) {
        super(parameters, value, "", "", parametersHandler, step);
        isSupported = true;
        isVisible = true;
        this.manualFocusModeString = manualFocusModeString;
        stringvalues = createStringArray(min,max,step);
        this.manualFocusType = manualFocusType;
    }


    @Override
    protected String[] createStringArray(int min, int max, float step) {
        ArrayList<String> ar = new ArrayList<>();
        ar.add(KEYS.AUTO);
        if (step == 0)
            step = 1;
        for (int i = min; i < max; i+=step)
        {
            ar.add(i+"");
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;

        if (valueToSet == 0)
        {
            parametersHandler.FocusMode.SetValue(KEYS.AUTO, true);
            Logger.d(TAG, "Set Focus to : auto");
        }
        else
        {
            if ((!manualFocusModeString.equals("") || manualFocusModeString == null)&& !parametersHandler.FocusMode.GetValue().equals(manualFocusModeString)) //do not set "manual" to "manual"
                parametersHandler.FocusMode.SetValue(manualFocusModeString, false);
            parameters.set(KEYS.KEY_MANUAL_FOCUS_TYPE, manualFocusType+"");

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            parametersHandler.SetParametersToCamera(parameters);
        }
    }

}
