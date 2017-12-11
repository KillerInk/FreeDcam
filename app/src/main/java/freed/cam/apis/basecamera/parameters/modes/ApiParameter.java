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

import android.os.Build.VERSION;
import android.text.TextUtils;

import freed.ActivityInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingsManager;

/**
 * Created by troop on 21.07.2015.
 */
public class ApiParameter extends AbstractParameter
{
    private final ActivityInterface fragment_activityInterface;
    private final boolean DEBUG = false;

    public ApiParameter(ActivityInterface fragment_activityInterface) {
        this.fragment_activityInterface = fragment_activityInterface;
    }

    @Override
    public String[] getStringValues()
    {
        if (VERSION.SDK_INT >= 21)
        {
            if (SettingsManager.getInstance().hasCamera2Features())
                return new String[]{SettingsManager.API_SONY, SettingsManager.API_2, SettingsManager.API_1};
            else
                return new String[]{SettingsManager.API_SONY, SettingsManager.API_1};
        } else
            return new String[]{SettingsManager.API_SONY, SettingsManager.API_1};
    }

    @Override
    public String GetStringValue() {
        String ret = SettingsManager.getInstance().getCamApi();
        if (TextUtils.isEmpty(ret))
            ret = SettingsManager.API_1;
        return ret;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        SettingsManager.getInstance().setCamApi(valueToSet);
        fragment_activityInterface.SwitchCameraAPI(valueToSet);
    }

    @Override
    public boolean IsSupported() {
        return true;
    }
}
