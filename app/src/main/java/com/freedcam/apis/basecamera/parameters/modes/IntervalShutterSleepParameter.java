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

package com.freedcam.apis.basecamera.parameters.modes;

import android.content.Context;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.troop.freedcam.R;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalShutterSleepParameter extends AbstractModeParameter
{
    private String current = "1 sec";
    private I_CameraUiWrapper cameraUiWrapper;
    public IntervalShutterSleepParameter(I_CameraUiWrapper cameraUiWrapper) {
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera) {
        current = valueToSet;
    }

    @Override
    public String GetValue() {
        return current;
    }

    @Override
    public String[] GetValues() {
        return cameraUiWrapper.getContext().getResources().getStringArray(R.array.interval_shutter_sleep);
    }
}
