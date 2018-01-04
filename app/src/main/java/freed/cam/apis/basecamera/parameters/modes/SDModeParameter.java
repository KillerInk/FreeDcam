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
import android.os.Build.VERSION_CODES;

import java.io.File;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.settings.SettingsManager;
import freed.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 */
public class SDModeParameter extends AbstractParameter
{
    public static final String internal = "Internal";
    public static final String external ="External";

    public SDModeParameter() {
        super(null);
    }

    @Override
    public void addEventListner(ParameterEvents eventListner) {
        super.addEventListner(eventListner);
    }

    @Override
    public boolean IsSupported()
    {
        try {
            if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP) {
                File file = new File(StringUtils.GetExternalSDCARD());
                return file.exists();
            }
            else
                return true;
        }
        catch (Exception ex)
        {
            return false;
        }

    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {

    }

    @Override
    public String GetStringValue()
    {
        if (SettingsManager.getInstance().GetWriteExternal())
            return external;
        else
            return internal;
    }

    @Override
    public String[] getStringValues() {
        return new String[] {internal, external};
    }

}
