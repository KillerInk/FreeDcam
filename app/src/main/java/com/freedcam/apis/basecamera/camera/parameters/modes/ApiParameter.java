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

package com.freedcam.apis.basecamera.camera.parameters.modes;

import android.os.Build;

import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.DeviceUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class ApiParameter extends AbstractModeParameter
{
    private I_Activity i_activity;
    private final boolean DEBUG = false;
    private AppSettingsManager appSettingsManager;

    public ApiParameter(I_Activity i_activity,AppSettingsManager appSettingsManager) {
        super();
        this.i_activity = i_activity;
        this.appSettingsManager = appSettingsManager;
    }

    @Override
    public String[] GetValues()
    {
        if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) || DeviceUtils.IS(DeviceUtils.Devices.Htc_M10))
        {
            return new String[]{AppSettingsManager.API_SONY, AppSettingsManager.API_2, AppSettingsManager.API_1};
        }
        else {
            if (Build.VERSION.SDK_INT >= 21) {
                if (appSettingsManager.IsCamera2FullSupported().equals("true"))
                    return new String[]{AppSettingsManager.API_SONY, AppSettingsManager.API_2};
                else
                    return new String[]{AppSettingsManager.API_SONY, AppSettingsManager.API_1};
            } else
                return new String[]{AppSettingsManager.API_SONY, AppSettingsManager.API_1};
        }
    }

    @Override
    public String GetValue() {
        String ret = appSettingsManager.getCamApi();
        if (ret.equals(""))
            ret = AppSettingsManager.API_1;
        return ret;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        appSettingsManager.setCamApi(valueToSet);
        i_activity.SwitchCameraAPI(valueToSet);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}
