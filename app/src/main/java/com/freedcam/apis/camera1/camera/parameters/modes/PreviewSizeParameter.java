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
 * Created by troop on 21.08.2014.
 */
public class PreviewSizeParameter extends BaseModeParameter
{
    private CameraHolder baseCameraHolder;
    final String TAG = PreviewSizeParameter.class.getSimpleName();

    public PreviewSizeParameter(Parameters parameters, CameraHolder parameterChanged)
    {
        super(parameters, parameterChanged, "preview-size", "preview-size-values");
        baseCameraHolder = parameterChanged;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        if (setToCam)
            baseCameraHolder.StopPreview();
        parameters.set(key_value, valueToSet);
        BackgroundValueHasChanged(valueToSet);
        try {
            baseCameraHolder.SetCameraParameters(parameters);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
        if (setToCam)
            baseCameraHolder.StartPreview();
    }

    @Override
    public String[] GetValues() {
        return parameters.get(key_values).split(",");
    }
}
