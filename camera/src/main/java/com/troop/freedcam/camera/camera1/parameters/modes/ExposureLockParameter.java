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

package com.troop.freedcam.camera.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.camera1.parameters.ParametersHandler;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockParameter extends BaseModeParameter
{
    final String TAG = ExposureLockParameter.class.getSimpleName();
    public ExposureLockParameter(Parameters parameters, CameraControllerInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper, SettingKeys.ExposureLock);
        try {
            if (parameters.isAutoExposureLockSupported())
                setViewState(ViewState.Visible);
        }
        catch (NullPointerException ex)
        {
            setViewState(ViewState.Hidden);
        }

    }

    @Override
    public void setValue(String valueToSet, boolean setToCam)
    {
        if (parameters.isAutoExposureLockSupported())
            parameters.setAutoExposureLock(Boolean.parseBoolean(valueToSet));

        if (setToCam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        fireStringValueChanged(valueToSet);
    }

    @Override
    public String GetStringValue()
    {

        return parameters.getAutoExposureLock()+"";
    }

    @Override
    public String[] getStringValues() {
        return new String[]{ContextApplication.getStringFromRessources(R.string.true_), ContextApplication.getStringFromRessources(R.string.false_)};
    }

}
