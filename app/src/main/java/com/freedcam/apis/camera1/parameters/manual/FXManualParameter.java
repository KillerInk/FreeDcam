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
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.Logger;

public class FXManualParameter extends BaseManualParameter {

    public FXManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
    }

    @Override
    public boolean IsSupported()
    {
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214)
        {
            isSupported = true;
            isVisible = true;
            stringvalues = createStringArray(0,38,1);
            return true;
        }
        else
            return false;

    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214);
                i = 0;
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        parameters.set(KEYS.MORPHO_EFFECT_TYPE, String.valueOf(valueToSet));
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

    }

}