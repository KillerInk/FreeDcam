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
import com.freedcam.utils.Logger;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter
{
    final String TAG = ExposureLockParameter.class.getSimpleName();
    public ExposureLockParameter(Parameters parameters, CameraHolder parameterChanged, String values) {
        super(parameters, parameterChanged, "", "");
    }

    @Override
    public boolean IsSupported() {
        return parameters.isAutoExposureLockSupported();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (parameters.isAutoExposureLockSupported())
            parameters.setAutoExposureLock(Boolean.parseBoolean(valueToSet));
        try {
            cameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }

    @Override
    public String GetValue()
    {

        return parameters.getAutoExposureLock()+"";
    }

    @Override
    public String[] GetValues() {
        return new String[]{"true", "false"};
    }

    @Override
    public void BackgroundValueHasChanged(String value) {
            super.BackgroundValueHasChanged(value);
    }
}
