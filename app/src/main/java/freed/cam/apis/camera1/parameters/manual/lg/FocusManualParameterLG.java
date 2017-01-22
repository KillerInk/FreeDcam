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

package freed.cam.apis.camera1.parameters.manual.lg;


import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.utils.DeviceUtils.Devices;

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
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G4 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_V20)
                max = 100;
            else
                max = 79;
            if (cameraUiWrapper.GetAppSettingsManager().manualFocus.getValues().length < 2) {
                stringvalues = createStringArray(0, max, step);
                cameraUiWrapper.GetAppSettingsManager().manualFocus.setValues(stringvalues);
            }
            else
                stringvalues = cameraUiWrapper.GetAppSettingsManager().manualFocus.getValues();
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

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }
}
