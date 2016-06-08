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

import com.freedcam.utils.AppSettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class ParameterExternalShutter extends AbstractModeParameter
{
    private static String VoLP = "Vol+";
    private static String VoLM = "Vol-";
    private static String Hook = "Hook";
    private AppSettingsManager appSettingsManager;

    private String[] values = {VoLP, VoLM, Hook};

    public ParameterExternalShutter(AppSettingsManager appSettingsManager)
    {
        this.appSettingsManager = appSettingsManager;
    }

    public boolean IsSupported()
    {
        return true;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_EXTERNALSHUTTER, valueToSet);
    }

    public String GetValue()
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER).equals(""))
            return "Hook";
        else
            return appSettingsManager.getString(AppSettingsManager.SETTING_EXTERNALSHUTTER);
    }

    public String[] GetValues() {
        return values;
    }
}
