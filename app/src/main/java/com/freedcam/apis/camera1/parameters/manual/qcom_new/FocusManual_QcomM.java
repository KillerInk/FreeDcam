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

package com.freedcam.apis.camera1.parameters.manual.qcom_new;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 27.04.2016.
 * manual-focus-modes=off,scale-mode,diopter-mode
 *
 * cur-focus-scale = 70
 *
 * max-focus-pos-dac=1023
 * max-focus-pos-diopter=10
 * max-focus-pos-index=1023
 * max-focus-pos-ratio=100
 */
public class FocusManual_QcomM extends BaseManualParameter
{
    private final String TAG = FocusManual_QcomM.class.getSimpleName();
    /**
     * @param parameters
     * @param cameraUiWrapper
     * @param step
     */
    public FocusManual_QcomM(Parameters parameters, CameraWrapperInterface cameraUiWrapper, float step)
    {
        super(parameters, KEYS.KEY_MANUAL_FOCUS_POSITION, KEYS.MAX_FOCUS_POS_RATIO, KEYS.MIN_FOCUS_POS_RATIO, cameraUiWrapper, (float) 1);
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
            parameters.set(KEYS.MANUAL_FOCUS, KEYS.OFF);
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
            cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(KEYS.AUTO, true);
            Logger.d(TAG, "Set Focusmode to : auto");
        }
        else
        {
            if (!cameraUiWrapper.GetParameterHandler().FocusMode.GetValue().equals(KEYS.KEY_FOCUS_MODE_MANUAL)) {//do not set "manual" to "manual"
                cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(KEYS.KEY_FOCUS_MODE_MANUAL, false);
                parameters.set(KEYS.MANUAL_FOCUS, KEYS.MANUAL_FOCUS_SCALE_MODE);
                ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
            }

            parameters.set(key_value, stringvalues[currentInt]);
            Logger.d(TAG, "Set "+ key_value +" to : " + stringvalues[currentInt]);
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
    }
}
