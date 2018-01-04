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

package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class ParameterExternalShutter extends AbstractParameter
{
    private static final String VoLP = "Vol+";
    private static final String VoLM = "Vol-";
    private static final String Hook = "Hook";

    private final String[] values = {VoLP, VoLM, Hook};

    public ParameterExternalShutter()
    {
        super(null);
    }

    public boolean IsSupported()
    {
        return true;
    }

    public void SetValue(String valueToSet, boolean setToCamera)
    {
        SettingsManager.getInstance().setApiString(SettingsManager.SETTING_EXTERNALSHUTTER, valueToSet);
    }

    public String GetStringValue()
    {
        if (TextUtils.isEmpty(SettingsManager.getInstance().getApiString(SettingsManager.SETTING_EXTERNALSHUTTER)))
            return "Hook";
        else
            return SettingsManager.getInstance().getApiString(SettingsManager.SETTING_EXTERNALSHUTTER);
    }

    public String[] getStringValues() {
        return values;
    }
}
