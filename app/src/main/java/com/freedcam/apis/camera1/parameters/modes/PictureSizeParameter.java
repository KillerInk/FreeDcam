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

package com.freedcam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.apis.camera1.parameters.ParametersHandler;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 18.08.2014.
 */
public class PictureSizeParameter extends BaseModeParameter
{
    final String TAG = PictureSizeParameter.class.getSimpleName();
    public PictureSizeParameter(Parameters  parameters, I_CameraUiWrapper parameterChanged) {
        super(parameters, parameterChanged, "picture-size", "picture-size-values");
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set("picture-size" , valueToSet);

        try {
            ((ParametersHandler)cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);
            BackgroundValueHasChanged(valueToSet);
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }
    }
}
