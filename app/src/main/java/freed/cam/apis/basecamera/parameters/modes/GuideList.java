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

//defcomg was here

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.AppSettingsManager;

/**
 * Created by George on 1/19/2015.
 */
public class GuideList extends AbstractParameter
{
    public GuideList() {
        super();
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        AppSettingsManager.getInstance().guide.set(valueToSet);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue()
    {
        return AppSettingsManager.getInstance().guide.get();
    }

    @Override
    public String[] getStringValues() {
        return AppSettingsManager.getInstance().guide.getValues();
    }



}
