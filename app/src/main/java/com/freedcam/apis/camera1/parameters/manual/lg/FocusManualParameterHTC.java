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

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.apis.camera1.parameters.manual.BaseManualParameter;

import java.util.ArrayList;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterHTC extends BaseManualParameter
{
    private final String TAG =FocusManualParameterHTC.class.getSimpleName();

    public FocusManualParameterHTC(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = parameters.get(KEYS.MIN_FOCUS) != null && parameters.get(KEYS.MAX_FOCUS) != null;
        key_max_value = KEYS.MAX_FOCUS;
        key_value = KEYS.FOCUS;
        key_min_value = KEYS.MIN_FOCUS;
        parameters.set(key_value,"0");
        isVisible = isSupported;
        if (isSupported)
        {
            stringvalues = createStringArray(Integer.parseInt(parameters.get(key_min_value)),Integer.parseInt(parameters.get(key_max_value)),1);
        }
    }

    @Override
    protected String[] createStringArray(int min, int max, float step)
    {
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
        if(valueToSet != 0)
        {
            parameters.set(key_value, stringvalues[valueToSet]);
            ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            parameters.set(key_value, valueToSet+"");
            ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
    }

}
