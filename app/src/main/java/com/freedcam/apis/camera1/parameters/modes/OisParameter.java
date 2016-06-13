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

package com.freedcam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils.Devices;

/**
 * Created by troop on 26.05.2015.
 */
public class OisParameter extends BaseModeParameter {
    /**
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     * @param values
     */
    public OisParameter(Parameters parameters, CameraWrapperInterface cameraHolder, String values) {
        super(parameters, cameraHolder);
    }

    @Override
    public boolean IsSupported() {
        return cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2pro
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.p8lite
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI5;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3|| cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2pro)
            parameters.set(KEYS.LG_OIS, valueToSet);
        else if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI5)
            parameters.set("ois", valueToSet);
        else
            parameters.set("hw_ois_enable", valueToSet);
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
    }

    @Override
    public String GetValue() {
        return parameters.get(KEYS.LG_OIS);
    }

    @Override
    public String[] GetValues() {
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G3 || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G2pro)
        return new String[] {
                KEYS.LG_OIS_PREVIEW_CAPTURE,KEYS.LG_OIS_CAPTURE,KEYS.LG_OIS_VIDEO,KEYS.LG_OIS_CENTERING_ONLY, KEYS.LG_OIS_CENTERING_OFF
        };
        else if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.XiaomiMI5)
        {
            return new String[] {
                    KEYS.ENABLE,KEYS.DISABLE
            };
        }
        else
            return new String[] {
                    KEYS.ON,KEYS.OFF
            };
    }
}
