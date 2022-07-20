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

package freed.cam.apis.camera2.parameters.manual;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

/**
 * Created by troop on 10.09.2015.
 */
public class BurstApi2 extends AbstractParameter
{
    int current = 1;


    public BurstApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper, SettingKeys.M_BURST);
        setViewState(ViewState.Visible);
        stringvalues = createStringArray(1,60,1);// new String[] {"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"};
    }


    @Override
    public int getIntValue() {
        return current;
    }

    @Override
    public String getStringValue() {
        if (current > stringvalues.length)
            return stringvalues[stringvalues.length-1];
        return stringvalues[current];
    }

    @Override
    public String[] getStringValues() {
        return stringvalues;
    }

    public void overwriteValues(int min, int max)
    {
        stringvalues = createStringArray(min,max,1);
        fireStringValuesChanged(stringvalues);
    }

    @Override
    public void setIntValue(int valueToSet, boolean setToCamera)
    {
        current = valueToSet;
        fireIntValueChanged(current);
    }
}
