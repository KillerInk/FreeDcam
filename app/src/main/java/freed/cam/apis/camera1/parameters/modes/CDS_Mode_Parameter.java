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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.utils.DeviceUtils.Devices;

/**
 * Created by Ingo on 12.04.2015.
 */
public class CDS_Mode_Parameter extends BaseModeParameter
{
    final String TAG = CDS_Mode_Parameter.class.getSimpleName();
    final String[] cds_values = {KEYS.AUTO, KEYS.ON, KEYS.OFF};
    public CDS_Mode_Parameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper)
    {
        super(parameters, cameraUiWrapper);
        try {
            String cds = parameters.get("cds-mode");
            if (cds != null && !cds.equals(""))
            {
                isSupported = true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if (!isSupported)
        {
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.Htc_M9
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.LG_G4)
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
            ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
