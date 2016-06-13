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
import com.freedcam.utils.DeviceUtils.Devices;

/**
 * Created by troop on 17.08.2014.
 */
public class FocusManualParameterLG extends BaseManualParameter
{
    private final String TAG =FocusManualParameterLG.class.getSimpleName();

    public FocusManualParameterLG(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        isSupported = true;
        isVisible = isSupported;
        if (isSupported)
        {
            int max = 0;
            step = 1;
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G4)
                max = 60;
            else
                max = 79;
            stringvalues = createStringArray(0,max, step);
        }

    }


    @Override
    public void SetValue(int valueToSet)
    {
        currentInt = valueToSet;
        if(valueToSet != 0)
        {
            if (!cameraUiWrapper.GetParameterHandler().FocusMode.GetValue().equals(KEYS.FOCUS_MODE_NORMAL)) {
                cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(KEYS.FOCUS_MODE_NORMAL, true);
            }
            parameters.set(KEYS.MANUALFOCUS_STEP, stringvalues[valueToSet]);
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
        else if (valueToSet == 0)
        {
            cameraUiWrapper.GetParameterHandler().FocusMode.SetValue(KEYS.AUTO, true);
        }


    }

    @Override
    public String GetStringValue()
    {
        if (cameraUiWrapper.GetParameterHandler().FocusMode.GetValue().equals(KEYS.AUTO))
            return KEYS.AUTO;
        else
            return GetValue()+"";
    }
}
