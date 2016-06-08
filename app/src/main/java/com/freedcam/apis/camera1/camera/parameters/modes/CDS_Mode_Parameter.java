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

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.DeviceUtils.Devices;
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 12.04.2015.
 */
public class CDS_Mode_Parameter extends BaseModeParameter
{
    final String TAG = CDS_Mode_Parameter.class.getSimpleName();
    final String[] cds_values = {"auto", "on", "off"};
    public CDS_Mode_Parameter(Parameters parameters, CameraHolder cameraHolder, String value)
    {
        super(parameters, cameraHolder, "", "");
        try {
            String cds = parameters.get("cds-mode");
            if (cds != null && !cds.equals(""))
            {
                isSupported = true;
            }
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        if (!isSupported)
        {
            if (cameraHolder.appSettingsManager.getDevice() == Devices.ZTEADVIMX214
                    || cameraHolder.appSettingsManager.getDevice() == Devices.ZTE_ADV
                    || cameraHolder.appSettingsManager.getDevice() == Devices.ZTEADVIMX214
                    || cameraHolder.appSettingsManager.getDevice() == Devices.Htc_M9
                    || cameraHolder.appSettingsManager.getDevice() == Devices.LG_G4)
                isSupported = true;
        }
    }

    @Override
    public boolean IsSupported() {
        return isSupported;
    }

    @Override
    public String[] GetValues() {
        return cds_values;
    }

    @Override
    public String GetValue()
    {
        String cds = parameters.get("cds-mode");
        if (cds != null && !cds.equals(""))
            return cds;
        else
            return "off";
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set("cds-mode", valueToSet);
        try {
            cameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }
}
